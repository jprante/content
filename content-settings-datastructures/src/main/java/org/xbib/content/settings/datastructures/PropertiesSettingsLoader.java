package org.xbib.content.settings.datastructures;

import org.xbib.datastructures.tiny.TinyMap;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Settings loader that loads (parses) the settings in a properties format.
 */
public class PropertiesSettingsLoader implements SettingsLoader {

    @Override
    public Set<String> suffixes() {
        return Set.of("properties");
    }

    @Override
    public Map<String, String> load(String source) throws IOException {
        Properties props = new Properties();
        try (StringReader reader = new StringReader(source)) {
            props.load(reader);
            TinyMap.Builder<String, String> result = TinyMap.builder();
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                result.put((String) entry.getKey(), (String) entry.getValue());
            }
            return result.build();
        }
    }

    @Override
    public Map<String, String> load(Map<String, Object> source) {
        Properties props = new Properties();
        props.putAll(source);
        TinyMap.Builder<String, String> result = TinyMap.builder();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            result.put((String) entry.getKey(), (String) entry.getValue());
        }
        return result.build();
    }

    @Override
    public boolean canLoad(String source) {
        return true;
    }
}
