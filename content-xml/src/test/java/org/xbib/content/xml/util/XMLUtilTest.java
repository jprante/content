package org.xbib.content.xml.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class XMLUtilTest {

    @Test
    public void testWhitespaceCleaner() {
        String s = "Hello World\u001b";
        assertEquals(XMLUtil.sanitize(s), "Hello World");
    }

    @Test
    public void testWhitespaceCleanerWithReplacementCharacter() {
        String s = "Hello World\u001b";
        assertEquals("Hello World\ufffd", XMLUtil.sanitizeXml10(s));
    }
}
