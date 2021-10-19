package org.xbib.config;

import org.xbib.settings.Settings;
import org.xbib.settings.SettingsBuilder;
import org.xbib.settings.SettingsLoader;
import org.xbib.settings.SettingsLoaderService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * A configuration loader for configuration files.
 */
public class ConfigLoader {

    private static final Map<ConfigParams, Settings> map = new HashMap<>();

    private ConfigLogger logger;

    private ConfigLoader() {
    }

    private static class Holder {
        private static ConfigLogger createConfigLogger() {
            ServiceLoader<ConfigLogger> serviceLoader = ServiceLoader.load(ConfigLogger.class);
            Optional<ConfigLogger> optionalConfigLogger = serviceLoader.findFirst();
            return optionalConfigLogger.orElse(new NullConfigLogger());
        }

        private static final ConfigLoader configLoader = new ConfigLoader().withLogger(createConfigLogger());
    }

    public static ConfigLoader getInstance() {
        return Holder.configLoader;
    }

    public ConfigLoader withLogger(ConfigLogger logger) {
        this.logger = logger;
        return this;
    }

    public synchronized Settings load(ConfigParams configParams) throws ConfigException {
        map.computeIfAbsent(configParams, p -> internalLoad(p)
                .replacePropertyPlaceholders()
                .build());
        return map.get(configParams);
    }

