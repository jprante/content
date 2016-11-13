package org.xbib.content.rdf.io.ntriple;

import static org.xbib.content.rdf.RdfContentFactory.ntripleBuilder;

import org.junit.Test;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.helper.StreamTester;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class NTripleReaderTest extends StreamTester {

    @Test
    public void testReader() throws Exception {
        String filename = "list.nt";
        InputStream in = getClass().getResourceAsStream(filename);
        if (in == null) {
            throw new IOException("file " + filename + " not found");
        }
        RdfContentBuilder<NTripleContentParams> builder = ntripleBuilder();
        NTripleContentParser<NTripleContentParams> reader = new NTripleContentParser<>(in);
        reader.setBuilder(builder);
        reader.parse();
        //assertStream(getClass().getResource("rdfxml.ttl").openStream(),
        //        builder.streamInput());
    }

}
