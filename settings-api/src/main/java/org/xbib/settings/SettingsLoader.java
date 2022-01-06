package org.xbib.settings;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Provides the ability to load settings from
 * the actual source content that represents them.
 */
public interface SettingsLoader {

    /**
     * Suffices for file names to load from.
     * @return a set of suffices
     */
    Set<String> suffixes();

    /**
     * Loads the settings from a source string.
     * @param source the source
     * @return a Map
     */
    Map<String, String> load(String source) throws IOException;

    /**
     * Loads the settings from a map.
     * @param source the map with the source
     * @return a Map
     */
    Map<String, String> load(Map<String, Object> source) throws IOException;

}
