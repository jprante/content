package org.xbib.content.rdf.io.ntriple;

import org.xbib.content.rdf.Literal;
import org.xbib.content.rdf.RdfContentGenerator;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.Triple;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;

import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * NTriple content generator.
 */
public class NTripleContentGenerator
        implements RdfContentGenerator<NTripleContentParams>, Flushable {

    private static final char LF = '\n';

    private final Writer writer;

    private NTripleContentParams params = NTripleContentParams.N_TRIPLE_CONTENT_PARAMS;

    NTripleContentGenerator(OutputStream out) throws IOException {
        this(new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }

    NTripleContentGenerator(Writer writer) throws IOException {
        this.writer = writer;
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public NTripleContentGenerator receive(IRI iri) throws IOException {
        return this;
    }

    @Override
    public RdfContentGenerator<NTripleContentParams> setParams(NTripleContentParams rdfContentParams) {
        this.params = rdfContentParams;
        return this;
    }

    @Override
    public NTripleContentParams getParams() {
        return params;
    }

    @Override
    public NTripleContentGenerator startStream() {
        return this;
    }

    @Override
    public RdfContentGenerator<NTripleContentParams> setBaseUri(String baseUri) {
        startPrefixMapping("", baseUri);
        return this;
    }

    @Override
    public NTripleContentGenerator receive(Triple triple) throws IOException {
        writer.write(writeStatement(triple));
        return this;
    }

    @Override
    public NTripleContentGenerator endStream() {
        return this;
    }

    @Override
    public NTripleContentGenerator startPrefixMapping(String prefix, String uri) {
        params.getNamespaceContext().addNamespace(prefix, uri);
        return this;
    }

    @Override
    public NTripleContentGenerator endPrefixMapping(String prefix) {
        // we don't remove name spaces. It's troubling RDF serializations.
        return this;
    }

    @Override
    public NTripleContentGenerator receive(Resource resource) throws IOException {
        for (Triple t : resource.triples()) {
            writer.write(writeStatement(t));
        }
        return this;
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    public String writeStatement(Triple stmt) throws IOException {
        Resource subj = stmt.subject();
        IRI pred = stmt.predicate();
        Node obj = stmt.object();
        return writeSubject(subj) + " " + writePredicate(pred) + " " + writeObject(obj) + " ." + LF;
    }

    public String writeSubject(Resource subject) {
        return subject.isEmbedded() ? subject.toString() : "<" + escape(subject.toString()) + ">";
    }

    public String writePredicate(IRI predicate) {
        return "<" + escape(predicate.toString()) + ">";
    }

    public String writeObject(Node object) {
        if (object instanceof Resource) {
            Resource subject = (Resource) object;
            return writeSubject(subject);
        } else if (object instanceof Literal) {
            Literal value = (Literal) object;
            String s = "\"" + escape(value.object().toString()) + "\"";
            String lang = value.lang();
            IRI type = value.type();
            if (lang != null) {
                return s + "@" + lang;
            }
            if (type != null) {
                return s + "^^<" + escape(type.toString()) + ">";
            }
            return s;
        } else if (object instanceof IRI) {
            return "<" + escape(object.toString()) + ">";
        }
        return "<class?>^^" + object.getClass();
    }

    private String escape(String buffer) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buffer.length(); i++) {
            char ch = buffer.charAt(i);
            switch (ch) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (ch >= 32 && ch <= 126) {
                        sb.append(ch);
                    } else {
                        sb.append("\\u");
                        String hexstr = Integer.toHexString(ch).toUpperCase();
                        int pad = 4 - hexstr.length();
                        for (; pad > 0; pad--) {
                            sb.append("0");
                        }
                        sb.append(hexstr);
                    }
            }
        }
        return sb.toString();
    }
}
