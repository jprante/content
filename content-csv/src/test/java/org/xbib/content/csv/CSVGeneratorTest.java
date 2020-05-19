package org.xbib.content.csv;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

/**
 *
 */
public class CSVGeneratorTest {

    @Test
    public void test() throws IOException {
        StringWriter writer = new StringWriter();
        CSVGenerator gen = new CSVGenerator(writer);
        gen.keys(Arrays.asList("a", "b", "c"));
        for (int i = 0; i < 10; i++) {
            gen.write("val" + i);
            gen.write("\"Hello, World\"");
            gen.write("hey look a line seperator \n");
        }
        gen.close();
    }
}
