package org.xbib.settings;

import org.xbib.datastructures.api.ByteSizeValue;
import org.xbib.datastructures.api.TimeValue;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public interface Settings extends AutoCloseable {

    class Holder {

        private Holder() {
        }

        private static SettingsBuilder createBuilder() {
            ServiceLoader<SettingsBuilder> serviceLoader = ServiceLoader.load(SettingsBuilder.class);
            Optional<SettingsBuilder> optionalSettingsBuilder = serviceLoader.findFirst();
            return optionalSettingsBuilder.orElse(null);
        }

        private static final Settings emptySettings = createBuilder().build();
    }

    static SettingsBuilder settingsBuilder() {
        return Holder.createBuilder();
    }

    static Settings emptySettings() {
        return Holder.emptySettings;
    }

    boolean isEmpty();

    String get(String setting);

    String get(String setting, String defaultValue);

    float getAsFloat(String setting, float defaultValue);

    double getAsDouble(String setting, double defaultValue);

    int getAsInt(String setting, int defaultValue);

    long getAsLong(String setting, long defaultValue);

    boolean getAsBoolean(String setting, boolean defaultValue);

    TimeValue getAsTime(String setting, TimeValue defaultValue);

    ByteSizeValue getAsBytesSize(String setting, ByteSizeValue defaultValue);

    String[] getAsArray(String settingPrefix);

    String[] getAsArray(String settingPrefix, String[] defaultArray);

    Map<String, String> getAsMap();

    Map<String, Object> getAsStructuredMap();

    Map<String, Settings> getGroups(String prefix);

    Settings getAsSettings(String setting);

    Settings getByPrefix(String prefix);

    boolean containsSetting(String setting);

    void close() throws IOException;
}
