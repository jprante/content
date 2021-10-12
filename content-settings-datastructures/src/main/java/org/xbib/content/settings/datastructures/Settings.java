package org.xbib.content.settings.datastructures;

import org.xbib.datastructures.api.ByteSizeValue;
import org.xbib.datastructures.api.TimeValue;
import org.xbib.datastructures.tiny.TinyMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Settings implements AutoCloseable {

    public static final Settings EMPTY_SETTINGS = new Builder().build();

    public static final String[] EMPTY_ARRAY = new String[0];

    private final TinyMap<String, String> map;

    private Settings(TinyMap<String, String> map) {
        this.map = map;
    }

    public static Settings fromMap(Map<String, Object> map) {
        Builder builder = new Builder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : null);
        }
        return builder.build();
    }

    public static void toMap(Settings settings, Map<String, Object> map) {
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

    public Map<String, String> getAsMap() {
        return this.map;
    }

    public Map<String, Object> getAsStructuredMap() {
        TinyMap.Builder<String, Object> stringObjectMap = TinyMap.builder();
        for (String key : map.keySet()) {
            String value = map.get(key);
            processSetting(stringObjectMap, "", key, value);
        }
        for (String key : stringObjectMap.keySet()) {
            Object object = stringObjectMap.get(key);
            if (object instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> valMap = (Map<String, Object>) object;
                stringObjectMap.put(key, convertMapsToArrays(valMap));
            }
        }
        return stringObjectMap.build();
    }

    public Settings getByPrefix(String prefix) {
        Builder builder = new Builder();
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (key.startsWith(prefix)) {
                if (key.length() < prefix.length()) {
                    continue;
                }
                builder.put(key.substring(prefix.length()), value);
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
        for (String key : map.keySet()) {
            if (key.startsWith(setting)) {
                return true;
            }
        }
        return false;
    }

    public String get(String setting) {
        return map.get(setting);
    }

    public String get(String setting, String defaultValue) {
        String s = map.get(setting);
        return s == null ? defaultValue : s;
    }

    public Float getAsFloat(String setting, Float defaultValue) {
        String s = get(setting);
        try {
            return s == null ? defaultValue : Float.parseFloat(s);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse float setting [" + setting + "] with value [" + s + "]", e);
        }
    }

    public Double getAsDouble(String setting, Double defaultValue) {
        String s = get(setting);
        try {
            return s == null ? defaultValue : Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse double setting [" + setting + "] with value [" + s + "]", e);
        }
    }

    public Integer getAsInt(String setting, Integer defaultValue) {
        String s = get(setting);
        try {
            return s == null ? defaultValue : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse int setting [" + setting + "] with value [" + s + "]", e);
        }
    }

    public Long getAsLong(String setting, Long defaultValue) {
        String s = get(setting);
        try {
            return s == null ? defaultValue : Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse long setting [" + setting + "] with value [" + s + "]", e);
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
        return TimeValue.parseTimeValue(get(setting), defaultValue);
    }

    public ByteSizeValue getAsBytesSize(String setting, ByteSizeValue defaultValue) {
        return ByteSizeValue.parseBytesSizeValue(get(setting), defaultValue);
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
        return result.toArray(new String[0]);
    }

    public Map<String, Settings> getGroups(String prefix) {
        String settingPrefix = prefix;
        if (settingPrefix.charAt(settingPrefix.length() - 1) != '.') {
            settingPrefix = settingPrefix + ".";
        }
        // we don't really care that it might happen twice
        TinyMap.Builder<String, TinyMap.Builder<String, String>> hashMap = TinyMap.builder();
        for (String o : this.map.keySet()) {
            if (o.startsWith(settingPrefix)) {
                String nameValue = o.substring(settingPrefix.length());
                int dotIndex = nameValue.indexOf('.');
                if (dotIndex == -1) {
                    throw new SettingsException("failed to get setting group for ["
                            + settingPrefix
                            + "] setting prefix and setting [" + o + "] because of a missing '.'");
                }
                String name = nameValue.substring(0, dotIndex);
                String value = nameValue.substring(dotIndex + 1);
                Map<String, String> groupSettings = hashMap.computeIfAbsent(name, k -> TinyMap.builder());
                groupSettings.put(value, get(o));
            }
        }
        TinyMap.Builder<String, Settings> retVal = TinyMap.builder();
        for (String key : hashMap.keySet()) {
            TinyMap.Builder<String, String> value = hashMap.get(key);
            retVal.put(key, new Settings(value.build()));
        }
        return retVal.build();
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
                for (String k : innerMap.keySet()) {
                    Object v = innerMap.get(k);
                    map.put(prefix + setting + "." + k, v);
                }
            }
            map.put(prefix + setting, value);
        } else {
            String key = setting.substring(0, prefixLength);
            String rest = setting.substring(prefixLength + 1);
            Object existingValue = map.get(prefix + key);
            if (existingValue == null) {
                Map<String, Object> newMap = TinyMap.builder();
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
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (isArray) {
                try {
                    int index = Integer.parseInt(key);
                    if (index >= 0) {
                        maxIndex = Math.max(maxIndex, index);
                    } else {
                        isArray = false;
                    }
                } catch (NumberFormatException ex) {
                    isArray = false;
                }
            }
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> valMap = (Map<String, Object>) value;
                map.put(key, convertMapsToArrays(valMap));
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

    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public void close() {
    }

    /**
     *
     */
    public static class Builder {

        private final SettingsLoaderService settingsLoaderService = SettingsLoaderService.getInstance();

        private final TinyMap.Builder<String, String> map;

        private Builder() {
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
         * @param resourceName resource name
         * @param inputStream input stream
         * @return builder
         */
        public Builder loadFromResource(String resourceName, InputStream inputStream) throws SettingsException {
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

        public Builder replacePropertyPlaceholders(PropertyPlaceholder propertyPlaceholder,
                                                   PlaceholderResolver placeholderResolver) {
            map.replaceAll((k, v) -> propertyPlaceholder.replacePlaceholders(v, placeholderResolver));
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
                        return map.get(placeholderName);
                    }
                }
            );
        }

        public Settings build() {
            return new Settings(map.build());
        }
    }
}