    private SettingsBuilder internalLoad(ConfigParams params) throws ConfigException {
        SettingsBuilder settings = Settings.settingsBuilder();
        if (params.withSystemEnvironment) {
            settings.loadFromSystemEnvironment();
        }
        if (params.withSystemProperties) {
            settings.loadFromSystemProperties();
        }
        if (!params.settings.isEmpty()) {
            for (Settings s : params.settings) {
                settings.put(s);
            }
        }
        if (!params.reader.isEmpty()) {
            for (ConfigParams.SuffixedReader reader : params.reader) {
                SettingsBuilder readerSettings = createSettingsFromReader(reader.reader, reader.suffix);
                if (readerSettings != null) {
                    settings.put(readerSettings.build());
                    if (!params.includeAll) {
                        return overrideFromProperties(params, settings);
                    }
                }
            }
        }
        if (params.args != null) {
            SettingsBuilder argsSettings = createSettingsFromArgs(params);
            if (argsSettings != null) {
                settings.put(argsSettings.build());
                if (!params.includeAll) {
                    return overrideFromProperties(params, settings);
                }
            }
        }
        if (params.withStdin) {
            SettingsBuilder stdinSettings = createSettingsFromStdin();
            if (stdinSettings != null) {
                settings.put(stdinSettings.build());
                if (!params.includeAll) {
                    return overrideFromProperties(params, settings);
                }
            }
        }
        if (!params.fileLocations.isEmpty()) {
            SettingsBuilder fileSettings = createSettingsFromFile(params.fileLocations);
            if (fileSettings != null) {
                settings.put(fileSettings.build());
                if (!params.includeAll) {
                    return overrideFromProperties(params, settings);
                }
            }
        }
        if (!params.fileNamesWithoutSuffix.isEmpty()) {
            for (String fileNameWithoutSuffix : params.fileNamesWithoutSuffix) {
                SettingsBuilder fileSettings = createSettingsFromFile(createListOfLocations(params, fileNameWithoutSuffix));
                if (fileSettings != null) {
                    settings.put(fileSettings.build());
                    if (!params.includeAll) {
                        return overrideFromProperties(params, settings);
                    }
                }
            }
            for (String fileNameWithoutSuffix : params.fileNamesWithoutSuffix) {
                if (params.classLoaders != null) {
                    for (ClassLoader cl : params.classLoaders) {
                        if (cl != null) {
                            SettingsBuilder classpathSettings = createClasspathSettings(params, cl, fileNameWithoutSuffix);
                            if (classpathSettings != null) {
                                settings.put(classpathSettings.build());
                                if (!params.includeAll) {
                                    return overrideFromProperties(params, settings);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!params.jdbcLookups.isEmpty()) {
            for (ConfigParams.JdbcLookup jdbcLookup : params.jdbcLookups) {
                try {
                    settings.fromJdbc(jdbcLookup.connection, jdbcLookup.statement, jdbcLookup.params);
                } catch (SQLException sqlException) {
                    throw new ConfigException(sqlException);
                }
            }
        }
        if (params.includeAll) {
            return overrideFromProperties(params, settings);
        }
        if (params.failIfEmpty) {
            throw new ConfigException("no config found");
        }
        return settings;
    }

    private SettingsBuilder createSettingsFromArgs(ConfigParams params) throws ConfigException {
        if (!params.fileNamesWithoutSuffix.isEmpty() && params.args != null) {
            for (String fileNameWithoutSuffix : params.fileNamesWithoutSuffix) {
                for (String suffix : SettingsLoaderService.getInstance().getSuffixes()) {
                    for (int i = 0; i < params.args.size() - 1; i++) {
                        String arg = params.args.get(i);
                        String s = params.directoryName != null ?
                                "--" + params.directoryName + "-" + fileNameWithoutSuffix + "." + suffix :
                                "--" + fileNameWithoutSuffix + "." + suffix;
                        if (arg.equals(s)) {
                            return createSettingsFromReader(new StringReader(params.args.get(i + 1)), suffix);
                        }
                    }
                }
            }
        }
        return null;
    }

    private SettingsBuilder createSettingsFromStdin() throws ConfigException {
        if (System.in != null) {
            try {
                int numBytesWaiting = System.in.available();
                if (numBytesWaiting > 0) {
                    String suffix = System.getProperty("config.format", "yaml");
                    return createSettingsFromStream(System.in, "." + suffix);
                }
            } catch (IOException e) {
                throw new ConfigException(e);
            }
        }
        return null;
    }

    private SettingsBuilder createSettingsFromFile(List<String> settingsFileNames) throws ConfigException {
        SettingsBuilder settings = Settings.settingsBuilder();
        for (String settingsFileName: settingsFileNames) {
            int pos = settingsFileName.lastIndexOf('.');
            String suffix = (pos > 0 ? settingsFileName.substring(pos + 1) : "").toLowerCase(Locale.ROOT);
            Path path = Paths.get(settingsFileName);
            if (logger != null) {
                logger.info("trying " + path);
            }
            if (Files.exists(path)) {
                if (logger != null) {
                    logger.info("found path: " + path);
                }
                System.setProperty("config.path", path.getParent().toString());
                try {
                    InputStream inputStream = Files.newInputStream(path);
                    SettingsBuilder fileSettings = createSettingsFromStream(inputStream, suffix);
                    if (fileSettings != null) {
                        settings.put(fileSettings.build());
                    }
                } catch (Exception e) {
                    throw new ConfigException(e);
                }
            }
        }
        return settings.isEmpty() ? null : settings;
    }

    private SettingsBuilder createClasspathSettings(ConfigParams params,
                                                    ClassLoader classLoader,
                                                    String fileNameWithoutSuffix) throws ConfigException {
        SettingsBuilder settings = Settings.settingsBuilder();
        for (String suffix : SettingsLoaderService.getInstance().getSuffixes()) {
            String path = params.directoryName != null ?
                    params.directoryName + '-' + fileNameWithoutSuffix + suffix :  fileNameWithoutSuffix + suffix;
            InputStream inputStream = classLoader.getResourceAsStream(path);
            if (inputStream != null) {
                if (logger != null) {
                    logger.info("found resource: " + path);
                }
                SettingsBuilder streamSettings = createSettingsFromStream(inputStream, suffix);
                if (streamSettings != null) {
                    settings.put(streamSettings.build());
                }
            }
        }
        return settings.isEmpty() ? null : settings;
    }

    private SettingsBuilder createSettingsFromStream(InputStream inputStream,
                                                     String suffix) throws ConfigException {
        if (inputStream == null) {
            if (logger != null) {
                logger.error("unable to open input stream");
            }
            return null;
        }
        return createSettingsFromReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), suffix);
    }

    private SettingsBuilder createSettingsFromReader(Reader reader,
                                                     String suffix) throws ConfigException {
        if (reader == null) {
            if (logger != null) {
                logger.error("unable to open reader");
            }
            return null;
        }
        SettingsLoader settingsLoader = SettingsLoaderService.getInstance().loaderFromResource(suffix);
        if (settingsLoader != null) {
            SettingsBuilder settings;
            try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                String content = bufferedReader.lines().collect(Collectors.joining("\n"));
                settings = Settings.settingsBuilder().put(settingsLoader.load(content));
            } catch (IOException e) {
                throw new ConfigException(e);
            }
            return settings;
        } else {
            if (logger != null) {
                logger.error("suffix is invalid: " + suffix);
            }
        }
        return null;
    }

    private SettingsBuilder overrideFromProperties(ConfigParams params, SettingsBuilder settingsBuilder) {
        if (params.withSystemPropertiesOverride) {
            settingsBuilder.map(e -> {
                String key = e.getKey();
                String value = System.getProperty(params.directoryName != null ? params.directoryName + '.' + key : key);
                return value != null ? Map.entry(key, value) : Map.entry(key, e.getValue());
            });
        }
        return settingsBuilder;
    }

    private List<String> createListOfLocations(ConfigParams params,
                                               String fileNameWithoutSuffix) {
        List<String> list = new ArrayList<>();
        for (String suffix : SettingsLoaderService.getInstance().getSuffixes()) {
            String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
            if (xdgConfigHome == null) {
                xdgConfigHome = System.getProperty("user.home") + "/.config";
            }
            if (params.directoryName != null) {
                list.add(params.directoryName + '-' + fileNameWithoutSuffix + "." + suffix);
                list.add(xdgConfigHome + '/' + params.directoryName + '/' + fileNameWithoutSuffix + "." + suffix);
                list.add("/etc/" + params.directoryName + '/' + fileNameWithoutSuffix + "." + suffix);
            } else {
                list.add(fileNameWithoutSuffix + "." + suffix);
                list.add(xdgConfigHome + '/' + fileNameWithoutSuffix + "." + suffix);
                list.add("/etc/" + fileNameWithoutSuffix + "." + suffix);
            }
        }
        return list;
    }
}
