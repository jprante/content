package org.xbib.content.csv;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class CSVParserTest {

    private static final Logger logger = Logger.getLogger(CSVParserTest.class.getName());

    @Test
    public void testCommaSeparated() throws IOException {
        InputStream in = getClass().getResourceAsStream("test.csv");
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            CSVParser csvParser = new CSVParser(reader);
            Iterator<List<String>> it = csvParser.iterator();
            while (it.hasNext()) {
                List<String> row = it.next();
                //logger.log(Level.INFO, MessageFormat.format("count={0} row={1}", count, row));
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
                //logger.log(Level.INFO, MessageFormat.format("count={0} row={1}", count, row));
                count++;
            }
        }
        assertEquals(44447, count);
    }
}
