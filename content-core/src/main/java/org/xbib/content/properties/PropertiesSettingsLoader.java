package org.xbib.content.properties;

import org.xbib.content.io.BytesReference;
import org.xbib.content.SettingsLoader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Settings loader that loads (parses) the settings in a properties format.
 */
public class PropertiesSettingsLoader implements SettingsLoader {

    private static final Set<String> PROPERTIES_SUFFIXES = new HashSet<>(Collections.singletonList("properties"));

    @Override
    public Set<String> suffixes() {
        return PROPERTIES_SUFFIXES;
    }

    @Override
    public Map<String, String> load(String source) throws IOException {
        Properties props = new Properties();
        try (StringReader reader = new StringReader(source)) {
            props.load(reader);
            Map<String, String> result = new HashMap<>();
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                result.put((String) entry.getKey(), (String) entry.getValue());
            }
            return result;
        }
    }

    public Map<String, String> load(BytesReference ref) throws IOException {
        Properties props = new Properties();
        try (Reader reader = new InputStreamReader(ref.streamInput(), StandardCharsets.UTF_8)) {
            props.load(reader);
            Map<String, String> result = new HashMap<>();
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                result.put((String) entry.getKey(), (String) entry.getValue());
            }
            return result;
        }
    }

    @Override
    public Map<String, String> load(Map<String, Object> source) throws IOException {
        Properties props = new Properties();
        props.putAll(source);
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            result.put((String) entry.getKey(), (String) entry.getValue());
        }
        return result;
    }

    @Override
    public boolean canLoad(String source) {
        return true;
    }
}
