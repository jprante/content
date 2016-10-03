package org.xbib.content.xml.stream;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;

/**
 * A JAXP {@link javax.xml.transform.Source} implementation that wraps
 * the specified {@link javax.xml.stream.XMLStreamReader} or
 * {@link javax.xml.stream.XMLEventReader} for use by applications that
 * expext a {@link javax.xml.transform.Source}.
 * The fact that StAXSource derives from SAXSource is an implementation
 * detail. Thus in general applications are strongly discouraged from
 * accessing methods defined on SAXSource. In particular:
 * <ul>
 * <li> The setXMLReader and setInputSource methods shall never be called.
 * <li> The XMLReader object obtained by the getXMLReader method shall
 * be used only for parsing the InputSource object returned by
 * the getInputSource method.</li>
 * <li> The InputSource object obtained by the getInputSource method shall
 * be used only for being parsed by the XMLReader object returned by
 * the getXMLReader method.</li>
 * </ul>
 * Example:
 * <pre>
 * // create a StAXSource
 * XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new FileReader(args[0]));
 * Source staxSource = new StAXSource(reader);
 *
 * // createa StreamResult
 * Result streamResult = new StreamResult(System.out);
 *
 * // run the transform
 * TransformerFactory.newInstance().newTransformer().transform(staxSource, streamResult);
 * </pre>
 */
public class StaxSource extends SAXSource {

    // SAX allows ContentHandler to be changed during the parsing,
    // but JAXB doesn't. So this repeater will sit between those
    // two components.
    private XMLFilterImpl repeater = new XMLFilterImpl();

    /**
     * Creates a new {@link javax.xml.transform.Source} for the given
     * {@link XMLStreamReader}.
     *
     * The XMLStreamReader must be pointing at either a
     * {@link javax.xml.stream.XMLStreamConstants#START_DOCUMENT} or
     * {@link javax.xml.stream.XMLStreamConstants#START_ELEMENT} event.
     *
     * @param reader XMLStreamReader that will be exposed as a Source
     * @param eagerQuit eager quit
     * @throws IllegalArgumentException iff the reader is null
     * @throws IllegalStateException    iff the reader is not pointing at either a
     *                                  START_DOCUMENT or START_ELEMENT event
     */
    public StaxSource(XMLStreamReader reader, boolean eagerQuit) {
        if (reader == null) {
            throw new IllegalArgumentException();
        }

        int eventType = reader.getEventType();
        if (!(eventType == XMLStreamConstants.START_DOCUMENT)
                && !(eventType == XMLStreamConstants.START_ELEMENT)) {
            throw new IllegalStateException();
        }

        final XMLStreamReaderToContentHandler reader1 = new XMLStreamReaderToContentHandler(reader, repeater, eagerQuit);

        XMLReader pseudoParser = new XMLReader() {
            private LexicalHandler lexicalHandler;
            // we will store this value but never use it by ourselves.
            private EntityResolver entityResolver;
            private DTDHandler dtdHandler;
            private ErrorHandler errorHandler;

            @Override
            public boolean getFeature(String name) throws SAXNotRecognizedException {
                throw new SAXNotRecognizedException(name);
            }

            @Override
            public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
                throw new SAXNotRecognizedException(name);
            }

            @Override
            public Object getProperty(String name) throws SAXNotRecognizedException {
                if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
                    return lexicalHandler;
                }
                throw new SAXNotRecognizedException(name);
            }

            @Override
            public void setProperty(String name, Object value) throws SAXNotRecognizedException {
                if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
                    this.lexicalHandler = (LexicalHandler) value;
                    return;
                }
                throw new SAXNotRecognizedException(name);
            }

            @Override
            public EntityResolver getEntityResolver() {
                return entityResolver;
            }

            @Override
            public void setEntityResolver(EntityResolver resolver) {
                this.entityResolver = resolver;
            }

            @Override
            public DTDHandler getDTDHandler() {
                return dtdHandler;
            }

            @Override
            public void setDTDHandler(DTDHandler handler) {
                this.dtdHandler = handler;
            }

            @Override
            public ContentHandler getContentHandler() {
                return repeater.getContentHandler();
            }

            @Override
            public void setContentHandler(ContentHandler handler) {
                repeater.setContentHandler(handler);
            }

            @Override
            public ErrorHandler getErrorHandler() {
                return errorHandler;
            }

            @Override
            public void setErrorHandler(ErrorHandler handler) {
                this.errorHandler = handler;
            }

            @Override
            public void parse(InputSource input) throws SAXException {
                parse();
            }

            @Override
            public void parse(String systemId) throws SAXException {
                parse();
            }

            public void parse() throws SAXException {
                // parses from a StAX reader and generates SAX events which
                // go through the repeater and are forwarded to the appropriate
                // component
                try {
                    reader1.bridge();
                } catch (XMLStreamException e) {
                    // wrap it in a SAXException
                    SAXParseException se =
                            new SAXParseException(
                                    e.getMessage(),
                                    null,
                                    null,
                                    e.getLocation().getLineNumber(),
                                    e.getLocation().getColumnNumber(),
                                    e);

                    // if the consumer sets an error handler, it is our responsibility
                    // to notify it.
                    if (errorHandler != null) {
                        errorHandler.fatalError(se);
                    }

                    // this is a fatal error. Even if the error handler
                    // returns, we will abort anyway.
                    throw se;

                }
            }
        };
        super.setXMLReader(pseudoParser);
        // pass a dummy InputSource. We don't care
        super.setInputSource(new InputSource());
    }

}
