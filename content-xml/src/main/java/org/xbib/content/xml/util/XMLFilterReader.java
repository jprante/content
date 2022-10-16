package org.xbib.content.xml.util;

import javax.xml.XMLConstants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.XMLFilterImpl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * A XML reader which is also a filter.
 * Does evaluate namespaces and does not validate or import external entities or document type definitions.
 */
public class XMLFilterReader extends XMLFilterImpl {

    private final SAXParser parser;

    public XMLFilterReader() {
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setFeature("http://xml.org/sax/features/namespaces", true);
            parserFactory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            parserFactory.setFeature("http://xml.org/sax/features/validation", false);
            parserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            parserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            parserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            parserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            parserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            parserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            parserFactory.setXIncludeAware(false);
            parser = parserFactory.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Saxon uses setFeature, so we override it here, otherwise XmlFilterImpl will bark.
     *
     * @param name  the name
     * @param value the value
     * @throws org.xml.sax.SAXNotRecognizedException if SAX is not recognized
     * @throws org.xml.sax.SAXNotSupportedException if SAX is not supported
     */
    @Override
    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        // accept all setFeature calls, but do nothing
    }

    @Override
    public void parse(InputSource input) throws SAXException, IOException {
        parser.parse(input,  new XMLFilterBridge(this));
    }
}
