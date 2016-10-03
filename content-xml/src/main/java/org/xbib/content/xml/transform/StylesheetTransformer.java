package org.xbib.content.xml.transform;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Style sheet transformer.
 */
public class StylesheetTransformer implements Closeable {

    private static final StylesheetPool pool = new StylesheetPool();

    private final Map<String, Object> parameters = new HashMap<String, Object>();

    private SAXTransformerFactory transformerFactory;

    private URIResolver resolver;

    private Source source;

    private Result result;

    public StylesheetTransformer setPath(String... path) {
        if (transformerFactory == null) {
            transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
        }
        transformerFactory.setErrorListener(new StylesheetErrorListener());
        setResolver(path == null ? new TransformerURIResolver() : new TransformerURIResolver(path));
        return this;
    }

    public StylesheetTransformer setResolver(URIResolver resolver) {
        this.resolver = resolver;
        if (transformerFactory == null) {
            transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
        }
        transformerFactory.setURIResolver(resolver);
        return this;
    }

    public StylesheetTransformer addParameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public StylesheetTransformer setSource(Reader reader) {
        this.source = new StreamSource(reader);
        return this;
    }

    public StylesheetTransformer setSource(InputSource source) {
        this.source = new SAXSource(source);
        return this;
    }

    public StylesheetTransformer setSource(Source source) {
        this.source = source;
        return this;
    }

    public StylesheetTransformer setSource(XMLReader reader, InputSource in) {
        this.source = new SAXSource(reader, in);
        return this;
    }

    public StylesheetTransformer setSource(XMLReader reader, InputSource in, String xsl, ContentHandler handler)
            throws TransformerException {
        if (transformerFactory == null) {
            transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
        }
        XMLFilter filter = transformerFactory.newXMLFilter(resolver.resolve(xsl, null));
        filter.setParent(reader);
        filter.setContentHandler(handler);
        this.source = new SAXSource(filter, in);
        return this;
    }

    public StylesheetTransformer setResult(Writer result) {
        this.result = new StreamResult(result);
        return this;
    }

    public StylesheetTransformer setResult(OutputStream result) {
        this.result = new StreamResult(result);
        return this;
    }

    public StylesheetTransformer setResult(Result result) {
        this.result = result;
        return this;
    }

    public void transform() throws TransformerException {
        if (source == null) {
            return;
        }
        if (result == null) {
            return;
        }
        if (transformerFactory == null) {
            transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
        }
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);
    }

    /**
     * Transform through a sequence of XSL style sheets.
     *
     * @param xsl sequence of XSL style sheets
     * @throws TransformerException is transformation fails
     */
    public void transform(Iterable<String> xsl) throws TransformerException {
        transform(null, xsl);
    }

    /**
     * Transform through a sequence of XSL style sheets.
     *
     * @param base base
     * @param xsl sequence of XSL style sheets
     * @throws TransformerException is transformation fails
     */
    public void transform(String base, Iterable<String> xsl) throws TransformerException {
        if (source == null) {
            return;
        }
        if (result == null) {
            return;
        }
        if (xsl == null) {
            return;
        }
        if (transformerFactory == null) {
            transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
        }
        Transformer transformer = transformerFactory.newTransformer();
        List<TransformerHandler> handlers = new LinkedList<TransformerHandler>();
        for (String s : xsl) {
            Templates t = pool.newTemplates(transformerFactory, resolver.resolve(s, base));
            TransformerHandler h = pool.newTransformerHandler(transformerFactory, t);
            for (Map.Entry<String, Object> me : parameters.entrySet()) {
                if (me.getValue() != null) {
                    h.getTransformer().setParameter(me.getKey(), me.getValue());
                }
            }
            handlers.add(h);
        }
        Result r = result;
        ListIterator<TransformerHandler> it = handlers.listIterator(handlers.size());
        while (it.hasPrevious()) {
            TransformerHandler h = it.previous();
            h.setResult(r);
            r = new SAXResult(h);
        }
        transformer.transform(source, r);
    }

    @Override
    public void close() throws IOException {
        if (resolver != null && resolver instanceof TransformerURIResolver) {
            TransformerURIResolver transformerURIResolver = (TransformerURIResolver) resolver;
            transformerURIResolver.close();
        }
    }

}
