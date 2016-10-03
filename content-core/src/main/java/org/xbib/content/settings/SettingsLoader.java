package org.xbib.content.settings;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Provides the ability to load settings from
 * the actual source content that represents them.
 */
public interface SettingsLoader {

    Set<String> suffixes();

    /**
     * Loads the settings from a source string.
     * @param source the source
     * @return a Map
     * @throws IOException if load fails
     */
    Map<String, String> load(String source) throws IOException;

    boolean canLoad(String source);
}
