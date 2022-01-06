package org.xbib.settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * A settings loader service for loading {@link SettingsLoader} implementations.
 */
public final class SettingsLoaderService {

    private static final SettingsLoaderService INSTANCE = new SettingsLoaderService();

    private final Map<Set<String>, SettingsLoader> settingsLoaderMap;

    private SettingsLoaderService() {
        this.settingsLoaderMap = new HashMap<>();
        ServiceLoader<SettingsLoader> serviceLoader = ServiceLoader.load(SettingsLoader.class);
        for (SettingsLoader settingsLoader : serviceLoader) {
            settingsLoaderMap.put(settingsLoader.suffixes(), settingsLoader);
        }
    }

    public static SettingsLoaderService getInstance() {
        return INSTANCE;
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
                if (resourceName.endsWith(suffix)) {
                    return entry.getValue();
                }
            }
        }
        throw new IllegalArgumentException("no settings loader for " + resourceName + " in " + settingsLoaderMap.keySet());
    }

    public Set<String> getSuffixes() {
        Set<String> suffixes = new HashSet<>();
        for (Set<String> set : settingsLoaderMap.keySet()) {
            suffixes.addAll(set);
        }
        return suffixes;
    }
}
