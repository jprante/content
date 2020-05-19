package org.xbib.content.rdf.io.json;

import org.xbib.content.rdf.RdfConstants;
import org.xbib.content.rdf.RdfContentGenerator;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.Triple;
import org.xbib.content.rdf.internal.DefaultAnonymousResource;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.resource.IRI;

import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 *
 */
public class JsonContentGenerator implements RdfContentGenerator<JsonContentParams>, Flushable {

    private final Writer writer;

    private boolean nsWritten;

    private Resource resource;

    private JsonContentParams params = JsonContentParams.JSON_CONTENT_PARAMS;

    JsonContentGenerator(OutputStream out) throws IOException {
        this(new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }

    JsonContentGenerator(Writer writer) throws IOException {
        this.writer = writer;
        this.nsWritten = false;
        this.resource = new DefaultAnonymousResource();
    }

    @Override
    public JsonContentParams getParams() {
        return params;
    }

    @Override
    public void close() throws IOException {
        // write last resource
        receive(resource);
    }

    @Override
    public JsonContentGenerator receive(IRI iri) throws IOException {
        if (!iri.equals(resource.id())) {
            if (!nsWritten) {
                writeNamespaces();
            }
            resource = new DefaultResource(iri);
        }
        return this;
    }

    @Override
    public RdfContentGenerator<JsonContentParams> setParams(JsonContentParams rdfContentParams) {
        this.params = rdfContentParams;
        return this;
    }

    @Override
    public JsonContentGenerator startStream() {
        return this;
    }

    @Override
    public RdfContentGenerator<JsonContentParams> setBaseUri(String baseUri) {
        return this;
    }

    @Override
    public JsonContentGenerator startPrefixMapping(String prefix, String uri) {
        return this;
    }

    @Override
    public JsonContentGenerator endPrefixMapping(String prefix) {
        return this;
    }

    @Override
    public JsonContentGenerator receive(Triple triple) {
        resource.add(triple);
        return this;
    }

    @Override
    public JsonContentGenerator endStream() {
        return this;
    }

    private JsonContentGenerator writeNamespaces() throws IOException {
        nsWritten = false;
        for (Map.Entry<String, String> entry : params.getNamespaceContext().getNamespaces().entrySet()) {
            if (entry.getValue().length() > 0) {
                String nsURI = entry.getValue();
                if (!RdfConstants.NS_URI.equals(nsURI)) {
                    nsWritten = true;
                }
            }
        }
        return this;
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public JsonContentGenerator receive(Resource resource) throws IOException {
        return this;
    }
}
