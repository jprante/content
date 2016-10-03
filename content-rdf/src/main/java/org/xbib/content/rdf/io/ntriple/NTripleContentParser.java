package org.xbib.content.rdf.io.ntriple;

import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentParser;
import org.xbib.content.rdf.RdfContentType;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.StandardRdfContentType;
import org.xbib.content.rdf.internal.DefaultAnonymousResource;
import org.xbib.content.rdf.internal.DefaultTriple;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Parser for NTriple RDF format.
 * See also the <a href="http://www.w3.org/TR/rdf-testcases/#convert">NTriple
 * specification</a>.
 */
public class NTripleContentParser implements RdfContentParser {

    private static final Resource resource = new DefaultAnonymousResource();

    private static final String RESOURCE_EXPRESSION = "(<[^<]+?>)";
    private static final String LITERAL_VALUE_EXPRESSION = "(\"([^\"]|\\\")*\")";
    private static final String ANONYMOUS_EXPRESSION = "(_:[^\\s]+?)";
    private static final String DATATYPE_EXPRESSION = "(\\^\\^" + RESOURCE_EXPRESSION + ")";
    private static final String LANGUAGE_EXPRESSION = "(@([a-z]{2}?))";
    private static final String LITERAL_EXPRESSION = "(" + LITERAL_VALUE_EXPRESSION + "(" + DATATYPE_EXPRESSION +
            "|" + LANGUAGE_EXPRESSION + ")??" + ")";
    private static final String SUBJECT_EXPRESSION = "(" + ANONYMOUS_EXPRESSION + "|" + RESOURCE_EXPRESSION + ")";
    private static final String PREDICATE_EXPRESSION = "(" + RESOURCE_EXPRESSION + ")";
    private static final String OBJECT_EXPRESSION = "(" + ANONYMOUS_EXPRESSION + "|" + RESOURCE_EXPRESSION + "|" +
            LITERAL_EXPRESSION + ")";
    private static final String TRIPLE_EXPRESSION = SUBJECT_EXPRESSION + "\\s+" + PREDICATE_EXPRESSION + "\\s+" +
            OBJECT_EXPRESSION + "\\s*\\.";
    private static final Pattern NTRIPLE_PATTERN = Pattern.compile(TRIPLE_EXPRESSION);

    private final Reader reader;

    private boolean eof;

    private RdfContentBuilder<NTripleContentParams> builder;

    public NTripleContentParser(InputStream in) throws IOException {
        this(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public NTripleContentParser(Reader reader) {
        this.reader = reader;
    }

    @Override
    public RdfContentType contentType() {
        return StandardRdfContentType.NTRIPLE;
    }

    public NTripleContentParser setBuilder(RdfContentBuilder<NTripleContentParams> builder) {
        this.builder = builder;
        return this;
    }

    @Override
    public NTripleContentParser parse() throws IOException {
        this.eof = false;
        try (BufferedReader br = new BufferedReader(reader)) {
            while (!eof) {
                parseLine(br.readLine());
                if (eof) {
                    break;
                }
            }
        }
        return this;
    }

    /*
     * Groups in the regular expression are identified by round brackets. There
     * are actually 21 groups in the regex. They are defined as follows:
     *
     * 0  the whole triple
     * 1  subject
     * 2  anonymous subject
     * 3  resource subject
     * 4  predicate
     * 5  resource predicate
     * 6  object
     * 7  anonymous subject
     * 8  resource object
     * 9  literal object
     * 10 literal value
     * 11 string with quotes in literal value
     * 12 string without quotes in literal value
     * 13 last character in string
     * 14 string with apostrophes in literal value
     * 15 string without apostrophes in literal value
     * 16 last character in string
     * 17 datatype or language
     * 18 datatype with ^^
     * 19 datatype without ^^ (resource)
     * 20 language with @
     * 21 language without @
     */
    private void parseLine(String line) throws IOException {
        if (line == null) {
            eof = true;
            return;
        }
        String s = line.trim();
        if (s.length() == 0 || s.startsWith("#")) {
            return;
        }
        Matcher matcher = NTRIPLE_PATTERN.matcher(s);
        Resource subject;
        IRI predicate;
        Node object;
        if (!matcher.matches()) {
            throw new PatternSyntaxException("the given pattern " +
                    TRIPLE_EXPRESSION + " doesn't match the expression:", s, -1);
        }
        // subject
        if (matcher.group(2) != null) {
            subject = new DefaultAnonymousResource(matcher.group(1));
        } else {
            // resource node
            String subj = matcher.group(1);
            IRI subjURI = IRI.create(subj.substring(1, subj.length() - 1));
            subject = resource.newSubject(subjURI);
        }
        // predicate
        String p = matcher.group(4);
        predicate = resource.newPredicate(IRI.create(p.substring(1, p.length() - 1)));
        // object
        if (matcher.group(7) != null) {
            // anonymous node
            object = new DefaultAnonymousResource(matcher.group(6));
        } else if (matcher.group(8) != null) {
            // resource node
            String obj = matcher.group(6);
            object = resource.newObject(IRI.builder().curie(obj.substring(1, obj.length() - 1)).build());
        } else {
            // literal node
            // 10 is without quotes or apostrophs
            // with quotes or apostrophes. to have the value without them you need to look at groups 12 and 15
            String literal = matcher.group(10);
            literal = literal.length() > 2 ? literal.substring(1, literal.length() - 1) : literal;
            if (matcher.groupCount() > 15) {
                object = resource.newLiteral(literal).lang(matcher.group(16));
            } else {
                object = resource.newLiteral(literal);
            }
        }
        if (builder != null) {
            builder.receive(new DefaultTriple(subject, predicate, object));
        }
    }
}
