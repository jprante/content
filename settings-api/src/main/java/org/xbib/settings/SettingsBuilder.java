package org.xbib.settings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public interface SettingsBuilder {

    SettingsBuilder put(String setting, String value);

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

    SettingsBuilder loadFromString(String resourceName, String content);

    SettingsBuilder loadFromResource(String resourceName, InputStream inputStream);

    default SettingsBuilder fromJdbc(Connection connection, String statement, String[] params) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement, params);
             ResultSet resultSet = preparedStatement.executeQuery()) {
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

    /**
     * Runs across all the settings set on this builder and replaces {@code ${...}} elements in the
     * each setting value according to the following logic:
     * First, tries to resolve it against a System property ({@link System#getProperty(String)}), next,
     * tries and resolve it against an environment variable ({@link System#getenv(String)}), next,
     * tries and resolve it against a date pattern to resolve the current date,
     * and last, tries and replace it with another setting already set on this builder.
     * @param propertyPlaceholder the property place holder
     * @param placeholderResolver  the place holder resolver
     * @return this builder
     */
    SettingsBuilder replacePropertyPlaceholders(PropertyPlaceholder propertyPlaceholder,
                                                PlaceholderResolver placeholderResolver);

    /**
     * A default method to replace property placeholders.
     * @return this builder
     */
    SettingsBuilder replacePropertyPlaceholders();

    /**
     * Optional settings refresh mechanism, using reloading from a path after a give time period.
     * May not be implemented at all.
     */
    SettingsBuilder setRefresh(Path path, long initialDelay, long period, TimeUnit timeUnit);

    /**
     * Map all settings keys and values to other keys and values.
     * Example usage is to override settings from another priority source.
     * @return this builder
     */
    SettingsBuilder map(Function<Map.Entry<String, String>, Map.Entry<String, String>> function);

    /**
     * Return the Settings from this SettingsBuilder.
     * @return the settings
     */
    Settings build();

    /**
     * Returns true if the settings builder is empty.
     * @return true if empty
     */
    boolean isEmpty();

}
