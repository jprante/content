package org.xbib.content.config;

import org.xbib.content.json.JsonSettingsLoader;
import org.xbib.content.settings.Settings;
import org.xbib.content.SettingsLoader;
import org.xbib.content.yaml.YamlSettingsLoader;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A configuration file loader for JSON/YAML configuration files.
 */
public class ConfigLoader {

    private static final String JSON = ".json";

    private static final String YML = ".yml";

    private static final String YAML = ".yaml";

    private final ConfigLogger logger;

    public ConfigLoader(ConfigLogger logger) {
        this.logger = logger;
    }

    public Settings.Builder loadSettings(String[] args,
                                         ClassLoader classLoader,
                                         String applicationName,
                                         String... fileNamesWithoutSuffix) throws IOException {
        Settings.Builder settings = createSettingsFromArgs(args, applicationName, fileNamesWithoutSuffix);
        return settings != null ? settings : loadSettings(classLoader, applicationName, fileNamesWithoutSuffix);
    }

    public Settings.Builder loadSettings(ClassLoader classLoader,
                                         String applicationName,
                                         String... fileNamesWithoutSuffix) throws IOException {
        Settings.Builder settings = createSettingsFromStdin();
        if (settings != null) {
            return overrideFromProperties(applicationName, settings);
        }
        for (String fileNameWithoutSuffix : fileNamesWithoutSuffix) {
            settings = createSettingsFromFile(createListOfLocations(applicationName, fileNameWithoutSuffix));
            if (settings != null) {
                return overrideFromProperties(applicationName, settings);
            }
            for (ClassLoader cl : List.of(classLoader,
                    Thread.currentThread().getContextClassLoader(),
                    ConfigLoader.class.getClassLoader(),
                    ClassLoader.getSystemClassLoader())) {
                if (cl != null) {
                    settings = createClasspathSettings(cl, applicationName, fileNameWithoutSuffix);
                    if (settings != null) {
                        return overrideFromProperties(applicationName, settings);
                    }
                }
            }
        }
        throw new IllegalArgumentException("no config found for " + applicationName + " " +
                Arrays.asList(fileNamesWithoutSuffix));
    }

    private Settings.Builder createSettingsFromArgs(String[] args,
                                                    String applicationName,
                                                    String... fileNamesWithoutSuffix) throws IOException {
        for (String fileNameWithoutSuffix : fileNamesWithoutSuffix) {
            for (String suffix : List.of(YML, YAML, JSON)) {
                for (int i = 0; i < args.length - 1; i++) {
                    String arg = args[i];
                    if (arg.equals("--" + applicationName + "-" + fileNameWithoutSuffix + suffix)) {
                        return createSettingsFromReader(new StringReader(args[i + 1]), suffix);
                    }
                }
            }
        }
        return null;
    }

    private Settings.Builder createSettingsFromStdin() throws IOException {
        if (System.in != null) {
            int numBytesWaiting = System.in.available();
            if (numBytesWaiting > 0) {
                String suffix = System.getProperty("config.format", "yaml");
                return createSettingsFromStream(System.in, "." + suffix);
            }
        }
        return null;
    }

    private Settings.Builder createSettingsFromFile(List<String> settingsFileNames) throws IOException {
        for (String settingsFileName: settingsFileNames) {
            int pos = settingsFileName.lastIndexOf('.');
            String suffix = (pos > 0 ? settingsFileName.substring(pos) : "").toLowerCase(Locale.ROOT);
            Path path = Paths.get(settingsFileName);
            logger.info("trying " + path.toString());
            if (Files.exists(path)) {
                logger.info("found path: " + path);
                System.setProperty("config.path", path.getParent().toString());
                return createSettingsFromStream(Files.newInputStream(path), suffix);
            }
        }
        return null;
    }

    private Settings.Builder createClasspathSettings(ClassLoader classLoader,
                                                     String applicationName,
                                                     String fileNameWithoutSuffix)
            throws IOException {
        for (String suffix : List.of(YML, YAML, JSON)) {
            InputStream inputStream = classLoader.getResourceAsStream(applicationName + '-' +
                    fileNameWithoutSuffix + suffix);
            if (inputStream != null) {
                logger.info("found resource: " + applicationName + '-' + fileNameWithoutSuffix + suffix);
                Settings.Builder settings = createSettingsFromStream(inputStream, suffix);
                if (settings != null) {
                    return settings;
                }
            }
        }
        return null;
    }

    private Settings.Builder createSettingsFromStream(InputStream inputStream, String suffix) throws IOException {
        if (inputStream == null) {
            logger.error("unable to open input stream");
            return null;
        }
        return createSettingsFromReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), suffix);
    }

    private Settings.Builder createSettingsFromReader(Reader reader, String suffix) throws IOException {
        if (reader == null) {
            logger.error("unable to open reader");
            return null;
        }
        SettingsLoader settingsLoader = isYaml(suffix) ? new YamlSettingsLoader() :
                isJson(suffix) ? new JsonSettingsLoader() : null;
        if (settingsLoader != null) {
            Settings.Builder settings;
            try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                String content = bufferedReader.lines().collect(Collectors.joining("\n"));
                settings = Settings.settingsBuilder().put(settingsLoader.load(content));
            }
            return settings;
        } else {
            logger.error("suffix is invalid: " + suffix);
        }
        return null;
    }

    private static Settings.Builder overrideFromProperties(String applicationName, Settings.Builder settings) {
        for (Map.Entry<String, String> entry : settings.map().entrySet()) {
            String key = entry.getKey();
            String value = System.getProperty(applicationName + '.' + key);
            if (value != null) {
                settings.put(key, value);
            }
        }
        return settings;
    }

    private static List<String> createListOfLocations(String applicationName, String fileNameWithoutSuffix) {
        String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
        if (xdgConfigHome == null) {
            xdgConfigHome = System.getProperty("user.home") + "/.config";
        }
        return List.of(
                applicationName + '-' + fileNameWithoutSuffix + YML,
                applicationName + '-' + fileNameWithoutSuffix + YAML,
                applicationName + '-' + fileNameWithoutSuffix + JSON,
                xdgConfigHome + '/' + applicationName + '/' + fileNameWithoutSuffix + YML,
                xdgConfigHome + '/' + applicationName + '/' + fileNameWithoutSuffix + YAML,
                xdgConfigHome + '/' + applicationName + '/' + fileNameWithoutSuffix + JSON,
                "/etc/" + applicationName + '/' + fileNameWithoutSuffix + YML,
                "/etc/" + applicationName + '/' + fileNameWithoutSuffix + YAML,
                "/etc/" + applicationName + '/' + fileNameWithoutSuffix + JSON);
    }

    private static boolean isYaml(String suffix) {
        return YAML.equals(suffix) || YML.equals(suffix);
    }

    private static boolean isJson(String suffix) {
        return JSON.equals(suffix);
    }
}
