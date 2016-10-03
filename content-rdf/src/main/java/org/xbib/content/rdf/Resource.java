package org.xbib.content.rdf;

import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Resource is an ID with a map of predicates associated with objects.
 */
public interface Resource extends Node {

    Resource setId(IRI id);

    IRI id();

    /**
     * Checks whether this resource ID is local/embedded ("blank node").
     *
     * @return true if embedded, otherwise false
     */
    @Override
    boolean isEmbedded();

    /**
     * Add a property to this resource with a string object value.
     *
     * @param predicate a predicate identifier
     * @param object    an object in its string representation form
     * @return the new resource with the property added
     */
    Resource add(IRI predicate, Node object);

    /**
     * Add a property to this resource with a string object value.
     *
     * @param predicate a predicate identifier
     * @param object    an object in its string representation form
     * @return the new resource with the property added
     */
    Resource add(IRI predicate, String object);

    /**
     * Add a property to this resource.
     *
     * @param predicate a predicate identifier
     * @param number    an integer
     * @return the new resource with the property added
     */
    Resource add(IRI predicate, Integer number);

    Resource add(IRI predicate, Boolean number);

    /**
     * Add a property to this resource.
     *
     * @param predicate a predicate identifier
     * @param literal   a literal
     * @return the new resource with the property added
     */
    Resource add(IRI predicate, Literal literal);

    /**
     * Add a property to this resource.
     *
     * @param predicate a predicate identifier
     * @param resource  external resource IRI
     * @return the new resource with the property added
     */
    Resource add(IRI predicate, IRI resource);

    /**
     * Add a property to this resource.
     *
     * @param predicate a predicate identifier
     * @param list      a list of objects
     * @return the new resource with the property added
     */
    Resource add(IRI predicate, List<Object> list);

    Resource add(IRI predicate, Map<Object, Object> map);

    /**
     * Add another resource to this resource.
     *
     * @param predicate a predicate identifier
     * @param resource  resource
     * @return the new resource with the resource added
     */
    Resource add(IRI predicate, Resource resource);

    /**
     * Add a property to this resource.
     *
     * @param predicate a predicate identifier in its string representation form
     * @param object    an object in its string representation form
     * @return the new resource with the property added
     */
    Resource add(String predicate, String object);

    /**
     * Add a property to this resource.
     *
     * @param predicate a predicate identifier
     * @param number    an integer
     * @return the new resource with the property added
     */
    Resource add(String predicate, Integer number);

    Resource add(String predicate, Boolean number);

    /**
     * Add a property to this resource.
     *
     * @param predicate a predicate identifier in its string representation form
     * @param literal   an object in its string representation form
     * @return the new resource with the property added
     */
    Resource add(String predicate, Literal literal);

    /**
     * Add a property to this resource.
     *
     * @param predicate        predicate
     * @param externalResource external resource
     * @return the new resource with the property added
     */
    Resource add(String predicate, IRI externalResource);

    /**
     * Add a property to this resource.
     *
     * @param predicate predicate
     * @param list      a list of objects
     * @return the new resource with the property added
     */
    Resource add(String predicate, List<Object> list);

    Resource add(String predicate, Map<Object, Object> map);

    /**
     * Add another resource to this resource.
     *
     * @param predicate predicate
     * @param resource  resource
     * @return the new resource with the resource added
     */
    Resource add(String predicate, Resource resource);

    Resource add(Map<Object, Object> map);

    Resource rename(String oldPredicate, String newPredicate);

    Resource rename(IRI oldPredicate, IRI newPredicate);

    /**
     * Setting the type of the resource.
     * This is equivalent to add("rdf:type", externalResource)
     *
     * @param externalResource external resource
     * @return this resource
     */
    Resource a(IRI externalResource);

    /**
     * Return list of resources for this predicate.
     *
     * @param predicate the predicate
     * @return list of resources
     */
    List<Resource> resources(IRI predicate);

    /**
     * Create an anonymous resource and associate it with this resource. If the
     * resource under the given resource identifier already exists, the existing
     * resource is returned.
     *
     * @param predicate the predicate ID for the resource
     * @return the new anonymous resource
     */
    Resource newResource(IRI predicate);

    /**
     * Create an anonymous resource and associate it with this resource. If the
     * resource under the given resource identifier already exists, the existing
     * resource is returned.
     *
     * @param predicate the predicate ID for the resource
     * @return the new anonymous resource
     */
    Resource newResource(String predicate);

    Resource newSubject(Object subject);

    IRI newPredicate(Object predicate);

    Node newObject(Object object);

    Literal newLiteral(Object value);

    /**
     * Return the set of predicates.
     *
     * @return set of predicates
     */
    Set<IRI> predicates();

    /**
     * Return object list for a given predicate.
     *
     * @param predicate predicate
     * @return set of objects
     */
    List<Node> objects(IRI predicate);

    List<Node> objects(String predicate);

    /**
     * Return collection of embedded resources.
     * It is required to get the size of the collection,
     * for determining if a single value or an array must be
     * constructed.
     *
     * @param predicate the predicate
     * @return collection of embedded resources
     */
    Collection<Resource> embeddedResources(IRI predicate);

    List<Node> externalObjects(IRI predicate);

    /**
     * Add a triple to this resource.
     *
     * @param triple triple
     * @return resource
     */
    Resource add(Triple triple);

    /**
     * Get iterator over triples.
     *
     * @return statements
     */
    List<Triple> triples();

    /**
     * Get iterator over triples thats are properties of this resource.
     *
     * @return iterator over triple
     */
    List<Triple> properties();

    /**
     * Compact a predicate. Under the predicate, there is a single blank node
     * object with a single value for the same predicate. In such case, the
     * blank node can be removed and the single value can be promoted to the
     * predicate.
     *
     * @param predicate the predicate
     */
    void compactPredicate(IRI predicate);

    /**
     * Check if resource is empty, if it has no properties and no resources.
     *
     * @return true if empty
     */
    boolean isEmpty();

    /**
     * The size of the resource. It corresponds to the number of statements in this resource.
     *
     * @return the size
     */
    int size();

    /**
     * Check if marker for resource deletion is set.
     *
     * @return true if the marker ist set
     */
    boolean isDeleted();

    /**
     * Set marker for resource deletion.
     *
     * @param delete true if resource should be marked as deleted
     * @return resource
     */
    Resource setDeleted(boolean delete);

    /**
     *
     */
    @FunctionalInterface
    interface Mapper {
        void map(Resource r, String path, String value) throws IOException;
    }
}
