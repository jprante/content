package org.xbib.content.settings;

import org.xbib.content.PlaceholderResolver;
import org.xbib.content.PropertyPlaceholder;
import org.xbib.content.Settings;
import org.xbib.content.SettingsBuilder;
import org.xbib.content.SettingsLoader;
import org.xbib.datastructures.tiny.TinyMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ContentSettingsBuilder implements SettingsBuilder {

    private final Map<String, String> map;

    private Path path;

    private long initialDelay;

    private long period;

    private TimeUnit timeUnit;

    public ContentSettingsBuilder() {
        map = TinyMap.builder();
    }

    public Map<String, String> map() {
        return map;
    }

    public String remove(String key) {
        return map.remove(key);
    }

    public String get(String key) {
        return map.get(key);
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
    public ContentSettingsBuilder put(String key, String value) {
        map.put(key, value);
        return this;
    }

    /**
     * Sets a setting with the provided setting key and class as value.
     *
     * @param key   The setting key
     * @param clazz The setting class value
     * @return The builder
     */
    public ContentSettingsBuilder put(String key, Class<?> clazz) {
        map.put(key, clazz.getName());
        return this;
    }

    /**
     * Sets the setting with the provided setting key and the boolean value.
     *
     * @param setting The setting key
     * @param value   The boolean value
     * @return The builder
     */
    public ContentSettingsBuilder put(String setting, boolean value) {
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
    public ContentSettingsBuilder put(String setting, int value) {
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
    public ContentSettingsBuilder put(String setting, long value) {
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
    public ContentSettingsBuilder put(String setting, float value) {
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
    public ContentSettingsBuilder put(String setting, double value) {
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
    public ContentSettingsBuilder putArray(String setting, String... values) {
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
    public ContentSettingsBuilder putArray(String setting, List<String> values) {
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
    public ContentSettingsBuilder put(String settingPrefix, String groupName, String[] settings, String[] values)
            throws SettingsException {
        if (settings.length != values.length) {
            throw new SettingsException("The settings length must match the value length");
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
    public ContentSettingsBuilder put(Settings settings) {
        map.putAll(settings.getAsMap());
        return this;
    }

    /**
     * Sets all the provided settings.
     *
     * @param settings settings
     * @return a builder
     */
    public ContentSettingsBuilder put(Map<String, String> settings) {
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
    public ContentSettingsBuilder loadFromString(String source) {
        SettingsLoaderService settingsLoaderService = new SettingsLoaderService();
        SettingsLoader settingsLoader = settingsLoaderService.loaderFromString(source);
        try {
            Map<String, String> loadedSettings = settingsLoader.load(source);
            put(loadedSettings);
        } catch (Exception e) {
            throw new SettingsException("Failed to load settings from [" + source + "]", e);
        }
        return this;
    }

    /**
     * Loads settings from an URL.
     *
     * @param url url
     * @return builder
     */
    public ContentSettingsBuilder loadFromUrl(URL url) throws SettingsException {
        try {
            return loadFromResource(url.toExternalForm(), url.openStream());
        } catch (IOException e) {
            throw new SettingsException("Failed to open stream for url [" + url.toExternalForm() + "]", e);
        }
    }

    /**
     * Loads settings from a stream.
     *
     * @param resourceName resource name
     * @param inputStream  input stream
     * @return builder
     */
    @Override
    public ContentSettingsBuilder loadFromResource(String resourceName, InputStream inputStream) throws SettingsException {
        SettingsLoaderService settingsLoaderService = new SettingsLoaderService();
        SettingsLoader settingsLoader = settingsLoaderService.loaderFromResource(resourceName);
        try {
            Map<String, String> loadedSettings = settingsLoader
                    .load(ContentSettings.copyToString(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
            put(loadedSettings);
        } catch (Exception e) {
            throw new SettingsException("Failed to load settings from [" + resourceName + "]", e);
        }
        return this;
    }

    /**
     * Load system properties to this settings.
     *
     * @return builder
     */
    @Override
    public ContentSettingsBuilder loadFromSystemProperties() {
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
    public ContentSettingsBuilder loadFromSystemEnvironment() {
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Runs across all the settings set on this builder and replaces {@code ${...}} elements in the
     * each setting value according to the following logic:
     * <p>
     * First, tries to resolve it against a System property ({@link System#getProperty(String)}), next,
     * tries and resolve it against an environment variable ({@link System#getenv(String)}), next,
     * tries and resolve it against a date pattern to resolve the current date,
     * and last, tries and replace it with another setting already set on this builder.
     *
     * @return builder
     */
    public ContentSettingsBuilder replacePropertyPlaceholders() {
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

    public ContentSettingsBuilder replacePropertyPlaceholders(PropertyPlaceholder propertyPlaceholder,
                                                              PlaceholderResolver placeholderResolver) {
        map.replaceAll((k, v) -> propertyPlaceholder.replacePlaceholders(v, placeholderResolver));
        return this;
    }

    public ContentSettingsBuilder setRefresh(Path path, long initialDelay, long period, TimeUnit timeUnit) {
        this.path = path;
        this.initialDelay = initialDelay;
        this.period = period;
        this.timeUnit = timeUnit;
        return this;
    }

    public ContentSettings build() {
        return new ContentSettings(map, path, initialDelay, period, timeUnit);
    }
}
