package org.xbib.content.xml.util;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class XMLUtilTest extends Assert {

    @Test
    public void testWhitespaceCleaner() {
        String s = "Hello World\u001b";
        assertEquals(XMLUtil.sanitize(s), "Hello World");
    }

    @Test
    public void testWhitespaceCleanerWithReplacementCharacter() {
        String s = "Hello World\u001b";
        assertEquals(XMLUtil.sanitizeXml10(s), "Hello World�");
    }
}
