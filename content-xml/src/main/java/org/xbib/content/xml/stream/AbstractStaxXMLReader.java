package org.xbib.content.xml.stream;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/**
 * Abstract base class for SAX <code>XMLReader</code> implementations that use StAX as a basis.
 */
abstract class AbstractStaxXMLReader extends AbstractXMLReader {

    private static final String NAMESPACES_FEATURE_NAME = "http://xml.org/sax/features/namespaces";

    private static final String NAMESPACE_PREFIXES_FEATURE_NAME = "http://xml.org/sax/features/namespace-prefixes";

    private static final String IS_STANDALONE_FEATURE_NAME = "http://xml.org/sax/features/is-standalone";

    private boolean namespacesFeature = true;

    private boolean namespacePrefixesFeature = false;

    private Boolean isStandalone;


    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(NAMESPACES_FEATURE_NAME)) {
            return this.namespacesFeature;
        } else if (name.equals(NAMESPACE_PREFIXES_FEATURE_NAME)) {
            return this.namespacePrefixesFeature;
        } else if (name.equals(IS_STANDALONE_FEATURE_NAME)) {
            if (this.isStandalone != null) {
                return this.isStandalone;
            } else {
                throw new SAXNotSupportedException("startDocument() callback not completed yet");
            }
        } else {
            return super.getFeature(name);
        }
    }

    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(NAMESPACES_FEATURE_NAME)) {
            this.namespacesFeature = value;
        } else if (name.equals(NAMESPACE_PREFIXES_FEATURE_NAME)) {
            this.namespacePrefixesFeature = value;
        } else {
            super.setFeature(name, value);
        }
    }

    protected void setStandalone(boolean standalone) {
        this.isStandalone = standalone;
    }

    /**
     * Indicates whether the SAX feature <code>http://xml.org/sax/features/namespaces</code> is turned on.
     * @return true if namespaces are featured
     */
    protected boolean hasNamespacesFeature() {
        return this.namespacesFeature;
    }

    /**
     * Indicates whether the SAX feature <code>http://xml.org/sax/features/namespaces-prefixes</code> is turned on.
     * @return true if namespace prefixes are featured
     */
    protected boolean hasNamespacePrefixesFeature() {
        return this.namespacePrefixesFeature;
    }

    /**
     * Sett the SAX <code>Locator</code> based on the given StAX <code>Location</code>.
     *
     * @param location the location
     * @see ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    protected void setLocator(Location location) {
        if (getContentHandler() != null) {
            getContentHandler().setDocumentLocator(new StaxLocator(location));
        }
    }

    /**
     * Convert a <code>QName</code> to a qualified name, as used by DOM and SAX.
     * The returned string has a format of <code>prefix:localName</code> if the
     * prefix is set, or just <code>localName</code> if not.
     *
     * @param qName the <code>QName</code>
     * @return the qualified name
     */
    protected String toQualifiedName(QName qName) {
        String prefix = qName.getPrefix();
        if (prefix.length() > 0) {
            return qName.getLocalPart();
        } else {
            return prefix + ":" + qName.getLocalPart();
        }
    }


    /**
     * Parse the StAX XML reader passed at construction-time.
     * <p><b>NOTE:</b>: The given <code>InputSource</code> is not read, but ignored.
     *
     * @param ignored is ignored
     * @throws SAXException a SAX exception, possibly wrapping a <code>XMLStreamException</code>
     */
    @Override
    public final void parse(InputSource ignored) throws SAXException {
        parse();
    }

    /**
     * Parse the StAX XML reader passed at construction-time.
     * <p><b>NOTE:</b>: The given system identifier is not read, but ignored.
     *
     * @param ignored is ignored
     * @throws SAXException A SAX exception, possibly wrapping a <code>XMLStreamException</code>
     */
    @Override
    public final void parse(String ignored) throws SAXException {
        parse();
    }

    private void parse() throws SAXException {
        try {
            parseInternal();
        } catch (XMLStreamException ex) {
            Locator locator = null;
            if (ex.getLocation() != null) {
                locator = new StaxLocator(ex.getLocation());
            }
            SAXParseException saxException = new SAXParseException(ex.getMessage(), locator, ex);
            if (getErrorHandler() != null) {
                getErrorHandler().fatalError(saxException);
            } else {
                throw saxException;
            }
        }
    }

    /**
     * Template-method that parses the StAX reader passed at construction-time.
     * @throws SAXException A SAX exception, possibly wrapping a <code>XMLStreamException</code>
     * @throws XMLStreamException if XML stream can not be processed
     */
    protected abstract void parseInternal() throws SAXException, XMLStreamException;

    /**
     * Implementation of the Locator interface that is based on a StAX Location.
     *
     * @see Locator
     * @see Location
     */
    private static class StaxLocator implements Locator {

        private Location location;

        StaxLocator(Location location) {
            this.location = location;
        }

        @Override
        public String getPublicId() {
            return location.getPublicId();
        }

        @Override
        public String getSystemId() {
            return location.getSystemId();
        }

        @Override
        public int getLineNumber() {
            return location.getLineNumber();
        }

        @Override
        public int getColumnNumber() {
            return location.getColumnNumber();
        }
    }

}
