package org.xbib.content.xml.util;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 *
 */
public class XMLFilterBridge extends DefaultHandler {

    private final XMLFilterImpl filter;

    XMLFilterBridge(XMLFilterImpl filter) {
        this.filter = filter;
    }

    @Override
    public void setDocumentLocator(Locator lctr) {
        filter.setDocumentLocator(lctr);
    }

    @Override
    public void startDocument() throws SAXException {
        filter.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        filter.endDocument();
    }

    @Override
    public void startPrefixMapping(String string, String string1) throws SAXException {
        filter.startPrefixMapping(string, string1);
    }

    @Override
    public void endPrefixMapping(String string) throws SAXException {
        filter.endPrefixMapping(string);
    }

    @Override
    public void startElement(String string, String string1, String string2, Attributes atrbts) throws SAXException {
        filter.startElement(string, string1, string2, atrbts);
    }

    @Override
    public void endElement(String string, String string1, String string2) throws SAXException {
        filter.endElement(string, string1, string2);
    }

    @Override
    public void characters(char[] chars, int i, int i1) throws SAXException {
        filter.characters(chars, i, i1);
    }

    @Override
    public void ignorableWhitespace(char[] chars, int i, int i1) throws SAXException {
        filter.ignorableWhitespace(chars, i, i1);
    }

    @Override
    public void processingInstruction(String string, String string1) throws SAXException {
        filter.processingInstruction(string, string1);
    }

    @Override
    public void skippedEntity(String string) throws SAXException {
        filter.skippedEntity(string);
    }
}
