package org.xbib.content.xml.transform;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

/**
 * A pool of precompiled XSLT stylesheets ({@link javax.xml.transform.Templates}).
 */
public final class StylesheetPool {

    /**
     * A map of precompiled stylesheets ({@link javax.xml.transform.Templates} objects).
     */
    private final Map<String, Templates> stylesheets = new ConcurrentHashMap<>();

    /**
     * @param transformerFactory transformer factory
     * @return returns the identity transformer handler.
     * @throws TransformerConfigurationException if handler can not created
     */
    public TransformerHandler getIdentityTransformerHandler(SAXTransformerFactory transformerFactory)
            throws TransformerConfigurationException {
        return transformerFactory.newTransformerHandler();
    }

    public boolean hasTemplate(StreamSource source) {
        return stylesheets.containsKey(source.getSystemId());
    }

    /**
     * Retrieves a previously stored template, if available.
     * @param systemId system ID
     * @return templates
     */
    public Templates getTemplate(String systemId) {
        return stylesheets.get(systemId);
    }

    /**
     * Create a template, add to the pool if necessary. Addition is quite costly
     * as it replaces the internal {@link #stylesheets} {@link java.util.HashMap}.
     * @param transformerFactory SAX transformer factory
     * @param source SAX source
     * @return templates
     * @throws TransformerConfigurationException if transformation fails
     */
    public Templates newTemplates(SAXTransformerFactory transformerFactory, Source source)
            throws TransformerConfigurationException {
        String systemId = source.getSystemId();
        Templates template = stylesheets.get(systemId);
        if (template == null) {
            template = transformerFactory.newTemplates(source);
            stylesheets.put(systemId, template);
        }
        return template;
    }

    /**
     * Return a new {@link javax.xml.transform.sax.TransformerHandler} based on a given precompiled
     * {@link javax.xml.transform.Templates}.
     * @param transformerFactory transformer factory
     * @param template template
     * @return transformer handler
     * @throws TransformerConfigurationException if transformer configuration fails
     */
    public TransformerHandler newTransformerHandler(SAXTransformerFactory transformerFactory, Templates template)
            throws TransformerConfigurationException {
        final TransformerHandler handler = transformerFactory.newTransformerHandler(template);
        /*
         * We want to raise transformer exceptions on <xml:message terminate="true">, so
         * we add a custom listener. Also, various XSLT processors react in different ways
         * to transformation errors -- some of them report error as recoverable, some of
         * them report error as unrecoverable.
         */
        handler.getTransformer().setErrorListener(new TransformerErrorListener());
        return handler;
    }
}
