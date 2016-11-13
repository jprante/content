package org.xbib.content.rdf.io.json;

import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentParams;
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
 * @param <R> parameterized type
 */
public class JsonContentParser<R extends RdfContentParams> implements RdfContentParser<R> {

    private final Reader reader;

    private XmlHandler<R> handler;

    private RdfContentBuilder<R> builder;

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

    public XmlHandler<R> getHandler() {
        return handler;
    }

    public JsonContentParser<R> setHandler(XmlHandler<R> handler) {
        this.handler = handler;
        return this;
    }

    public JsonContentParser<R> root(QName root) {
        this.root = root;
        return this;
    }

    public JsonContentParser<R> builder(RdfContentBuilder<R> builder) {
        this.builder = builder;
        return this;
    }

    @Override
    public JsonContentParser<R> parse() throws IOException {
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
