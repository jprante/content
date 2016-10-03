package org.xbib.content.rdf.io.json;

import org.xbib.content.rdf.RdfContent;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentGenerator;
import org.xbib.content.rdf.RdfContentParser;
import org.xbib.content.rdf.StandardRdfContentType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public class JsonContent implements RdfContent<JsonContentParams> {

    private static final JsonContent JSON_CONTENT = new JsonContent();

    private JsonContent() {
    }

    public static JsonContent jsonContent() {
        return JSON_CONTENT;
    }

    public static RdfContentBuilder<JsonContentParams> contentBuilder(JsonContentParams params) throws IOException {
        return new RdfContentBuilder<>(JSON_CONTENT, params);
    }

    public static RdfContentBuilder<JsonContentParams> contentBuilder(OutputStream out, JsonContentParams params)
            throws IOException {
        return new RdfContentBuilder<>(JSON_CONTENT, params, out);
    }

    @Override
    public StandardRdfContentType type() {
        return null;
    }

    @Override
    public RdfContentGenerator<JsonContentParams> createGenerator(OutputStream out) throws IOException {
        return new JsonContentGenerator(out);
    }

    @Override
    public RdfContentParser createParser(InputStream in) throws IOException {
        return new JsonContentParser(in);
    }
}
