package org.xbib.content.rdf;

import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;

/**
 * A triple is a group of a subject, a predicate, and an object.
 */
public interface Triple {

    Triple subject(Resource subject);

    Resource subject();

    Triple predicate(IRI predicate);

    IRI predicate();

    Triple object(Node object);

    Node object();

}
