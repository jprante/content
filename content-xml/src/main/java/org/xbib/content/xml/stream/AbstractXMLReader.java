package org.xbib.content.xml.stream;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * Abstract base class for SAX <code>XMLReader</code> implementations.
 * Contains properties as defined in {@link XMLReader}, and does not recognize any features.
 */
abstract class AbstractXMLReader implements XMLReader {

    private DTDHandler dtdHandler;

    private ContentHandler contentHandler;

    private EntityResolver entityResolver;

    private ErrorHandler errorHandler;

    private LexicalHandler lexicalHandler;

    @Override
    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    @Override
    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    @Override
    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }

    @Override
    public void setDTDHandler(DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }

    @Override
    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    @Override
    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    protected LexicalHandler getLexicalHandler() {
        return lexicalHandler;
    }

    /**
     * Throws a <code>SAXNotRecognizedException</code> exception.
     *
     * @throws org.xml.sax.SAXNotRecognizedException always
     */
    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(name);
    }

    /**
     * Throws a <code>SAXNotRecognizedException</code> exception.
     *
     * @throws SAXNotRecognizedException always
     */
    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(name);
    }

    /**
     * Throws a <code>SAXNotRecognizedException</code> exception when the given property does not signify a lexical
     * handler. The property name for a lexical handler is <code>http://xml.org/sax/properties/lexical-handler</code>.
     */
    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            return lexicalHandler;
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    /**
     * Throws a <code>SAXNotRecognizedException</code> exception when the given property does not signify a lexical
     * handler. The property name for a lexical handler is <code>http://xml.org/sax/properties/lexical-handler</code>.
     */
    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            lexicalHandler = (LexicalHandler) value;
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }
}
