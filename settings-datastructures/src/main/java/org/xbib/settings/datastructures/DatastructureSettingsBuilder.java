package org.xbib.settings.datastructures;

import org.xbib.settings.PlaceholderResolver;
import org.xbib.settings.PropertyPlaceholder;
import org.xbib.settings.Settings;
import org.xbib.settings.SettingsBuilder;
import org.xbib.settings.SettingsException;
import org.xbib.settings.SettingsLoader;
import org.xbib.settings.SettingsLoaderService;
import org.xbib.datastructures.tiny.TinyMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
public class DatastructureSettingsBuilder implements SettingsBuilder {

    private final SettingsLoaderService settingsLoaderService = SettingsLoaderService.getInstance();

    private final TinyMap.Builder<String, String> map;

    public DatastructureSettingsBuilder() {
        map = TinyMap.builder();
    }

    public String remove(String key) {
        return map.remove(key);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Sets a setting with the provided setting key and value.
     *
     * @param key   The setting key
     * @param value The setting value
     * @return The builder
     */
    @Override
    public DatastructureSettingsBuilder put(String key, String value) {
        map.put(key, value);
        return this;
    }

    /**
     * Sets the setting with the provided setting key and the boolean value.
     *
     * @param setting The setting key
     * @param value   The boolean value
     * @return The builder
     */
    @Override
    public DatastructureSettingsBuilder put(String setting, boolean value) {
        put(setting, String.valueOf(value));
        return this;
    }

    /**
     * Sets the setting with the provided setting key and the int value.
     *
     * @param setting The setting key
     * @param value   The int value
     * @return The builder
     */
    @Override
    public DatastructureSettingsBuilder put(String setting, int value) {
        put(setting, String.valueOf(value));
        return this;
    }

    /**
     * Sets the setting with the provided setting key and the long value.
     *
     * @param setting The setting key
     * @param value   The long value
     * @return The builder
     */
    @Override
    public DatastructureSettingsBuilder put(String setting, long value) {
        put(setting, String.valueOf(value));
        return this;
    }

    /**
     * Sets the setting with the provided setting key and the float value.
     *
     * @param setting The setting key
     * @param value   The float value
     * @return The builder
     */
    @Override
    public DatastructureSettingsBuilder put(String setting, float value) {
        put(setting, String.valueOf(value));
        return this;
    }

    /**
     * Sets the setting with the provided setting key and the double value.
     *
     * @param setting The setting key
     * @param value   The double value
     * @return The builder
     */
    @Override
    public DatastructureSettingsBuilder put(String setting, double value) {
        put(setting, String.valueOf(value));
        return this;
    }

    /**
     * Sets the setting with the provided setting key and an array of values.
     *
     * @param setting The setting key
     * @param values  The values
     * @return The builder
     */
    @Override
    public DatastructureSettingsBuilder putArray(String setting, String... values) {
        remove(setting);
        int counter = 0;
        while (true) {
            String value = map.remove(setting + '.' + (counter++));
            if (value == null) {
                break;
            }
        }
        for (int i = 0; i < values.length; i++) {
            put(setting + '.' + i, values[i]);
        }
        return this;
    }

    /**
     * Sets the setting with the provided setting key and an array of values.
     *
     * @param setting The setting key
     * @param values  The values
     * @return The builder
     */
    @Override
    public DatastructureSettingsBuilder putArray(String setting, List<String> values) {
        remove(setting);
        int counter = 0;
        while (true) {
            String value = map.remove(setting + '.' + (counter++));
            if (value == null) {
                break;
            }
        }
        for (int i = 0; i < values.size(); i++) {
            put(setting + '.' + i, values.get(i));
        }
        return this;
    }

    /**
     * Sets the setting group.
     *
     * @param settingPrefix setting prefix
     * @param groupName     group name
     * @param settings      settings
     * @param values        values
     * @return a builder
     * @throws SettingsException if setting fails
     */
    @Override
    public DatastructureSettingsBuilder put(String settingPrefix, String groupName, String[] settings, String[] values)
            throws SettingsException {
        if (settings.length != values.length) {
            throw new SettingsException("the settings length must match the value length");
        }
        for (int i = 0; i < settings.length; i++) {
            if (values[i] == null) {
                continue;
            }
            put(settingPrefix + "" + groupName + "." + settings[i], values[i]);
        }
        return this;
    }

    /**
     * Sets all the provided settings.
     *
     * @param settings settings
     * @return builder
     */
    @Override
    public DatastructureSettingsBuilder put(Settings settings) {
        map.putAll(settings.getAsMap());
        return this;
    }

    /**
     * Sets all the provided settings.
     *
     * @param settings settings
     * @return a builder
     */
    @Override
    public DatastructureSettingsBuilder put(Map<String, String> settings) {
        map.putAll(settings);
        return this;
    }

    /**
     * Loads settings from the actual string content that represents them using the
     * {@link SettingsLoaderService#loaderFromString(String)}.
     *
     * @param source source
     * @return builder
     */
    @Override
    public DatastructureSettingsBuilder loadFromString(String source) {
        SettingsLoader settingsLoader = settingsLoaderService.loaderFromString(source);
        try {
            Map<String, String> loadedSettings = settingsLoader.load(source);
            put(loadedSettings);
        } catch (Exception e) {
            throw new SettingsException("failed to load settings from [" + source + "]", e);
        }
        return this;
    }

    /**
     * Loads settings from a resource.
     *
     * @param resourceName resource name
     * @param inputStream  input stream
     * @return builder
     */
    @Override
    public DatastructureSettingsBuilder loadFromResource(String resourceName, InputStream inputStream) throws SettingsException {
        SettingsLoader settingsLoader = settingsLoaderService.loaderFromResource(resourceName);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            Map<String, String> loadedSettings = settingsLoader.load(bufferedReader.lines().collect(Collectors.joining()));
            put(loadedSettings);
        } catch (Exception e) {
            throw new SettingsException("failed to load settings from [" + resourceName + "]", e);
        }
        return this;
    }

