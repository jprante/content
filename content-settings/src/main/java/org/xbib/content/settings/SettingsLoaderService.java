package org.xbib.content.settings;

import org.xbib.content.SettingsLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * A settings loader service for loading {@link SettingsLoader} implementations.
 */
public final class SettingsLoaderService {

    private final Map<Set<String>, SettingsLoader> settingsLoaderMap;

    public SettingsLoaderService() {
        this.settingsLoaderMap = new HashMap<>();
        ServiceLoader<SettingsLoader> serviceLoader = ServiceLoader.load(SettingsLoader.class);
        for (SettingsLoader settingsLoader : serviceLoader) {
            if (!settingsLoaderMap.containsKey(settingsLoader.suffixes())) {
                settingsLoaderMap.put(settingsLoader.suffixes(), settingsLoader);
            }
        }
    }

    /**
     * Returns a {@link SettingsLoader} based on the resource name.
     * @param resourceName the resource
     * @return the settings loader
     */
    public SettingsLoader loaderFromResource(String resourceName) {
        for (Map.Entry<Set<String>, SettingsLoader> entry : settingsLoaderMap.entrySet()) {
            Set<String> suffixes = entry.getKey();
            for (String suffix : suffixes) {
                if (resourceName.endsWith("." + suffix)) {
                    return entry.getValue();
                }
            }
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a {@link SettingsLoader} based on the actual source.
     * @param source the source
     * @return the settings loader
     */
    public SettingsLoader loaderFromString(String source) {
        for (SettingsLoader loader : settingsLoaderMap.values()) {
            if (loader.canLoad(source)) {
                return loader;
            }
        }
        throw new UnsupportedOperationException();
    }
}
