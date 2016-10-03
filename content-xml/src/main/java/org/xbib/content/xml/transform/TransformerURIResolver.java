package org.xbib.content.xml.transform;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

/**
 * URI resolver for Transformer.
 */
public class TransformerURIResolver implements URIResolver, Closeable {

    private static final Logger logger = Logger.getLogger(TransformerURIResolver.class.getName());

    private List<InputStream> inputStreams = new LinkedList<>();

    private List<String> bases = new LinkedList<>();

    private ClassLoader classLoader;

    public TransformerURIResolver() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public TransformerURIResolver(String... bases) {
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.bases.addAll(Arrays.asList(bases));
    }

    public TransformerURIResolver setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    /**
     * @param hrefAttribute An href attribute, which may be relative or absolute
     * @param base The base URI against which the first argument will be made absolute if the absolute URI is required
     * @return the souce
     * @throws TransformerException if resolve fails
     */
    @Override
    public Source resolve(String hrefAttribute, String base) throws TransformerException {
        String href = hrefAttribute;
        InputStream in = null;
        URL url = null;
        try {
            URI uri = URI.create(href);
            // relative href?
            if (!uri.isAbsolute() && base != null) {
                url = new URL(new URL(base), href);
                href = url.toURI().getRawSchemeSpecificPart(); // drop scheme
            }
        } catch (MalformedURLException | URISyntaxException e) {
            throw new TransformerException(e);
        }
        String systemId = href;
        if (url != null) {
            try {
                in = url.openStream();
            } catch (IOException e) {
                logger.log(Level.WARNING, "error while opening stream", e);
            }
        }
        if (in == null) {
            try {
                url = classLoader.getResource(href);
                if (url != null) {
                    systemId = url.toExternalForm();
                    in = url.openStream();
                } else {
                    systemId = href;
                    in = classLoader.getResourceAsStream(href);
                    if (in == null) {
                        if (bases.isEmpty()) {
                            systemId = href;
                        } else {
                            for (String s : bases) {
                                systemId = s + "/" + href;
                                in = classLoader.getResourceAsStream(systemId);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new TransformerException("I/O error", e);
            }
        }
        if (in == null) {
            throw new TransformerException("href could not be resolved: " + href);
        }
        try {
            XMLReader reader = XMLReaderFactory.createXMLReader();
            inputStreams.add(in);
            SAXSource source = new SAXSource(reader, new InputSource(in));
            source.setSystemId(systemId);
            return source;
        } catch (SAXException e) {
            throw new TransformerException("no XML reader for SAX source in URI resolving for:" + href, e);
        }
    }

    @Override
    public void close() throws IOException {
        for (InputStream in : inputStreams) {
            in.close();
        }
    }
}
