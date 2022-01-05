package org.xbib.config.test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.jupiter.api.Test;
import org.xbib.config.ConfigLoader;
import org.xbib.config.ConfigParams;
import org.xbib.settings.Settings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigLoaderTest {

    @Test
    public void configEmptyTest() {
        Settings settings = ConfigLoader.getInstance()
                .load(new ConfigParams());
        assertTrue(settings.isEmpty());
    }

    @Test
    public void configSettingsTest() {
        Settings configSettings = Settings.settingsBuilder()
                .put("hello", "world")
                .build();
        Settings settings = ConfigLoader.getInstance()
                .load(new ConfigParams().withSettings(configSettings));
        assertEquals("world", settings.get("hello"));
    }

    @Test
    public void configArgsTest() {
        String[] args = new String[] {
                "--config.yaml",
                "hello: world"
        };
        Settings settings = ConfigLoader.getInstance()
                .load(new ConfigParams()
                        .withArgs(args)
                        .withFileNamesWithoutSuffix("config"));
        assertEquals("world", settings.get("hello"));
    }

    @Test
    public void configPropertiesTest() {
        Reader reader = new StringReader("a=b");
        Settings settings = ConfigLoader.getInstance()
                .load(new ConfigParams()
                        .withReader(reader, "properties"));
        assertEquals("b", settings.get("a"));
    }

    @Test
    public void configFileTest() throws IOException {
        Settings settings = ConfigLoader.getInstance()
                .load(new ConfigParams()
                        .withPath(null, null, "src/test/resources", "config.*"));
        assertEquals("world", settings.get("hello"));
        assertEquals("world2", settings.get("hello2"));
    }

    @Test
    public void testSystemPropertiesOverride() throws IOException {
        System.setProperty("hello", "override");
        Settings settings = ConfigLoader.getInstance()
                .load(new ConfigParams()
                        .withPath(null, null, "src/test/resources", "config.*"));
        assertEquals("world", settings.get("hello"));
        settings = ConfigLoader.getInstance()
                .load(new ConfigParams()
                        .withSystemPropertiesOverride()
                        .withPath(null, null, "src/test/resources", "config.*"));
        assertEquals("override", settings.get("hello"));
    }
}
