package org.xbib.helper;

import org.junit.Assert;

import java.io.InputStream;
import java.io.Reader;

/**
 *
 */
public class StreamTester extends Assert {

    protected static void assertStream(InputStream expected, InputStream actual) {
        int offset = 0;
        try {
            while (true) {
                final int exp = expected.read();
                if (exp == -1) {
                    assertEquals("Expecting end of actual stream at offset " + offset, -1, actual.read());
                    break;
                } else {
                    final int act = actual.read();
                    assertEquals("Expecting same data at offset " + offset, exp, act);
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
                    assertEquals("Expecting end of actual stream at offset " + offset, -1, actual.read());
                    break;
                } else {
                    final int act = actual.read();
                    assertEquals("Expecting same data at offset " + offset, exp, act);
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
