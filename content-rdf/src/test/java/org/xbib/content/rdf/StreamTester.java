package org.xbib.content.rdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.InputStream;
import java.io.Reader;

/**
 *
 */
public class StreamTester {

    public static void assertStream(InputStream expected, InputStream actual) {
        int offset = 0;
        try {
            while (true) {
                final int exp = expected.read();
                if (exp == -1) {
                    assertEquals(-1, actual.read());
                    break;
                } else {
                    final int act = actual.read();
                    assertEquals(exp, act);
                }
                offset++;
            }
            expected.close();
            actual.close();
        } catch (Exception e) {
            fail("Exception at offset " + offset + ": " + e);
        }
    }

    protected static void assertStream(Reader expected, Reader actual) {
        int offset = 0;
        try {
            while (true) {
                final int exp = expected.read();
                if (exp == -1) {
                    assertEquals(-1, actual.read(), "Expecting end of actual stream at offset " + offset);
                    break;
                } else {
                    final int act = actual.read();
                    assertEquals(exp, act, "Expecting same data at offset " + offset);
                }
                offset++;
            }
            expected.close();
            actual.close();
        } catch (Exception e) {
            fail("Exception at offset " + offset + ": " + e);
        }
    }

}
