package org.xbib.content.rdf.io.source;

import org.xbib.content.rdf.io.sink.CharSink;
import org.xbib.content.rdf.io.sink.Sink;
import org.xbib.content.rdf.io.sink.XmlSink;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Simple pipeline managing wrapper. Automatically instantiates source appropriate for specified sink.
 * Provides processing and setup methods.
 * @param <S> sink type
 */
public final class StreamProcessor<S extends Sink> extends BaseStreamProcessor {

    private final Sink sink;

    private final AbstractSource<S> source;

    /**
     * Instantiates stream processor for pipe starting with specified sink.
     *
     * @param sink pipe's input
     */
    @SuppressWarnings("unchecked")
    public StreamProcessor(CharSink sink) {
        this.sink = sink;
        this.source = (AbstractSource<S>) new CharSource(sink);
    }

    @SuppressWarnings("unchecked")
    public StreamProcessor(XmlSink sink) {
        this.sink = sink;
        this.source = (AbstractSource<S>) new XmlSource(sink);
    }

    @Override
    public void processInternal(InputStream inputStream, String mimeType, String baseUri) throws IOException {
        source.process(inputStream, mimeType, baseUri);
    }

    @Override
    protected void startStream() throws IOException {
        sink.startStream();
    }

    @Override
    protected void endStream() throws IOException {
        sink.endStream();
    }

    @Override
    public void processInternal(Reader reader, String mimeType, String baseUri) throws IOException {
        source.process(reader, mimeType, baseUri);
    }

    public void setReader(XMLReader xmlReader) throws SAXException {
        ((XmlSource) source).setXmlReader(xmlReader);
    }

}
