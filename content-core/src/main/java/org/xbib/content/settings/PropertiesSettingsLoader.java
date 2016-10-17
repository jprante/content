package org.xbib.content.settings;

import java.io.IOException;
import java.io.StringReader;
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

    @Override
    public boolean canLoad(String source) {
        return true;
    }
}
