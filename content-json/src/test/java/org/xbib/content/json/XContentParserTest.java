package org.xbib.content.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xbib.content.XContentParser;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XContentParserTest {

    private static final Logger logger = Logger.getLogger(XContentParserTest.class.getName());

    @Test
    public void simpleParse() throws IOException {
        XContentParser parser = JsonXContent.jsonContent().createParser("{\"a\":1,\"b\":2,\"c\":3}");
        Map<String, Object> map = parser.mapOrderedAndClose();
        logger.log(Level.INFO, map.getClass().getName().toString());
        assertEquals("{a=1, b=2, c=3}", map.toString());
        parser = JsonXContent.jsonContent().createParser("{\"a\":1,\"b\":2,\"c\":3}");
        map = parser.mapAndClose();
        logger.log(Level.INFO, map.getClass().getName().toString());
    }
}
