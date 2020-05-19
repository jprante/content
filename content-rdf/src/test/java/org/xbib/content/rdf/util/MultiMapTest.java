package org.xbib.content.rdf.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

/**
 *
 */
public class MultiMapTest {

    @Test
    public void testLinkedHashMultiMap() {
        LinkedHashMultiMap<String, String> map = new LinkedHashMultiMap<>();
        map.put("a", "b");
        map.put("b", "c");
        map.put("a", "c");
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertEquals("[b, c]", map.get("a").toString());
        assertEquals("[c]", map.get("b").toString());
        map.putAll("a", Arrays.asList("d", "e"));
        assertEquals("[b, c, d, e]", map.get("a").toString());
    }

    @Test
    public void testTreeMultiMap() {
        TreeMultiMap<String, String> map = new TreeMultiMap<>();
        map.put("a", "b");
        map.put("b", "c");
        map.put("a", "c");
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertEquals("[a, b]", map.keySet().toString());
        assertEquals("[b, c]", map.get("a").toString());
        assertEquals("[c]", map.get("b").toString());
        map.putAll("a", Arrays.asList("d", "e"));
        assertEquals("[b, c, d, e]", map.get("a").toString());
    }
}
