package org.xbib.content.settings.datastructures;

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
     * @throws IOException if load fails
     */
    Map<String, String> load(String source) throws IOException;

    Map<String, String> load(Map<String, Object> source) throws IOException;

    boolean canLoad(String source);
}
