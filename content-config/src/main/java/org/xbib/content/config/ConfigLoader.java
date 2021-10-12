package org.xbib.content.config;

import org.xbib.content.settings.datastructures.SettingsLoader;
import org.xbib.content.settings.datastructures.Settings;
import org.xbib.content.settings.datastructures.SettingsLoaderService;

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

    private final Map<ConfigParams, Settings> map;

    private ConfigLogger logger;

    private ConfigLoader() {
        this.map = new HashMap<>();
    }

    private static class Holder {
        private static ConfigLogger createConfigLogger() {
            ServiceLoader<ConfigLogger> serviceLoader = ServiceLoader.load(ConfigLogger.class);
            Optional<ConfigLogger> optionalConfigLogger = serviceLoader.findFirst();
            return optionalConfigLogger.orElse(new SystemConfigLogger());
        }
        static ConfigLoader LOADER = new ConfigLoader().withLogger(createConfigLogger());
    }

    public static ConfigLoader getInstance() {
        return Holder.LOADER;
    }

    public ConfigLoader withLogger(ConfigLogger logger) {
        this.logger = logger;
        return this;
    }

    public synchronized Settings load(ConfigParams configParams) throws ConfigException {
        map.computeIfAbsent(configParams, p -> internalLoad(p).build());
        return map.get(configParams);
    }

    private Settings.Builder internalLoad(ConfigParams params) throws ConfigException {
        Settings.Builder settings = Settings.settingsBuilder();
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
                Settings.Builder readerSettings = createSettingsFromReader(reader.reader, reader.suffix);
                if (readerSettings != null) {
                    settings.put(readerSettings.build());
                    if (!params.includeAll) {
                        return settings;
                    }
                }
            }
        }
        if (params.args != null) {
            Settings.Builder argsSettings = createSettingsFromArgs(params);
            if (argsSettings != null) {
                settings.put(argsSettings.build());
                if (!params.includeAll) {
                    return settings;
                }
            }
        }
        if (params.withStdin) {
            Settings.Builder stdinSettings = createSettingsFromStdin();
            if (stdinSettings != null) {
                settings.put(stdinSettings.build());
                if (!params.includeAll) {
                    return overrideFromProperties(params, settings);
                }
            }
        }
        if (!params.fileLocations.isEmpty()) {
            Settings.Builder fileSettings = createSettingsFromFile(params.fileLocations);
            if (fileSettings != null) {
                settings.put(fileSettings.build());
                if (!params.includeAll) {
                    return overrideFromProperties(params, settings);
                }
            }
        }
        if (!params.fileNamesWithoutSuffix.isEmpty()) {
            for (String fileNameWithoutSuffix : params.fileNamesWithoutSuffix) {
                Settings.Builder fileSettings = createSettingsFromFile(createListOfLocations(params, fileNameWithoutSuffix));
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
                            Settings.Builder classpathSettings = createClasspathSettings(params, cl, fileNameWithoutSuffix);
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
        if (params.includeAll) {
            return overrideFromProperties(params, settings);
        }
        throw new ConfigException("no config found");
    }

    private Settings.Builder createSettingsFromArgs(ConfigParams params) throws ConfigException {
        if (!params.fileNamesWithoutSuffix.isEmpty() && params.args != null) {
            for (String fileNameWithoutSuffix : params.fileNamesWithoutSuffix) {
                for (String suffix : SettingsLoaderService.getInstance().getSuffixes()) {
                    for (int i = 0; i < params.args.size() - 1; i++) {
                        String arg = params.args.get(i);
                        String s = params.directoryName != null ?
                          "--" + params.directoryName + "-" + fileNameWithoutSuffix + suffix : "--" + fileNameWithoutSuffix + suffix;
                        if (arg.equals(s)) {
                            return createSettingsFromReader(new StringReader(params.args.get(i + 1)), suffix);
                        }
                    }
                }
            }
        }
        return null;
    }

    private Settings.Builder createSettingsFromStdin() throws ConfigException {
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

    private Settings.Builder createSettingsFromFile(List<String> settingsFileNames) throws ConfigException {
        Settings.Builder settings = Settings.settingsBuilder();
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
                    Settings.Builder fileSettings = createSettingsFromStream(inputStream, suffix);
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

    private Settings.Builder createClasspathSettings(ConfigParams params,
                                                     ClassLoader classLoader,
                                                     String fileNameWithoutSuffix) throws ConfigException {
        Settings.Builder settings = Settings.settingsBuilder();
        for (String suffix : SettingsLoaderService.getInstance().getSuffixes()) {
            String path = params.directoryName != null ?
                    params.directoryName + '-' + fileNameWithoutSuffix + suffix :  fileNameWithoutSuffix + suffix;
            InputStream inputStream = classLoader.getResourceAsStream(path);
            if (inputStream != null) {
                if (logger != null) {
                    logger.info("found resource: " + path);
                }
                Settings.Builder streamSettings = createSettingsFromStream(inputStream, suffix);
                if (streamSettings != null) {
                    settings.put(streamSettings.build());
                }
            }
        }
        return settings.isEmpty() ? null : settings;
    }

    private Settings.Builder createSettingsFromStream(InputStream inputStream,
                                                      String suffix) throws ConfigException {
        if (inputStream == null) {
            if (logger != null) {
                logger.error("unable to open input stream");
            }
            return null;
        }
        return createSettingsFromReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), suffix);
    }

    private Settings.Builder createSettingsFromReader(Reader reader,
                                                      String suffix) throws ConfigException {
        if (reader == null) {
            if (logger != null) {
                logger.error("unable to open reader");
            }
            return null;
        }
        SettingsLoader settingsLoader = SettingsLoaderService.getInstance().loaderFromResource(suffix);
        if (settingsLoader != null) {
            Settings.Builder settings;
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

    private Settings.Builder overrideFromProperties(ConfigParams params,
                                                    Settings.Builder settings) {
        for (String key : settings.map().keySet()) {
            String value = System.getProperty(params.directoryName != null ? params.directoryName + '.' + key : key);
            if (value != null) {
                settings.put(key, value);
            }
        }
        return settings;
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
                list.add(xdgConfigHome + '/' + params.directoryName + '/' + fileNameWithoutSuffix + "." +suffix);
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
