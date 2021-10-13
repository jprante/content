package org.xbib.content;

import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface SettingsBuilder {

    SettingsBuilder put(String setting, String value);

    SettingsBuilder put(String setting, Class<?> clazz);

    SettingsBuilder put(String setting, boolean value);

    SettingsBuilder put(String setting, int value);

    SettingsBuilder put(String setting, long value);

    SettingsBuilder put(String setting, float value);

    SettingsBuilder put(String setting, double value);

    SettingsBuilder putArray(String setting, String... values);

    SettingsBuilder putArray(String setting, List<String> values);

    SettingsBuilder put(String settingPrefix, String groupName, String[] settings, String[] values)
            throws SettingsException;

    SettingsBuilder put(Settings settings);

    SettingsBuilder put(Map<String, String> settings);

    SettingsBuilder loadFromString(String source);

    SettingsBuilder loadFromResource(String resourceName, InputStream inputStream) throws SettingsException;

    default SettingsBuilder fromJdbcConfTable(Connection connection, String id, String type) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select key, value from conf where id = ? and type = ?",
                new String[]{id, type}); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String key = resultSet.getString("key");
                String value = resultSet.getString("value");
                put(key, value);
            }
        }
        return this;
    }

    SettingsBuilder loadFromSystemProperties();

    SettingsBuilder loadFromSystemEnvironment();

    SettingsBuilder replacePropertyPlaceholders(PropertyPlaceholder propertyPlaceholder,
                                                PlaceholderResolver placeholderResolver);

    SettingsBuilder replacePropertyPlaceholders();

    SettingsBuilder setRefresh(Path path, long initialDelay, long period, TimeUnit timeUnit);

    Settings build();

    boolean isEmpty();

    Map<String, String> map();

}
