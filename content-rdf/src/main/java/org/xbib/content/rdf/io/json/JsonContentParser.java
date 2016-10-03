package org.xbib.content.rdf.io.json;

import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentParser;
import org.xbib.content.rdf.RdfContentType;
import org.xbib.content.rdf.StandardRdfContentType;
import org.xbib.content.rdf.io.xml.XmlHandler;
import org.xbib.content.xml.json.JsonSaxAdapter;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.xml.namespace.QName;

/**
 * A parser for generic JSON (not JSON-LD) via JSON SaX adapter.
 */
public class JsonContentParser implements RdfContentParser {

    private final Reader reader;

    private XmlHandler<JsonContentParams> handler;

    private RdfContentBuilder<JsonContentParams> builder;

    private QName root;

    public JsonContentParser(InputStream in) throws IOException {
        this(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public JsonContentParser(Reader reader) {
        this.reader = reader;
    }

    @Override
    public RdfContentType contentType() {
        return StandardRdfContentType.JSON;
    }

    public XmlHandler<JsonContentParams> getHandler() {
        return handler;
    }

    public JsonContentParser setHandler(XmlHandler<JsonContentParams> handler) {
        this.handler = handler;
        return this;
    }

    public JsonContentParser root(QName root) {
        this.root = root;
        return this;
    }

    public JsonContentParser builder(RdfContentBuilder<JsonContentParams> builder) {
        this.builder = builder;
        return this;
    }

    @Override
    public JsonContentParser parse() throws IOException {
        if (handler != null) {
            if (builder != null) {
                handler.setBuilder(builder);
            }
            JsonSaxAdapter adapter = new JsonSaxAdapter(reader, handler)
                    .root(root)
                    .context(handler.getNamespaceContext());
            try {
                adapter.parse();
            } catch (SAXException e) {
                throw new IOException(e);
            }
        }
        return this;
    }

}
