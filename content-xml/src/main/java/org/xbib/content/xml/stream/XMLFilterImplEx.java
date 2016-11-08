package org.xbib.content.xml.stream;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Extension to XMLFilterImpl that implements LexicalHandler.
 */
public class XMLFilterImplEx extends XMLFilterImpl implements LexicalHandler {

    protected LexicalHandler lexicalHandler;

    protected boolean namespacePrefixes;

    public boolean getNamespacePrefixes() {
        return namespacePrefixes;
    }

    public void setNamespacePrefixes(boolean v) {
        namespacePrefixes = v;
    }

    /**
     * Get the lexical event handler.
     *
     * @return The current lexical handler, or null if none was set.
     */
    public LexicalHandler getLexicalHandler() {
        return lexicalHandler;
    }

    /**
     * Set the lexical event handler.
     *
     * @param handler the new lexical handler
     */
    public void setLexicalHandler(LexicalHandler handler) {
        lexicalHandler = handler;
    }

    @Override
    public void startDTD(String name, String publicId, String systemId)
            throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.startDTD(name, publicId, systemId);
        }
    }

    @Override
    public void endDTD() throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endDTD();
        }
    }

    @Override
    public void startEntity(String name) throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.startEntity(name);
        }
    }

    @Override
    public void endEntity(String name) throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endEntity(name);
        }
    }

    @Override
    public void startCDATA() throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.startCDATA();
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endCDATA();
        }
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.comment(ch, start, length);
        }
    }
}
