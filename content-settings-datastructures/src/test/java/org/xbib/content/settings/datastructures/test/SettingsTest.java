package org.xbib.content.settings.datastructures.test;

import org.junit.jupiter.api.Test;
import org.xbib.content.settings.datastructures.Settings;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SettingsTest {

    @Test
    public void testEmpty() {
        Settings settings = Settings.EMPTY_SETTINGS;
        assertTrue(settings.getAsMap().isEmpty());
    }

    @Test
    public void testSimpleSettings() {
        Settings settings = Settings.settingsBuilder()
                .put("a", "b")
                .build();
        assertEquals("{a=b}", settings.getAsMap().toString());
        assertEquals("{a=b}", settings.getAsStructuredMap().toString());
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

}
