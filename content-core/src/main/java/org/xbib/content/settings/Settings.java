package org.xbib.content.settings;

import static org.xbib.content.util.unit.ByteSizeValue.parseBytesSizeValue;
import static org.xbib.content.util.unit.TimeValue.parseTimeValue;

import org.xbib.content.XContentBuilder;
import org.xbib.content.json.JsonSettingsLoader;
import org.xbib.content.json.JsonXContent;
import org.xbib.content.util.unit.ByteSizeValue;
import org.xbib.content.util.unit.TimeValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Settings {

    private static final Logger logger = Logger.getLogger(Settings.class.getName());

    public static final Settings EMPTY_SETTINGS = new Builder().build();
    public static final String[] EMPTY_ARRAY = new String[0];
    public static final int BUFFER_SIZE = 1024 * 8;
    private final Map<String, String> map;

    private Settings(Map<String, String> settings) {
        this.map = new HashMap<>(settings);
    }

    public static Settings readSettingsFromMap(Map<String, Object> map) throws IOException {
        Builder builder = new Builder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : null);
        }
        return builder.build();
    }

    public static void writeSettingsToMap(Settings settings, Map<String, Object> map) throws IOException {
        for (String key : settings.getAsMap().keySet()) {
            map.put(key, settings.get(key));
        }
    }

    /**
     * Returns a builder to be used in order to build settings.
     * @return a builder
     */
    public static Builder settingsBuilder() {
        return new Builder();
    }

    public static String[] splitStringByCommaToArray(final String s) {
        return splitStringToArray(s, ',');
    }

    public static String[] splitStringToArray(final String s, final char c) {
        if (s.length() == 0) {
            return EMPTY_ARRAY;
        }
        final char[] chars = s.toCharArray();
        int count = 1;
        for (final char x : chars) {
            if (x == c) {
                count++;
            }
        }
        final String[] result = new String[count];
        final int len = chars.length;
        int start = 0;
        int pos = 0;
        int i = 0;
        for (; pos < len; pos++) {
            if (chars[pos] == c) {
                int size = pos - start;
                if (size > 0) {
                    result[i++] = new String(chars, start, size);
                }
                start = pos + 1;
            }
        }
        int size = pos - start;
        if (size > 0) {
            result[i++] = new String(chars, start, size);
        }
        if (i != count) {
            String[] result1 = new String[i];
            System.arraycopy(result, 0, result1, 0, i);
            return result1;
        }
        return result;
    }

    public static String copyToString(Reader in) throws IOException {
        StringWriter out = new StringWriter();
        copy(in, out);
        return out.toString();
    }

    public static int copy(final Reader in, final Writer out) throws IOException {
        try (Reader reader = in; Writer writer = out) {
            int byteCount = 0;
            char[] buffer = new char[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            writer.flush();
            return byteCount;
        }
    }

    public Map<String, String> getAsMap() {
        return this.map;
    }

    public Map<String, Object> getAsStructuredMap() {
        Map<String, Object> stringObjectMap = new HashMap<>(2);
        for (Map.Entry<String, String> entry : this.map.entrySet()) {
            processSetting(stringObjectMap, "", entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Object> entry : stringObjectMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> valMap = (Map<String, Object>) entry.getValue();
                entry.setValue(convertMapsToArrays(valMap));
            }
        }
        return stringObjectMap;
    }

    public Settings getByPrefix(String prefix) {
        Builder builder = new Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                if (entry.getKey().length() < prefix.length()) {
                    continue;
                }
                builder.put(entry.getKey().substring(prefix.length()), entry.getValue());
            }
        }
        return builder.build();
    }

    public Settings getAsSettings(String setting) {
        return getByPrefix(setting + ".");
    }

    public boolean containsSetting(String setting) {
        if (map.containsKey(setting)) {
            return true;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().startsWith(setting)) {
                return true;
            }
        }
        return false;
    }

    public String get(String setting) {
        String retVal = map.get(setting);
        if (retVal != null) {
            return retVal;
        }
        return null;
    }

    public String get(String setting, String defaultValue) {
        String retVal = map.get(setting);
        return retVal == null ? defaultValue : retVal;
    }

    public Float getAsFloat(String setting, Float defaultValue) {
        String sValue = get(setting);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(sValue);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse float setting [" + setting + "] with value [" + sValue + "]", e);
        }
    }

    public Double getAsDouble(String setting, Double defaultValue) {
        String sValue = get(setting);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(sValue);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse double setting [" + setting + "] with value [" + sValue + "]", e);
        }
    }

    public Integer getAsInt(String setting, Integer defaultValue) {
        String sValue = get(setting);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(sValue);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse int setting [" + setting + "] with value [" + sValue + "]", e);
        }
    }

    public Long getAsLong(String setting, Long defaultValue) {
        String sValue = get(setting);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(sValue);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse long setting [" + setting + "] with value [" + sValue + "]", e);
        }
    }

    public Boolean getAsBoolean(String setting, Boolean defaultValue) {
        String value = get(setting);
        if (value == null) {
            return defaultValue;
        }
        return !("false".equals(value) || "0".equals(value) || "off".equals(value) || "no".equals(value));
    }

    public TimeValue getAsTime(String setting, TimeValue defaultValue) {
        return parseTimeValue(get(setting), defaultValue);
    }

    public ByteSizeValue getAsBytesSize(String setting, ByteSizeValue defaultValue) {
        return parseBytesSizeValue(get(setting), defaultValue);
    }

    public String[] getAsArray(String settingPrefix) {
        return getAsArray(settingPrefix, EMPTY_ARRAY);
    }

    public String[] getAsArray(String settingPrefix, String[] defaultArray) {
        List<String> result = new ArrayList<>();
        if (get(settingPrefix) != null) {
            String[] strings = splitStringByCommaToArray(get(settingPrefix));
            if (strings.length > 0) {
                for (String string : strings) {
                    result.add(string.trim());
                }
            }
        }
        int counter = 0;
        while (true) {
            String value = get(settingPrefix + '.' + (counter++));
            if (value == null) {
                break;
            }
            result.add(value.trim());
        }
        if (result.isEmpty()) {
            return defaultArray;
        }
        return result.toArray(new String[result.size()]);
    }

    public Map<String, Settings> getGroups(String prefix) {
        String settingPrefix = prefix;
        if (settingPrefix.charAt(settingPrefix.length() - 1) != '.') {
            settingPrefix = settingPrefix + ".";
        }
        // we don't really care that it might happen twice
        Map<String, Map<String, String>> hashMap = new LinkedHashMap<>();
        for (Object o : this.map.keySet()) {
            String setting = (String) o;
            if (setting.startsWith(settingPrefix)) {
                String nameValue = setting.substring(settingPrefix.length());
                int dotIndex = nameValue.indexOf('.');
                if (dotIndex == -1) {
                    throw new SettingsException("Failed to get setting group for ["
                            + settingPrefix
                            + "] setting prefix and setting [" + setting + "] because of a missing '.'");
                }
                String name = nameValue.substring(0, dotIndex);
                String value = nameValue.substring(dotIndex + 1);
                Map<String, String> groupSettings = hashMap.get(name);
                if (groupSettings == null) {
                    groupSettings = new LinkedHashMap<>();
                    hashMap.put(name, groupSettings);
                }
                groupSettings.put(value, get(setting));
            }
        }
        Map<String, Settings> retVal = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : hashMap.entrySet()) {
            retVal.put(entry.getKey(), new Settings(Collections.unmodifiableMap(entry.getValue())));
        }
        return Collections.unmodifiableMap(retVal);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && map.equals(((Settings) o).map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    private void processSetting(Map<String, Object> map, String prefix, String setting, String value) {
        int prefixLength = setting.indexOf('.');
        if (prefixLength == -1) {
            @SuppressWarnings("unchecked")
            Map<String, Object> innerMap = (Map<String, Object>) map.get(prefix + setting);
            if (innerMap != null) {
                for (Map.Entry<String, Object> entry : innerMap.entrySet()) {
                    map.put(prefix + setting + "." + entry.getKey(), entry.getValue());
                }
            }
            map.put(prefix + setting, value);
        } else {
            String key = setting.substring(0, prefixLength);
            String rest = setting.substring(prefixLength + 1);
            Object existingValue = map.get(prefix + key);
            if (existingValue == null) {
                Map<String, Object> newMap = new HashMap<>(2);
                processSetting(newMap, "", rest, value);
                map.put(key, newMap);
            } else {
                if (existingValue instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> innerMap = (Map<String, Object>) existingValue;
                    processSetting(innerMap, "", rest, value);
                    map.put(key, innerMap);
                } else {
                    processSetting(map, prefix + key + ".", rest, value);
                }
            }
        }
    }

    private Object convertMapsToArrays(Map<String, Object> map) {
        if (map.isEmpty()) {
            return map;
        }
        boolean isArray = true;
        int maxIndex = -1;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (isArray) {
                try {
                    int index = Integer.parseInt(entry.getKey());
                    if (index >= 0) {
                        maxIndex = Math.max(maxIndex, index);
                    } else {
                        isArray = false;
                    }
                } catch (NumberFormatException ex) {
                    isArray = false;
                }
            }
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> valMap = (Map<String, Object>) entry.getValue();
                entry.setValue(convertMapsToArrays(valMap));
            }
        }
        if (isArray && (maxIndex + 1) == map.size()) {
            ArrayList<Object> newValue = new ArrayList<>(maxIndex + 1);
            for (int i = 0; i <= maxIndex; i++) {
                Object obj = map.get(Integer.toString(i));
                if (obj == null) {
                    return map;
                }
                newValue.add(obj);
            }
            return newValue;
        }
        return map;
    }

    /**
     *
     */
    public static class Builder {

        private final Map<String, String> map = new LinkedHashMap<>();

        private Builder() {
        }

        public Map<String, String> internalMap() {
            return this.map;
        }

        public String remove(String key) {
            return map.remove(key);
        }

        public String get(String key) {
            return map.get(key);
        }

        /**
         * Sets a setting with the provided setting key and value.
         *
         * @param key   The setting key
         * @param value The setting value
         * @return The builder
         */
        public Builder put(String key, String value) {
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
        public Builder put(String key, Class<?> clazz) {
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
        public Builder put(String setting, boolean value) {
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
        public Builder put(String setting, int value) {
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
        public Builder put(String setting, long value) {
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
        public Builder put(String setting, float value) {
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
        public Builder put(String setting, double value) {
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
        public Builder putArray(String setting, String... values) {
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
        public Builder putArray(String setting, List<String> values) {
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
         * @param settingPrefix setting prefix
         * @param groupName group name
         * @param settings settings
         * @param values values
         * @return a builder
         * @throws SettingsException if setting fails
         */
        public Builder put(String settingPrefix, String groupName, String[] settings, String[] values)
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
         * @param settings settings
         * @return builder
         */
        public Builder put(Settings settings) {
            map.putAll(settings.getAsMap());
            return this;
        }

        /**
         * Sets all the provided settings.
         *
         * @param settings settings
         * @return a builder
         */
        public Builder put(Map<String, String> settings) {
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
        public Builder loadFromString(String source) {
            SettingsLoader settingsLoader = SettingsLoaderService.loaderFromString(source);
            try {
                Map<String, String> loadedSettings = settingsLoader.load(source);
                put(loadedSettings);
            } catch (Exception e) {
                throw new SettingsException("Failed to load settings from [" + source + "]", e);
            }
            return this;
        }

        /**
         * Loads settings from a map.
         * @param map map
         * @return builder
         */
        public Builder loadFromMap(Map<String, Object> map) {
            try (XContentBuilder builder = JsonXContent.contentBuilder()) {
                put(new JsonSettingsLoader().load(builder.map(map).string()));
            } catch (Exception e) {
                throw new SettingsException("Failed to load settings from [" + map + "]", e);
            }
            return this;
        }

        /**
         * Loads settings from an URL.
         * @param url url
         * @return builder
         */
        public Builder loadFromUrl(URL url) throws SettingsException {
            try {
                return loadFromStream(url.toExternalForm(), url.openStream());
            } catch (IOException e) {
                throw new SettingsException("Failed to open stream for url [" + url.toExternalForm() + "]", e);
            }
        }

        /**
         * Loads settings from a stream.
         * @param resourceName resource name
         * @param inputStream input stream
         * @return builder
         */
        public Builder loadFromStream(String resourceName, InputStream inputStream) throws SettingsException {
            SettingsLoader settingsLoader = SettingsLoaderService.loaderFromResource(resourceName);
            try {
                Map<String, String> loadedSettings = settingsLoader
                        .load(copyToString(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
                put(loadedSettings);
            } catch (Exception e) {
                throw new SettingsException("Failed to load settings from [" + resourceName + "]", e);
            }
            return this;
        }

        /**
         * Load system properties to this settings.
         * @return builder
         */
        public Builder loadFromSystemProperties() {
            for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
                put((String) entry.getKey(), (String) entry.getValue());
            }
            return this;
        }

        /**
         * Load system environment to this settings.
         * @return builder
         */
        public Builder loadFromSystemEnvironment() {
            for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
            return this;
        }
        /**
         * Runs across all the settings set on this builder and replaces {@code ${...}} elements in the
         * each setting value according to the following logic:
         *
         * First, tries to resolve it against a System property ({@link System#getProperty(String)}), next,
         * tries and resolve it against an environment variable ({@link System#getenv(String)}), next,
         * tries and resolve it against a date pattern to resolve the current date,
         * and last, tries and replace it with another setting already set on this builder.
         * @return builder
         */
        public Builder replacePropertyPlaceholders() {
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
                            logger.log(Level.FINER, e.getMessage(), e);
                            return map.get(placeholderName);
                        }
                    }
            );
        }

        public Builder replacePropertyPlaceholders(PropertyPlaceholder propertyPlaceholder,
                                                   PlaceholderResolver placeholderResolver) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                map.put(entry.getKey(), propertyPlaceholder.replacePlaceholders(entry.getValue(), placeholderResolver));
            }
            return this;
        }

        public Settings build() {
            return new Settings(map);
        }
    }
}
