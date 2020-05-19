package org.xbib.content.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *
 */
public class CSVParser {

    private final CSVLexer lexer;

    private final List<String> row;

    private final CSVToken reusableCSVToken;

    public CSVParser(Reader reader) throws IOException {
        lexer = new CSVLexer(new CSVLookAheadReader(reader), ',', '\\', '"', '#', true, true);
        row = new LinkedList<>();
        reusableCSVToken = new CSVToken();
    }

    public CSVParser(Reader reader, char sep) throws IOException {
        lexer = new CSVLexer(new CSVLookAheadReader(reader), sep, '\\', '"', '#', true, true);
        row = new LinkedList<>();
        reusableCSVToken = new CSVToken();
    }

    public void close() throws IOException {
        lexer.close();
    }

    public long getCurrentLineNumber() {
        return lexer.getCurrentLineNumber();
    }

    public Iterator<List<String>> iterator() {
        return new Iterator<>() {
            private List<String> current;

            private List<String> getNextRow() throws IOException {
                    return CSVParser.this.nextRow();
            }

            @Override
            public boolean hasNext() {
                if (current == null) {
                    try {
                        current = getNextRow();
                    } catch (IOException e) {
                        throw new NoSuchElementException(e.getMessage());
                    }
                }
                return current != null && !current.isEmpty();
            }

            @Override
            public List<String> next() {
                if (current == null || current.isEmpty()) {
                    throw new NoSuchElementException();
                }
                List<String> list = current;
                current = null;
                return list;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected List<String> nextRow() throws IOException {
        row.clear();
        StringBuilder sb = null;
        do {
            reusableCSVToken.reset();
            lexer.nextToken(reusableCSVToken);
            String s = reusableCSVToken.content.toString();
            switch (reusableCSVToken.type) {
                case TOKEN:
                case EORECORD:
                    row.add(s);
                    break;
                case EOF:
                    if (reusableCSVToken.isReady) {
                        row.add(s);
                    }
                    break;
                case INVALID:
                    throw new IOException("(line " + getCurrentLineNumber() + ") invalid parse sequence");
                case COMMENT:
                    if (sb == null) {
                        sb = new StringBuilder();
                    } else {
                        sb.append(CSVConstants.LF);
                    }
                    sb.append(reusableCSVToken.content);
                    reusableCSVToken.type = CSVToken.Type.TOKEN;
                    break;
                default:
                    throw new IllegalStateException("unexpected token type: " + reusableCSVToken.type);
            }
        } while (reusableCSVToken.type == CSVToken.Type.TOKEN);
        return row;
    }

}
