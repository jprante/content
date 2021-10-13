package org.xbib.content.settings.datastructures;

import org.xbib.content.Settings;
import org.xbib.content.SettingsException;
import org.xbib.datastructures.api.ByteSizeValue;
import org.xbib.datastructures.api.TimeValue;
import org.xbib.datastructures.tiny.TinyMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatastructureSettings implements Settings {

    private static final String[] EMPTY_ARRAY = new String[0];

    private final TinyMap<String, String> map;

    protected DatastructureSettings(TinyMap<String, String> map) {
        this.map = map;
    }

    public static DatastructureSettings fromMap(Map<String, Object> map) {
        DatastructureSettingsBuilder builder = new DatastructureSettingsBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : null);
        }
        return builder.build();
    }

    public static void toMap(DatastructureSettings settings, Map<String, Object> map) {
        for (String key : settings.getAsMap().keySet()) {
            map.put(key, settings.get(key));
        }
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

    @Override
    public Map<String, String> getAsMap() {
        return this.map;
    }

    @Override
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

    @Override
    public Settings getByPrefix(String prefix) {
        DatastructureSettingsBuilder builder = new DatastructureSettingsBuilder();
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

    @Override
    public Settings getAsSettings(String setting) {
        return getByPrefix(setting + ".");
    }

    @Override
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

    @Override
    public String get(String setting) {
        return map.get(setting);
    }

    @Override
    public String get(String setting, String defaultValue) {
        String s = map.get(setting);
        return s == null ? defaultValue : s;
    }

    @Override
    public Float getAsFloat(String setting, Float defaultValue) {
        String s = get(setting);
        try {
            return s == null ? defaultValue : Float.parseFloat(s);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse float setting [" + setting + "] with value [" + s + "]", e);
        }
    }

    @Override
    public Double getAsDouble(String setting, Double defaultValue) {
        String s = get(setting);
        try {
            return s == null ? defaultValue : Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse double setting [" + setting + "] with value [" + s + "]", e);
        }
    }

    @Override
    public Integer getAsInt(String setting, Integer defaultValue) {
        String s = get(setting);
        try {
            return s == null ? defaultValue : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse int setting [" + setting + "] with value [" + s + "]", e);
        }
    }

    @Override
    public Long getAsLong(String setting, Long defaultValue) {
        String s = get(setting);
        try {
            return s == null ? defaultValue : Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse long setting [" + setting + "] with value [" + s + "]", e);
        }
    }

    @Override
    public Boolean getAsBoolean(String setting, Boolean defaultValue) {
        String value = get(setting);
        if (value == null) {
            return defaultValue;
        }
        return !("false".equals(value) || "0".equals(value) || "off".equals(value) || "no".equals(value));
    }

    @Override
    public TimeValue getAsTime(String setting, TimeValue defaultValue) {
        return TimeValue.parseTimeValue(get(setting), defaultValue);
    }

    @Override
    public ByteSizeValue getAsBytesSize(String setting, ByteSizeValue defaultValue) {
        return ByteSizeValue.parseBytesSizeValue(get(setting), defaultValue);
    }

    @Override
    public String[] getAsArray(String settingPrefix) {
        return getAsArray(settingPrefix, EMPTY_ARRAY);
    }

    @Override
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

    @Override
    public Map<String, org.xbib.content.Settings> getGroups(String prefix) {
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
        TinyMap.Builder<String, org.xbib.content.Settings> retVal = TinyMap.builder();
        for (String key : hashMap.keySet()) {
            TinyMap.Builder<String, String> value = hashMap.get(key);
            retVal.put(key, new DatastructureSettings(value.build()));
        }
        return retVal.build();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && map.equals(((DatastructureSettings) o).map);
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
    public void close() throws IOException {
    }

}
