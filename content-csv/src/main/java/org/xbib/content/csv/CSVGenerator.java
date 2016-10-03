package org.xbib.content.csv;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CSVGenerator implements CSVConstants, Closeable, Flushable {

    private static final String LF = System.getProperty("line.separator");

    private Writer writer;

    private int col;

    private int row;

    private List<String> keys;

    public CSVGenerator(Writer writer) {
        this.writer = writer;
        this.col = 0;
        this.keys = new ArrayList<>();
    }

    public CSVGenerator keys(List<String> keys) {
        this.keys = keys;
        return this;
    }

    public CSVGenerator writeKeys() throws IOException {
        for (String k : keys) {
            write(k);
        }
        return this;
    }

    public CSVGenerator write(String value) throws IOException {
        if (col > 0) {
            writer.write(COMMA);
        }
        if (value != null) {
            writer.write(escape(value));
        }
        col++;
        if (col > keys.size()) {
            writer.write(LF);
            row++;
            col = 0;
        }
        return this;
    }

    public int getColumn() {
        return col;
    }

    public int getRow() {
        return row;
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    private String escape(String value) {
        if (value.indexOf(QUOTE) < 0
                && value.indexOf(ESCAPE_CHARACTER) < 0
                && value.indexOf(COMMA) < 0
                && value.indexOf(TAB) < 0
                && !value.contains(LF)) {
           return value;
        }
        int length = value.length();
        StringBuilder sb = new StringBuilder(length + 2);
        sb.append(QUOTE);
        for (int i = 0; i < length; i++) {
            char ch = value.charAt(i);
            if (ch == QUOTE) {
                sb.append(QUOTE);
            }
            sb.append(ch);
        }
        sb.append(QUOTE);
        return sb.toString();
    }

}
