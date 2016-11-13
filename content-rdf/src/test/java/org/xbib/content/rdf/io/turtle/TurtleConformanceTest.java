package org.xbib.content.rdf.io.turtle;

import org.junit.Test;
import org.xbib.content.rdf.RdfContentFactory;
import org.xbib.content.resource.IRI;
import org.xbib.helper.StreamTester;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class TurtleConformanceTest extends StreamTester {

    private static final Logger logger = Logger.getLogger(TurtleConformanceTest.class.getName());

    @Test
    public void conformance() throws Exception {
        for (int n = 0; n < 30; n++) {
            String testNum = String.format("%02d", n);
            InputStream in = getClass().getResource("/turtle/test-" + testNum + ".ttl").openStream();
            TurtleContentParser<TurtleContentParams> turtleParser = new TurtleContentParser<TurtleContentParams>(in)
                    .setBaseIRI(IRI.create("http://example/base/"));
            turtleParser.setRdfContentBuilderProvider(RdfContentFactory::turtleBuilder);
            turtleParser.setRdfContentBuilderHandler(b -> {
                logger.log(Level.INFO, MessageFormat.format("turtle test {0}", b.string()));
            });
            turtleParser.parse();
        }
    }

}
