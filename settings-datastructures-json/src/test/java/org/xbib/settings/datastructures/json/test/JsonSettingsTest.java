package org.xbib.settings.datastructures.json.test;

import org.junit.jupiter.api.Test;
import org.xbib.settings.Settings;
import org.xbib.settings.SettingsLoader;
import org.xbib.settings.datastructures.json.JsonSettingsLoader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonSettingsTest {

    @Test
    public void testMapForSettings() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("hello", "world");
        Map<String, Object> settingsMap = new HashMap<>();
        settingsMap.put("map", map);
        SettingsLoader settingsLoader = new JsonSettingsLoader();
        Settings settings = Settings.settingsBuilder()
                .put(settingsLoader.load(settingsMap))
                .build();
        assertEquals("{map.hello=world}", settings.getAsMap().toString());
    }

    @Test
    public void testMapSettingsFromReader() throws IOException {
        Map<String, Object> map =  Map.of("map", Map.of("hello", "world"));
        SettingsLoader settingsLoader = new JsonSettingsLoader();
        Settings settings = Settings.settingsBuilder()
                .put(settingsLoader.load(map))
                .build();
        assertEquals("{map.hello=world}", settings.getAsMap().toString());
    }

    @Test
    public void testFlatLoader() throws IOException {
        String s = "{\"a\":{\"b\":\"c\"}}";
        SettingsLoader loader = new JsonSettingsLoader();
        Map<String, String> flatMap = loader.load(s);
        assertEquals("{a.b=c}", flatMap.toString());
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
        List<String> list = Arrays.asList("a", "b");
        map.put("code", code);
        map.put("name", name);
        map.put("list", list);
        map.put("null", null);
        SettingsLoader loader = new JsonSettingsLoader();
        Map<String, String> result = loader.load(map);
        assertEquals("{code.a=b, code.b=c, name.a=b, name.b=c, list.0=a, list.1=b, null=null}", result.toString());
    }
}
