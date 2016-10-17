package org.xbib.content.settings;

import org.xbib.content.json.JsonSettingsLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A settings loader service for loading {@link SettingsLoader} implementations.
 */
public final class SettingsLoaderService {

    private static final Logger logger = Logger.getLogger(SettingsLoaderService.class.getName());

    private static final Map<Set<String>, SettingsLoader> settingsLoaderMap;

    static {
        settingsLoaderMap = new HashMap<>();
        try {
            ServiceLoader<SettingsLoader> serviceLoader = ServiceLoader.load(SettingsLoader.class);
            for (SettingsLoader settingsLoader : serviceLoader) {
                if (!settingsLoaderMap.containsKey(settingsLoader.suffixes())) {
                    settingsLoaderMap.put(settingsLoader.suffixes(), settingsLoader);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private SettingsLoaderService() {
    }

    /**
     * Returns a {@link SettingsLoader} based on the resource name.
     * @param resourceName the resource
     * @return the settings loader
     */
    public static SettingsLoader loaderFromResource(String resourceName) {
        for (Map.Entry<Set<String>, SettingsLoader> entry : settingsLoaderMap.entrySet()) {
            Set<String> suffixes = entry.getKey();
            for (String suffix : suffixes) {
                if (resourceName.endsWith("." + suffix)) {
                    return entry.getValue();
                }
            }
        }
        return new JsonSettingsLoader();
    }

    /**
     * Returns a {@link SettingsLoader} based on the actual source.
     * @param source the source
     * @return the settings loader
     */
    public static SettingsLoader loaderFromString(String source) {
        for (SettingsLoader loader : settingsLoaderMap.values()) {
            if (loader.canLoad(source)) {
                return loader;
            }
        }
        return new JsonSettingsLoader();
    }
}
