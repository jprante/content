package org.xbib.settings.content.test;

import org.junit.jupiter.api.Test;
import org.xbib.content.core.XContentHelper;
import org.xbib.content.io.BytesArray;
import org.xbib.content.io.BytesReference;
import org.xbib.content.json.JsonXContent;
import org.xbib.settings.Settings;
import org.xbib.settings.SettingsLoader;
import org.xbib.settings.content.json.JsonSettingsLoader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
public class SettingsTest {

    @Test
    public void testEmpty() {
        Settings settings = Settings.emptySettings();
        assertTrue(settings.isEmpty());
    }

    @Test
    public void testArray() {
        Settings settings = Settings.settingsBuilder()
                .putArray("input", Arrays.asList("a", "b", "c")).build();
        assertEquals("a", settings.getAsArray("input")[0]);
        assertEquals("b", settings.getAsArray("input")[1]);
        assertEquals("c", settings.getAsArray("input")[2]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testArrayOfMaps() {
        Settings settings = Settings.settingsBuilder()
                .put("location.0.code", "Code 0")
                .put("location.0.name", "Name 0")
                .put("location.1.code", "Code 1")
                .put("location.1.name", "Name 1")
                .build();

        // turn map with index keys 0,1,... into a list of maps
        Map<String, Object> map = settings.getAsSettings("location").getAsStructuredMap();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            list.add((Map<String, Object>) entry.getValue());
        }
        assertEquals("[{code=Code 0, name=Name 0}, {code=Code 1, name=Name 1}]", list.toString());
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
    public void testMapForSettings() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("hello", "world");
        Map<String, Object> settingsMap = new HashMap<>();
        settingsMap.put("map", map);
        SettingsLoader settingsLoader = new JsonSettingsLoader();
        Settings settings = Settings.settingsBuilder()
                .put(settingsLoader.load(settingsMap)).build();
        assertEquals("{map.hello=world}", settings.getAsMap().toString());
    }

    @Test
    public void testMapSettingsFromReader() throws IOException {
        StringReader reader = new StringReader("{\"map\":{\"hello\":\"world\"}}");
        Map<String, Object> map = XContentHelper.convertFromContentToMap(JsonXContent.jsonContent(), reader);
        SettingsLoader settingsLoader = new JsonSettingsLoader();
        Settings settings = Settings.settingsBuilder()
                .put(settingsLoader.load(map))
                .build();
        assertEquals("{map.hello=world}", settings.getAsMap().toString());
    }

    @Test
    public void testCurrentYearInSettings() {
        Settings settings = Settings.settingsBuilder()
                .put("date", "${yyyy}")
                .replacePropertyPlaceholders()
                .build();
        assertEquals(LocalDate.now().getYear(), Integer.parseInt(settings.get("date")));
    }

    @Test
    public void testSystemEnvironment() {
        Settings settings = Settings.settingsBuilder()
                .loadFromSystemEnvironment()
                .build();
        assertFalse(settings.getAsMap().isEmpty());
    }

    @Test
    public void testSystemProperties() {
        Settings settings = Settings.settingsBuilder()
                .loadFromSystemProperties()
                .build();
        assertFalse(settings.getAsMap().isEmpty());
    }

    @Test
    public void testPropertiesLoader() {
        Settings settings = Settings.settingsBuilder()
                .loadFromResource(".properties", new ByteArrayInputStream("a.b=c".getBytes(StandardCharsets.UTF_8)))
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

    @Test
    public void testFlatMapAsString() throws IOException {
        String s = "{\"a\":{\"b\":\"c\"}}";
        BytesReference ref = new BytesArray(s.getBytes(StandardCharsets.UTF_8));
        JsonSettingsLoader loader = new JsonSettingsLoader();
        String result = loader.flatMapAsString(ref);
        assertEquals("{\"a.b\":\"c\"}", result);
    }

    @Test
    public void testLoadFromMap() throws IOException {
        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> code = new LinkedHashMap<>();
        code.put("a", "b");
        code.put("b", "c");
        Map<String, Object> name = new LinkedHashMap<>();
        name.put("a", "b");
        name.put("b", "c");
        List<String> list = Arrays.asList("a","b");
        map.put("code", code);
        map.put("name", name);
        map.put("list", list);
        map.put("null", null);
        JsonSettingsLoader loader = new JsonSettingsLoader();
        Map<String, String> result = loader.load(map);
        assertEquals("{code.a=b, code.b=c, name.a=b, name.b=c, list.0=a, list.1=b, null=null}", result.toString());
    }

    @Test
    public void testRefresher() throws Exception {
        Settings settings = Settings.settingsBuilder()
                .put("name", "hello")
                .setRefresh(Paths.get("src/test/resources/settings.json"), 1L, 1L, TimeUnit.SECONDS)
                .build();
        assertEquals("hello", settings.get("name"));
        Thread.sleep(2000L);
        assertEquals("world", settings.get("name"));
        settings.close();
    }
}
