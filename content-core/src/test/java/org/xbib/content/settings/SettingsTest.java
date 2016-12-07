package org.xbib.content.settings;


import org.junit.Assert;
import org.junit.Test;
import org.xbib.content.XContentBuilder;
import org.xbib.content.XContentHelper;
import org.xbib.content.json.JsonSettingsLoader;
import org.xbib.content.json.JsonXContent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SettingsTest extends Assert {

    @Test
    public void testEmpty() {
        Settings settings = Settings.EMPTY_SETTINGS;
        assertTrue(settings.getAsMap().isEmpty());
    }

    @Test
    public void testArray() {
        Settings settings = Settings.settingsBuilder()
                .putArray("input", Arrays.asList("a", "b", "c")).build();
        assertEquals("a", settings.getAsArray("input")[0]);
        assertEquals("b", settings.getAsArray("input")[1]);
        assertEquals("c", settings.getAsArray("input")[2]);
    }

    @Test
    public void testGroups() {
        Settings settings = Settings.settingsBuilder()
                .put("prefix.group1.k1", "v1")
                .put("prefix.group1.k2", "v2")
                .put("prefix.group1.k3", "v3")
                .put("prefix.group2.k1", "v1")
                .put("prefix.group2.k2", "v2")
                .put("prefix.group2.k3", "v3")
                .build();
        Map<String, Settings> groups = settings.getGroups("prefix");
        assertEquals("[group1, group2]", groups.keySet().toString());
        assertTrue(groups.get("group1").getAsMap().containsKey("k1"));
        assertTrue(groups.get("group1").getAsMap().containsKey("k2"));
        assertTrue(groups.get("group1").getAsMap().containsKey("k3"));
        assertTrue(groups.get("group2").getAsMap().containsKey("k1"));
        assertTrue(groups.get("group2").getAsMap().containsKey("k2"));
        assertTrue(groups.get("group2").getAsMap().containsKey("k3"));
    }

    @Test
    public void testMapForSettings() {
        Map<String, Object> map = new HashMap<>();
        map.put("hello", "world");
        Map<String, Object> settingsMap = new HashMap<>();
        settingsMap.put("map", map);
        Settings settings = Settings.settingsBuilder().loadFromMap(settingsMap).build();
        assertEquals("{map.hello=world}", settings.getAsMap().toString());
    }

    @Test
    public void testMapSettingsFromReader() {
        StringReader reader = new StringReader("{\"map\":{\"hello\":\"world\"}}");
        Map<String, Object> spec = XContentHelper.convertFromJsonToMap(reader);
        Settings settings = Settings.settingsBuilder().loadFromMap(spec).build();
        assertEquals("{map.hello=world}", settings.getAsMap().toString());
    }

    @Test
    public void testCurrentYearInSettings() {
        Settings settings = Settings.settingsBuilder()
                .put("date", "${yyyy}")
                .replacePropertyPlaceholders()
                .build();
        assertTrue(Integer.parseInt(settings.get("date")) > 2000);
    }

    @Test
    public void testSystemEnvironment() {
        Settings settings = Settings.settingsBuilder()
                .loadFromSystemEnvironment()
                .build();
        assertTrue(!settings.getAsMap().isEmpty());
    }

    @Test
    public void testSystemProperties() {
        Settings settings = Settings.settingsBuilder()
                .loadFromSystemProperties()
                .build();
        assertTrue(!settings.getAsMap().isEmpty());
    }

    @Test
    public void testPropertiesLoader() {
        Settings settings = Settings.settingsBuilder()
                .loadFromStream(".properties", new ByteArrayInputStream("a.b=c".getBytes(StandardCharsets.UTF_8)))
                .build();
        assertEquals("{a.b=c}", settings.getAsMap().toString());
    }

    @Test
    public void testFlatLoader() throws IOException {
        String s = "{\"a\":{\"b\":\"c\"}}";
        JsonSettingsLoader loader = new JsonSettingsLoader();
        Map<String, String> flatMap = loader.load(s);
        assertEquals("{a.b=c}", flatMap.toString());
    }

    @Test
    public void testFlatLoaderToJsonString() throws IOException {
        String s = "{\"a\":{\"b\":\"c\"}}";
        JsonSettingsLoader loader = new JsonSettingsLoader();
        String result = JsonXContent.contentBuilder().flatMap(loader.load(s)).string();
        assertEquals("{\"a.b\":\"c\"}", result);
    }

}
