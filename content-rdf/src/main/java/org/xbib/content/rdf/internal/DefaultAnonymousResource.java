package org.xbib.content.rdf.internal;

import org.xbib.content.resource.IRI;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class DefaultAnonymousResource extends DefaultResource {

    private static final AtomicLong nodeID = new AtomicLong(0L);

    public DefaultAnonymousResource() {
        this(IRI.builder().curie(GENID, "b" + next()).build());
    }

    public DefaultAnonymousResource(String id) {
        this(id != null && id.startsWith(PLACEHOLDER) ?
                IRI.builder().curie(id).build() : IRI.builder().curie(GENID, id).build());
    }

    public DefaultAnonymousResource(IRI id) {
        super(id);
    }

    // for test
    public static void reset() {
        nodeID.set(0L);
    }

    // for test
    public static long next() {
        return nodeID.incrementAndGet();
    }

}
