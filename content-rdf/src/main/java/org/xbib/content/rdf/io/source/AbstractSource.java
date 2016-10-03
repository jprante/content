package org.xbib.content.rdf.io.source;

import org.xbib.content.rdf.io.sink.Sink;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

abstract class AbstractSource<S extends Sink> {

    protected final S sink;

    protected AbstractSource(S sink) {
        this.sink = sink;
    }

    protected abstract void process(Reader reader, String mimeType, String baseUri) throws IOException;

    protected abstract void process(InputStream inputStream, String mimeType, String baseUri) throws IOException;

}