    /**
     * Load system properties to this settings.
     *
     * @return builder
     */
    @Override
    public DatastructureSettingsBuilder loadFromSystemProperties() {
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            put((String) entry.getKey(), (String) entry.getValue());
        }
        return this;
    }

    /**
     * Load system environment to this settings.
     *
     * @return builder
     */
    @Override
    public DatastructureSettingsBuilder loadFromSystemEnvironment() {
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public DatastructureSettingsBuilder replacePropertyPlaceholders(PropertyPlaceholder propertyPlaceholder,
                                                                    PlaceholderResolver placeholderResolver) {
        map.replaceAll((k, v) -> propertyPlaceholder.replacePlaceholders(v, placeholderResolver));
        return this;
    }

    @Override
    public DatastructureSettingsBuilder replacePropertyPlaceholders() {
        return replacePropertyPlaceholders(new PropertyPlaceholder("${", "}", false),
                placeholderName -> {
                    // system property
                    String value = System.getProperty(placeholderName);
                    if (value != null) {
                        return value;
                    }
                    // environment
                    value = System.getenv(placeholderName);
                    if (value != null) {
                        return value;
                    }
                    // current date
                    try {
                        return DateTimeFormatter.ofPattern(placeholderName).format(LocalDate.now());
                    } catch (IllegalArgumentException | DateTimeException e) {
                        return map.get(placeholderName);
                    }
                }
        );
    }

    @Override
    public DatastructureSettingsBuilder setRefresh(Path path, long initialDelay, long period, TimeUnit timeUnit) {
        return this;
    }

    @Override
    public SettingsBuilder map(Function<Map.Entry<String, String>, Map.Entry<String, String>> function) {
        map.entrySet().stream().map(function).forEach(e -> put(e.getKey(), e.getValue()));
        return this;
    }

    @Override
    public DatastructureSettings build() {
        return new DatastructureSettings(map.build());
    }
}
