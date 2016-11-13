package org.xbib.content.rdf.io.turtle;

import org.xbib.content.rdf.Literal;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentBuilderHandler;
import org.xbib.content.rdf.RdfContentBuilderProvider;
import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.rdf.RdfContentParser;
import org.xbib.content.rdf.RdfContentType;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.StandardRdfContentType;
import org.xbib.content.rdf.Triple;
import org.xbib.content.rdf.XSDResourceIdentifiers;
import org.xbib.content.rdf.internal.DefaultAnonymousResource;
import org.xbib.content.rdf.internal.DefaultLiteral;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.rdf.internal.DefaultTriple;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;
import org.xbib.content.resource.XmlNamespaceContext;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Turtle - Terse RDF Triple Parser.
 *
 * @see <a href="http://www.w3.org/TeamSubmission/turtle/">Turtle - Terse RDF
 * Triple Language</a>
 * @param <R> RDF content type parameter
 */
public class TurtleContentParser<R extends RdfContentParams> implements RdfContentParser<R> {

    private static final Logger logger = Logger.getLogger(TurtleContentParser.class.getName());

    private final Resource resource = new DefaultAnonymousResource();

    private final HashMap<String, Node> bnodes = new HashMap<>();

    private RdfContentBuilderProvider<R> provider;

    private RdfContentBuilderHandler<R> rdfContentBuilderHandler;

    private RdfContentBuilder<R> builder;

    /**
     * The base IRI.
     */
    private IRI baseIRI;
    /**
     * The reader for reading input streams of turtle statements.
     */
    private PushbackReader reader;
    /**
     * The parsed subject.
     */
    private Resource subject;
    /**
     * The parsed predicate.
     */
    private IRI predicate;
    /**
     * The parsed object.
     */
    private Node object;
    /**
     * The last subject parsed, for sending record events. A collection of
     * triples with same subject in sequence is assumed a record.
     */
    private Resource lastsubject;
    /**
     * String builder for parsing.
     */
    private StringBuilder sb;
    /**
     * Indicate if endStream of stream is reached.
     */
    private boolean eof;
    /**
     * Stack for triples.
     */
    private LinkedList<Triple> triples;
    /**
     * The namespace context.
     */
    private XmlNamespaceContext context = XmlNamespaceContext.newDefaultInstance();

