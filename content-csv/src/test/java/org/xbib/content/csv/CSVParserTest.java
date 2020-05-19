package org.xbib.content.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class CSVParserTest {

    @Test
    public void testCommaSeparated() throws IOException {
        InputStream in = getClass().getResourceAsStream("test.csv");
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            CSVParser csvParser = new CSVParser(reader);
            Iterator<List<String>> it = csvParser.iterator();
            while (it.hasNext()) {
                List<String> row = it.next();
                count++;
            }
        }
        assertEquals(2, count);
    }

    @Test
    public void testLargeFile() throws IOException {
        InputStream in = getClass().getResourceAsStream("titleFile.csv");
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            CSVParser csvParser = new CSVParser(reader);
            Iterator<List<String>> it = csvParser.iterator();
            while (it.hasNext()) {
                List<String> row = it.next();
                count++;
            }
        }
        assertEquals(44447, count);
    }
}
