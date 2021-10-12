package org.xbib.content.config.test;

import org.junit.jupiter.api.Test;
import org.xbib.content.config.ConfigLoader;
import org.xbib.content.config.ConfigParams;
import org.xbib.content.settings.datastructures.Settings;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigLoaderTest {

    @Test
    public void configTest() {
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
    public void configInterlibraryTest() {
        Settings settings = ConfigLoader.getInstance()
                .load(new ConfigParams()
                        .withDirectoryName("interlibrary")
                        .withFileNamesWithoutSuffix("test"));
        Logger.getAnonymousLogger().log(Level.INFO, settings.getAsMap().toString());
    }
}
