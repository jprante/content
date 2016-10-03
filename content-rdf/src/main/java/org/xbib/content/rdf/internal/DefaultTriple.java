package org.xbib.content.rdf.internal;

import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.Triple;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;

/**
 * A memory triple.
 */
public class DefaultTriple implements Triple, Comparable<Triple> {

    private Resource subject;

    private IRI predicate;

    private Node object;

    /**
     * Create a new triple.
     *
     * @param subject   subject
     * @param predicate predicate
     * @param object    object
     */
    public DefaultTriple(Resource subject, IRI predicate, Node object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    @Override
    public Triple subject(Resource subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public Resource subject() {
        return subject;
    }

    @Override
    public Triple predicate(IRI predicate) {
        this.predicate = predicate;
        return null;
    }

    @Override
    public IRI predicate() {
        return predicate;
    }

    @Override
    public Triple object(Node object) {
        this.object = object;
        return this;
    }

    @Override
    public Node object() {
        return object;
    }

    @Override
    public String toString() {
        return (subject != null ? subject : " <null>")
                + (predicate != null ? " " + predicate : " <null>")
                + (object != null ? " " + object : " <null>");
    }

    @Override
    public int hashCode() {
        return (subject != null ? subject.hashCode() : 0)
                + (predicate != null ? predicate.hashCode() : 0)
                + (object != null ? object.hashCode() : 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass() == obj.getClass() && compareTo((Triple) obj) == 0;
    }

    @Override
    public int compareTo(Triple triple) {
        return toString().compareTo(triple.toString());
    }
}