    public TurtleContentParser(InputStream in) throws IOException {
        this(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public TurtleContentParser(Reader reader) {
        this.reader = new PushbackReader(reader, 2);
    }

    public static String decode(String s, String encoding) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean fragment = false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '+':
                    sb.append(' ');
                    break;
                case '#':
                    sb.append(ch);
                    fragment = true;
                    break;
                case '%':
                    if (!fragment) {
                        // fast hex decode
                        sb.append((char) ((Character.digit(s.charAt(++i), 16) << 4)
                                | Character.digit(s.charAt(++i), 16)));
                    } else {
                        sb.append(ch);
                    }
                    break;
                default:
                    sb.append(ch);
                    break;
            }
        }
        return new String(sb.toString().getBytes(StandardCharsets.ISO_8859_1), encoding);
    }

    @Override
    public RdfContentType contentType() {
        return StandardRdfContentType.TURTLE;
    }

    public TurtleContentParser<R> setRdfContentBuilderProvider(RdfContentBuilderProvider<R> provider) {
        this.provider = provider;
        return this;
    }

    public TurtleContentParser<R> setRdfContentBuilderHandler(RdfContentBuilderHandler<R>
                                                                   rdfContentBuilderHandler) {
        this.rdfContentBuilderHandler = rdfContentBuilderHandler;
        return this;
    }

    public TurtleContentParser<R> setBaseIRI(IRI baseIRI) {
        this.baseIRI = baseIRI;
        return this;
    }

    public TurtleContentParser<R> context(XmlNamespaceContext context) {
        this.context = context;
        return this;
    }

    @Override
    public TurtleContentParser<R> parse() throws IOException {
        if (provider != null) {
            builder = provider.newContentBuilder();
            builder.startStream();
        }
        this.reader = new PushbackReader(reader, 2);
        this.sb = new StringBuilder();
        this.eof = false;
        this.triples = new LinkedList<>();
        try {
            while (!eof) {
                char ch = skipWhitespace();
                if (eof) {
                    break;
                }
                if (ch == '@') {
                    parseDirective();
                } else {
                    parseTriple();
                }
            }
        } finally {
            this.reader.close();
            if (builder != null) {
                while (!triples.isEmpty()) {
                    Triple t = triples.pop();
                    builder.receive(t);
                }
                if (rdfContentBuilderHandler != null) {
                    rdfContentBuilderHandler.build(builder);
                }
                builder.endStream();
                bnodes.clear();
            }
        }
        return this;
    }

    private void skip() throws IOException {
        int c = reader.read();
        if (c == -1) {
            throw new EOFException();
        }
    }

    /**
     * Parse a directive.
     * The prefix directive binds a prefix to a namespace URI. It indicates that
     * a qualified name (qname) with that prefix will thereafter be a shorthand
     * for a URI consisting of the concatenation of the namespace identifier and
     * the bit of the qname to the right of the (only allowed) colon.
     * The namespace prefix may be empty, in which case the qname starts with a
     * colon. This is known as the default namespace. The empty prefix "" is by
     * default , bound to "#" -- the local namespace of the file. The parser
     * behaves as though there were a @prefix : &lt;#&gt;. just before the file. This
     * means that &lt;#foo&gt; can be written :foo.
     * The base directive sets the base URI to be used for the parsing of
     * relative URIs. It takes, itself, a relative URI, so it can be used to
     * change the base URI relative to the previous one.
     *
     * @throws IOException if directive could not be parsed
     */
    private void parseDirective() throws IOException {
        String directive;
        sb.setLength(0);
        boolean b;
        do {
            char ch = read();
            b = !isWhitespace(ch);
            if (b) {
                sb.append(ch);
            }
        } while (b);
        directive = sb.toString();
        skipWhitespace();
        sb.setLength(0);
        if ("@prefix".equalsIgnoreCase(directive)) {
            char ch = read();
            while (ch != ':') {
                sb.append(ch);
                ch = read();
            }
            String prefix = sb.toString();
            skip();
            skipWhitespace();
            IRI nsURI = parseURI();
            if ("".equals(prefix)) {
                this.baseIRI = nsURI;
            }
            context.addNamespace(prefix, nsURI.toString());
        } else if ("@base".equalsIgnoreCase(directive)) {
            this.baseIRI = parseURI();
        } else {
            throw new IOException(baseIRI + ": unknown directive: " + directive);
        }
        skipWhitespace();
        validate(reader.read(), '.');
    }

    private void parseTriple() throws IOException {
        subject = null;
        predicate = null;
        object = null;
        parseSubject();
        skipWhitespace();
        parsePredicateObjectList();
        skipWhitespace();
        validate(reader.read(), '.');
    }

    private void parsePredicateObjectList() throws IOException {
        predicate = parsePredicate();
        skipWhitespace();
        parseObjectList();
        char ch = skipWhitespace();
        while (ch == ';') {
            skip();
            ch = skipWhitespace();
            if (ch == '.' || ch == ']') {
                break;
            }
            predicate = parsePredicate();
            skipWhitespace();
            parseObjectList();
            ch = skipWhitespace();
        }
    }

    private void parseObjectList() throws IOException {
        parseObject();
        char ch = skipWhitespace();
        while (ch == ',') {
            skip();
            skipWhitespace();
            parseObject();
            ch = skipWhitespace();
        }
    }

    private void parseSubject() throws IOException {
        char ch = peek();
        if (ch == '(') {
            subject = parseCollection();
        } else if (ch == '[') {
            subject = (Resource) parseBlankNode();
        } else {
            Node value = parseValue();
            if (value instanceof Resource) {
                subject = (Resource) value;
            } else {
                throw new IOException(baseIRI + ": illegal subject value: '" + value + "' (" + value.getClass() + ")");
            }
        }
    }

    private IRI parsePredicate() throws IOException {
        char ch = read();
        if (ch == 'a') {
            char ch2 = read();
            if (isWhitespace(ch2)) {
                return resource.newPredicate("rdf:type");
            }
            reader.unread(ch2);
        }
        reader.unread(ch);
        Node obj = parseValue();
        if (obj instanceof Resource) {
            return resource.newPredicate(obj.toString());
        } else {
            throw new IOException(baseIRI + ": illegal predicate value: " + obj);
        }
    }

    private void parseObject() throws IOException {
        char ch = peek();
        if (ch == '(') {
            object = parseCollection();
        } else if (ch == '[') {
            object = parseBlankNode();
        } else {
            object = parseValue();
        }
        Triple stmt = new DefaultTriple(subject, predicate, object);
        if (subject.isEmbedded()) {
            // Push triples with blank node subjects on stack.
            // The idea for having ordered getResource properties is:
            // All resource property triples should be serialized
            // after the resource parent triple.
            triples.add(0, stmt);
        } else {
            // A "record" is grouped by a sequence of same (non-blank) subjects
            // build temp resource and pass triples to builder
            if (lastsubject == null || !subject.equals(lastsubject)) {
                if (provider != null) {
                    while (!triples.isEmpty()) {
                        Triple t = triples.pop();
                        builder.receive(t);
                    }
                    if (rdfContentBuilderHandler != null && builder.getSubject() != null) {
                        rdfContentBuilderHandler.build(builder);
                    }
                    bnodes.clear();
                    builder = provider.newContentBuilder();
                    builder.receive(subject.id());
                    builder.receive(stmt);
                }
                lastsubject = subject;
            } else {
                if (builder != null) {
                    builder.receive(stmt);
                }
            }
        }
    }

    private Node parseValue() throws IOException {
        char ch = peek();
        if (ch == '<') {
            return new DefaultResource(parseURI());
        } else if (ch == ':' || isPrefixStartChar(ch)) {
            return parseQNameOrBoolean();
        } else if (ch == '_') {
            return parseNodeID();
        } else if (ch == '(') {
            return parseCollection();
        } else if (ch == '"') {
            return parseQuotedLiteral();
        } else if (Character.isDigit(ch) || ch == '.' || ch == '+' || ch == '-') {
            return parseNumber();
        } else if ((int) ch == 65535) {
            throw new EOFException();
        } else {
            throw new IOException(baseIRI + ": unable to parse value, unknown character: code = " + (int) ch
                    + " character = '" + ch + "'.  Last triple seen: " + subject + " " + predicate + " " + object);
        }
    }

    private IRI parseURI() throws IOException {
        char ch = read();
        boolean checkForClose = false;
        if (ch == '<') {
            ch = read(); // skip '<' and check for closing '>'
            checkForClose = true;
        }
        sb.setLength(0);
        boolean ended = false;
        while (!ended) {
            sb.append(ch);
            if (ch == '\\') {
                ch = read();
                sb.append(ch);
            }
            ch = read();
            ended = checkForClose ?
                    (ch == '>') :
                    (ch == '>' || ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r');
        }
        // we trim to cope with GND erraneous IRIs with spaces like skos:exactMatch &lt;http://zbw.eu/stw/descriptor/18673-1 &gt; ;
        String decoded = decode(sb.toString().trim(), "UTF-8");
        IRI u = IRI.builder().curie(decoded).build();
        u = baseIRI.resolve(u);
        return u;
    }

    /**
     * Parse qualified name.
     *
     * @return qualified name URI
     * @throws IOException if parse fails
     */
    private Node parseQNameOrBoolean() throws IOException {
        sb.setLength(0);
        char ch = read();
        if (ch != ':' && !isPrefixStartChar(ch)) {
            throw new IOException(baseIRI + ": expected colon or letter, not: '" + ch + "'");
        }
        String ns;
        if (ch == ':') {
            ns = context.getNamespaceURI("");
        } else {
            sb.append(ch);
            ch = read();
            while (Character.isLetter(ch) || Character.isDigit(ch) || ch == '-' || ch == '_') {
                sb.append(ch);
                ch = read();
            }
            if (ch != ':') {
                String value = sb.toString();
                if ("true".equals(value) || "false".equals(value)) {
                    return resource.newLiteral(value).type(XSDResourceIdentifiers.BOOLEAN);
                }
            }
            validate(ch, ':');
            ns = context.getNamespaceURI(sb.toString());
            if (ns == null) {
                throw new IOException(baseIRI + ": namespace not found: " + sb.toString());
            }
        }
        sb.setLength(0);
        ch = read();
        if (Character.isLetter(ch) || ch == '_') {
            sb.append(ch);
            ch = read();
            while (Character.isLetter(ch) || Character.isDigit(ch) || ch == '-' || ch == '_') {
                sb.append(ch);
                ch = read();
            }
        }
        reader.unread(ch);
        // namespace is already resolved
        IRI iri = IRI.builder().curie(ns + sb).build();
        return new DefaultResource(iri);
    }

    /**
     * Parse blank node, with or without a node ID.
     *
     * @return node
     * @throws IOException if parse fails
     */
    private Node parseBlankNode() throws IOException {
        char ch = peek();
        if (ch == '_') {
            return parseNodeID();
        } else if (ch == '[') {
            skip();
            DefaultResource bnode = new DefaultAnonymousResource();
            ch = read();
            if (ch != ']') {
                Resource oldsubject = subject;
                IRI oldpredicate = predicate;
                subject = bnode;
                skipWhitespace();
                parsePredicateObjectList();
                skipWhitespace();
                validate(reader.read(), ']');
                subject = oldsubject;
                predicate = oldpredicate;
            }
            return bnode;
        } else {
            throw new IOException(baseIRI + ":expected character: '[' or '_'");
        }
    }

    /**
     * Parse a collection.
     *
     * @return the collection as a resource
     * @throws IOException if parse fails
     */
    private Resource parseCollection() throws IOException {
        validate(reader.read(), '(');
        char ch = skipWhitespace();
        if (ch == ')') {
            skip();
            return new DefaultResource(IRI.builder().curie("rdf", "nil").build());
        } else {
            DefaultResource first = new DefaultAnonymousResource();
            Resource oldsubject = subject;
            IRI oldpredicate = predicate;
            subject = first;
            predicate = resource.newPredicate("rdf:first");
            parseObject();
            ch = skipWhitespace();
            DefaultResource blanknode = new DefaultResource(first.id());
            while (ch != ')') {
                DefaultResource value = new DefaultAnonymousResource();
                if (builder != null) {
                    builder.receive(new DefaultTriple(blanknode, resource.newPredicate("rdf:rest"), value));
                }
                subject = value;
                blanknode = value;
                parseObject();
                ch = skipWhitespace();
            }
            skip();
            if (builder != null) {
                Node value = new DefaultResource(IRI.builder().curie("rdf", "null").build());
                builder.receive(new DefaultTriple(blanknode, resource.newPredicate("rdf:rest"), value));
            }
            subject = oldsubject;
            predicate = oldpredicate;
            return first;
        }
    }

    /**
     * Parse node ID.
     *
     * @return the node
     * @throws IOException if parse fails
     */
    private Node parseNodeID() throws IOException {
        validate(reader.read(), '_');
        validate(reader.read(), ':');
        char ch = read();
        sb.setLength(0);
        if (Character.isLetter(ch) || ch == '_') {
            sb.append(ch);
            ch = read();
            while (Character.isLetter(ch) || Character.isDigit(ch) || ch == '-' || ch == '_') {
                sb.append(ch);
                ch = read();
            }
        }
        reader.unread(ch);
        String nodeID = sb.toString();
        Node bnode = bnodes.get(nodeID);
        if (bnode != null) {
            return bnode;
        }
        bnode = new DefaultAnonymousResource(nodeID);
        bnodes.put(nodeID, bnode);
        return bnode;
    }

    /**
     * Parse a literal.
     *
     * @return the literal
     * @throws IOException if parse fails.
     */
    private Literal parseQuotedLiteral() throws IOException {
        String value = parseQuotedString();
        char ch = peek();
        if (ch == '@') {
            skip();
            sb.setLength(0);
            ch = read();
            if (!Character.isLowerCase(ch)) {
                throw new IOException(baseIRI + ": lower case character expected: " + ch);
            }
            sb.append(ch);
            ch = read();
            while (Character.isLowerCase(ch) || Character.isDigit(ch) || ch == '-') {
                sb.append(ch);
                ch = read();
            }
            reader.unread(ch);
            return new DefaultLiteral(value).lang(sb.toString());
        } else if (ch == '^') {
            skip();
            validate(reader.read(), '^');
            skipWhitespace();
            IRI iri = parseURI();
            return new DefaultLiteral(value).type(iri);
        } else {
            return new DefaultLiteral(value);
        }
    }

    /**
     * Parses a quoted string, which is either a "normal string" or a """long
     * string""".
     */
    private String parseQuotedString() throws IOException {
        String result;
        validate(reader.read(), '\"');
        char c2 = read();
        char c3 = read();
        if (c2 == '"' && c3 == '"') {
            result = parseLongString();
        } else {
            reader.unread(c3);
            reader.unread(c2);
            result = parseString();
        }
        return decodeTurtleString(result);
    }

    /**
     * Parses a "normal string". This method assumes that the first double quote
     * has already been parsed.
     */
    private String parseString() throws IOException {
        sb.setLength(0);
        while (true) {
            char ch = read();
            if (ch == '"') {
                break;
            }
            sb.append(ch);
            if (ch == '\\') {
                ch = read();
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * Parses a """long string""". This method assumes that the first three
     * double quotes have already been parsed.
     */
    private String parseLongString() throws IOException {
        sb.setLength(0);
        int doubleQuoteCount = 0;
        char ch;
        while (doubleQuoteCount < 3) {
            ch = read();
            if (ch == '"') {
                doubleQuoteCount++;
            } else {
                doubleQuoteCount = 0;
            }
            sb.append(ch);
            if (ch == '\\') {
                // This escapes the next character, which might be a '"'
                ch = read();
                sb.append(ch);
            }
        }

        return sb.substring(0, sb.length() - 3);
    }

    private Literal parseNumber() throws IOException {
        sb.setLength(0);
        IRI datatype = IRI.builder().curie("xsd", "integer").build();
        char ch = read();
        if (ch == '+' || ch == '-') {
            sb.append(ch);
            ch = read();
        }
        while (Character.isDigit(ch)) {
            sb.append(ch);
            ch = read();
        }
        if (ch == '.' || ch == 'e' || ch == 'E') {
            datatype = IRI.builder().curie("xsd", "decimal").build();
            if (ch == '.') {
                sb.append(ch);
                ch = read();
                while (Character.isDigit(ch)) {
                    sb.append(ch);
                    ch = read();
                }
                if (sb.length() == 1) {
                    throw new IOException(" incomplete decimal: " + sb);
                }
            } else {
                if (sb.length() == 0) {
                    throw new IOException("in complete fraction: " + sb);
                }
            }
            if (ch == 'e' || ch == 'E') {
                datatype = IRI.builder().curie("xsd", "double").build();
                sb.append(ch);
                ch = read();
                if (ch == '+' || ch == '-') {
                    sb.append(ch);
                    ch = read();
                }
                if (!Character.isDigit(ch)) {
                    throw new IOException("exponent value missing: " + sb);
                }
                sb.append(ch);
                ch = read();
                while (Character.isDigit(ch)) {
                    sb.append(ch);
                    ch = read();
                }
            }
        }
        reader.unread(ch);
        return new DefaultLiteral(sb.toString()).type(datatype);
    }

    private char skipWhitespace() throws IOException {
        int ch = reader.read();
        while (isWhitespace((char) ch) || ch == '#') {
            if (ch == '#') {
                skipLine();
            }
            ch = reader.read();
        }
        if (ch == -1) {
            eof = true;
        }
        reader.unread(ch);
        return (char) ch;
    }

    private void skipLine() throws IOException {
        int ch = reader.read();
        while (ch != 0xd && ch != 0xa && ch != -1) {
            ch = reader.read();
        }
        if (ch == 0xd) {
            ch = reader.read();
            if (ch != 0xa) {
                reader.unread(ch);
            }
        }
        if (ch == -1) {
            eof = true;
        }
    }

    private char peek() throws IOException {
        int ch = reader.read();
        if (ch == -1) {
            eof = true;
        }
        reader.unread(ch);
        return (char) ch;
    }

    private char read() throws IOException {
        int ch = reader.read();
        if (ch == -1) {
            throw new EOFException();
        }
        return (char) ch;
    }

    private void validate(int ch, char v) throws IOException {
        if ((char) ch != v) {
            String message = (subject != null ? subject : "") + " unexpected character: '" +
                    (char) ch + "' expected: '" + v + "'";
            logger.log(Level.WARNING, message);
        }
    }

    private boolean isWhitespace(char ch) {
        return ch == 0x20 || ch == 0x9 || ch == 0xA || ch == 0xD;
    }

    private boolean isPrefixStartChar(char ch) {
        return Character.isLetter(ch) || ch >= 0x00C0 && ch <= 0x00D6
                || ch >= 0x00D8 && ch <= 0x00F6 || ch >= 0x00F8 && ch <= 0x02FF
                || ch >= 0x0370 && ch <= 0x037D || ch >= 0x037F && ch <= 0x1FFF
                || ch >= 0x200C && ch <= 0x200D || ch >= 0x2070 && ch <= 0x218F
                || ch >= 0x2C00 && ch <= 0x2FEF || ch >= 0x3001 && ch <= 0xD7FF
                || ch >= 0xF900 && ch <= 0xFDCF || ch >= 0xFDF0 && ch <= 0xFFFD;
    }

    /**
     * Decodes an encoded Turtle string. Any \-escape sequences are substituted
     * with their decoded sb.
     *
     * @param s An encoded Turtle string.
     * @return The unencoded string.
     * @throws IllegalArgumentException If the supplied string is not a
     *                                  correctly encoded Turtle string.
     */
    private String decodeTurtleString(String s) {
        int pos = s.indexOf('\\');
        if (pos == -1) {
            return s;
        }
        int i = 0;
        int len = s.length();
        sb.setLength(0);
        while (pos != -1) {
            sb.append(s.substring(i, pos));
            if (pos + 1 >= len) {
                break;
            }
            char ch = s.charAt(pos + 1);
            if (ch == 't') {
                sb.append('\t');
                i = pos + 2;
            } else if (ch == 'r') {
                sb.append('\r');
                i = pos + 2;
            } else if (ch == 'n') {
                sb.append('\n');
                i = pos + 2;
            } else if (ch == '"') {
                sb.append('"');
                i = pos + 2;
            } else if (ch == '>') {
                sb.append('>');
                i = pos + 2;
            } else if (ch == 'u') {
                if (pos + 5 >= len) {
                    throw new IllegalArgumentException("incomplete Unicode escape sequence in: " + s);
                }
                String xx = s.substring(pos + 2, pos + 6);
                try {
                    ch = (char) Integer.parseInt(xx, 16);
                    sb.append(ch);
                    i = pos + 6;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("illegal Unicode escape sequence '\\u" + xx + "' in: " + s);
                }
            } else if (ch == 'U') {
                if (pos + 9 >= len) {
                    throw new IllegalArgumentException("incomplete Unicode escape sequence in: " + s);
                }
                String xx = s.substring(pos + 2, pos + 10);
                try {
                    ch = (char) Integer.parseInt(xx, 16);
                    sb.append(ch);
                    i = pos + 10;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("illegal Unicode escape sequence '\\U" + xx + "' in: " + s);
                }
            } else {
                sb.append('\\');
                i = pos + 2;
            }
            pos = s.indexOf('\\', i);
        }
        sb.append(s.substring(i));
        return sb.toString();
    }

}
