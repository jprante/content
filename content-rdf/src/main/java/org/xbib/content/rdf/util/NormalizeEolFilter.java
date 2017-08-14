package org.xbib.content.rdf.util;

import java.io.IOException;
import java.io.Reader;

/**
 *
 */
public class NormalizeEolFilter extends SimpleFilterReader {

    private boolean previousWasEOL;

    private boolean fixLast;

    private int normalizedEOL = 0;

    private char[] eol = null;

    public NormalizeEolFilter(Reader in, String eolString, boolean fixLast) {
        super(in);
        eol = eolString.toCharArray();
        this.fixLast = fixLast;
    }

    public int read() throws IOException {
        int thisChar = super.read();
        if (normalizedEOL == 0) {
            int numEOL = 0;
            boolean atEnd = false;
            switch (thisChar) {
                case '\u001A':
                    int c = super.read();
                    if (c == -1) {
                        atEnd = true;
                        if (fixLast && !previousWasEOL) {
                            numEOL = 1;
                            push(thisChar);
                        }
                    } else {
                        push(c);
                    }
                    break;
                case -1:
                    atEnd = true;
                    if (fixLast && !previousWasEOL) {
                        numEOL = 1;
                    }
                    break;
                case '\n':
                    numEOL = 1;
                    break;
                case '\r':
                    numEOL = 1;
                    int c1 = super.read();
                    int c2 = super.read();
                    if (c1 != '\r' || c2 != '\n') {
                        if (c1 == '\r') {
                            numEOL = 2;
                            push(c2);
                        } else if (c1 == '\n') {
                            push(c2);
                        } else {
                            push(c2);
                            push(c1);
                        }
                    }
                    break;
                default:
                    break;
            }
            if (numEOL > 0) {
                while (numEOL-- > 0) {
                    push(eol);
                    normalizedEOL += eol.length;
                }
                previousWasEOL = true;
                thisChar = read();
            } else if (!atEnd) {
                previousWasEOL = false;
            }
        } else {
            normalizedEOL--;
        }
        return thisChar;
    }
}
