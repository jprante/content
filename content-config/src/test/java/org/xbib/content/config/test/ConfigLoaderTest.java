package org.xbib.content.config.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xbib.content.Settings;
import org.xbib.content.config.ConfigLoader;
import org.xbib.content.config.ConfigParams;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

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

}
