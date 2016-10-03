package org.xbib.content.rdf;

import org.xbib.content.io.BytesReference;
import org.xbib.content.io.BytesStreamOutput;
import org.xbib.content.resource.IRI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * RDF content builder.
 *
 * @param <P> type parameter
 */
public class RdfContentBuilder<P extends RdfContentParams> implements RdfContentGenerator<P> {

    private final RdfContentGenerator<P> generator;

    private final OutputStream out;

    private IRI subject;

    public RdfContentBuilder(RdfContent<P> rdfContent, P rdfParams) throws IOException {
        this(rdfContent, rdfParams, new BytesStreamOutput());
    }

    public RdfContentBuilder(RdfContent<P> rdfContent, P rdfContentParams, OutputStream out) throws IOException {
        this.out = out;
        this.generator = rdfContent.createGenerator(out);
        this.generator.setParams(rdfContentParams);
    }

    @Override
    public P getParams() {
        return generator.getParams();
    }

    @Override
    public RdfContentBuilder<P> setParams(P rdfContentParams) {
        generator.setParams(rdfContentParams);
        return this;
    }

    @Override
    public void flush() throws IOException {
        generator.flush();
    }

    @Override
    public void close() throws IOException {
        generator.close();
    }

    public BytesReference bytes() throws IOException {
        close();
        return ((BytesStreamOutput) out).bytes();
    }

    public InputStream streamInput() throws IOException {
        return bytes().streamInput();
    }

    public String string() throws IOException {
        return bytes().toUtf8();
    }

    @Override
    public RdfContentBuilder<P> startStream() throws IOException {
        generator.startStream();
        return this;
    }

    @Override
    public RdfContentBuilder<P> endStream() throws IOException {
        generator.endStream();
        return this;
    }

    @Override
    public RdfContentBuilder<P> setBaseUri(String baseUri) {
        generator.startPrefixMapping("", baseUri);
        return this;
    }

    @Override
    public RdfContentBuilder<P> startPrefixMapping(String prefix, String uri) {
        generator.startPrefixMapping(prefix, uri);
        return this;
    }

    @Override
    public RdfContentBuilder<P> endPrefixMapping(String prefix) {
        generator.endPrefixMapping(prefix);
        return this;
    }

    @Override
    public RdfContentBuilder<P> receive(IRI identifier) throws IOException {
        this.subject = identifier;
        generator.receive(identifier);
        return this;
    }

    @Override
    public RdfContentBuilder<P> receive(Triple triple) throws IOException {
        generator.receive(triple);
        return this;
    }

    @Override
    public RdfContentBuilder<P> receive(Resource resource) throws IOException {
        generator.receive(resource);
        return this;
    }

    public IRI getSubject() {
        return subject;
    }

}
