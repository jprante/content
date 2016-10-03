package org.xbib.content.rdf.internal;

import org.xbib.content.rdf.Literal;
import org.xbib.content.rdf.RdfConstants;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.Triple;
import org.xbib.content.rdf.XSDResourceIdentifiers;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.IRINamespaceContext;
import org.xbib.content.resource.Node;
import org.xbib.content.rdf.util.LinkedHashMultiMap;
import org.xbib.content.rdf.util.MultiMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A resource is a sequence of properties and of associated resources.
 */
public class DefaultResource implements Resource, Comparable<Resource>, XSDResourceIdentifiers {

    static final String GENID = "genid";
    static final String PLACEHOLDER = "_:";
    private static final Logger logger = Logger.getLogger(DefaultResource.class.getName());
    private static final String UNDERSCORE = "_";

    private final MultiMap<IRI, Node> attributes;

    private final Map<IRI, Resource> children;

    private IRI iri;

    private boolean embedded;

    private boolean deleted;

    public DefaultResource(IRI iri) {
        this(iri, new LinkedHashMultiMap<>(), new LinkedHashMap<>());
    }

    public DefaultResource(DefaultResource resource) {
        this(resource.id(), resource.getAttributes(), resource.getChildren());
        this.deleted = resource.isDeleted();
    }

    public DefaultResource(IRI iri, MultiMap<IRI, Node> attributes, Map<IRI, Resource> children) {
        setId(iri);
        this.attributes = attributes;
        this.children = children;
    }

    public static Resource create(String id) {
        return new DefaultResource(IRI.builder().curie(id).build());
    }

    public static Resource create(IRINamespaceContext context, String id) {
        return new DefaultResource(context.expandIRI(IRI.builder().curie(id).build()));
    }

    public static boolean isBlank(Resource resource) {
        if (resource == null) {
            return false;
        }
        String scheme = resource.id().getScheme();
        return scheme != null && (GENID.equals(scheme) || UNDERSCORE.equals(scheme));
    }

    public static Resource from(Map<String, Object> map, Mapper mapper) throws IOException {
        Resource r = new DefaultAnonymousResource();
        map(mapper, r, null, map);
        return r;
    }

    @SuppressWarnings("unchecked")
    private static void map(Mapper mapper, Resource r, String prefix, Map<String, Object> map) throws IOException {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String p = prefix != null ? prefix + "." + key : key;
            Object value = entry.getValue();
            if (value instanceof Map) {
                map(mapper, r, p, (Map<String, Object>) value);
            } else if (value instanceof List) {
                for (Object o : (List) value) {
                    if (o instanceof Map) {
                        map(mapper, r, p, (Map<String, Object>) o);
                    } else {
                        mapper.map(r, p, o.toString());
                    }
                }
            } else {
                if (value != null) {
                    mapper.map(r, p, value.toString());
                }
            }
        }
    }

    @Override
    public DefaultResource setId(IRI id) {
        this.iri = id;
        if (id != null) {
            this.embedded = isBlank(this);
        }
        return this;
    }

    @Override
    public IRI id() {
        return iri;
    }

    @Override
    public int compareTo(Resource r) {
        return iri != null ? iri.toString().compareTo(r.id().toString()) : -1;
    }

    @Override
    public int hashCode() {
        return iri != null ? iri.toString().hashCode() : -1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Resource && iri != null && iri.toString().equals(((Resource) obj).id().toString());
    }

    @Override
    public boolean isEmbedded() {
        return embedded;
    }

    public MultiMap<IRI, Node> getAttributes() {
        return attributes;
    }

    public Map<IRI, Resource> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return embedded ? PLACEHOLDER + (iri != null ? iri.getSchemeSpecificPart() : "<null>") :
                (iri != null ? iri.toString() : "<null>");
    }

    @Override
    public Resource add(Triple triple) {
        if (triple == null) {
            return this;
        }
        IRI otherId = triple.subject().id();
        if (otherId == null || otherId.equals(id())) {
            add(triple.predicate(), triple.object());
        } else {
            Resource child = children.get(otherId);
            if (child != null) {
                return child.add(triple);
            } else {
                // nothing found, continue with a new resource with new subject
                logger.info("nothing found!!! my ID is " + id());
                return new DefaultResource(otherId).add(triple);
            }
        }
        return this;
    }

    @Override
    public Resource add(IRI predicate, Node object) {
        attributes.put(predicate, object);
        if (object instanceof Resource) {
            Resource r = (Resource) object;
            children.put(r.id(), r);
        }
        return this;
    }

    @Override
    public Resource add(IRI predicate, IRI iri) {
        return add(predicate, new DefaultResource(iri));
    }

    @Override
    public Resource add(IRI predicate, Literal literal) {
        if (predicate != null && literal != null) {
            attributes.put(predicate, literal);
        }
        return this;
    }

    @Override
    public Resource add(IRI predicate, Resource resource) {
        if (resource == null) {
            return this;
        }
        if (resource.id() == null) {
            resource.setId(id()); // side effect, transfer our ID to other resource
            Resource r = newResource(predicate);
            resource.triples().forEach(r::add);
        } else {
            attributes.put(predicate, resource);
        }
        return this;
    }

    @Override
    public Resource add(IRI predicate, String value) {
        return add(predicate, newLiteral(value));
    }

    @Override
    public Resource add(IRI predicate, Integer value) {
        return add(predicate, newLiteral(value));
    }

    @Override
    public Resource add(IRI predicate, Boolean value) {
        return add(predicate, newLiteral(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Resource add(IRI predicate, List<Object> list) {
        list.forEach(object -> {
            if (object instanceof Map) {
                add(predicate, (Map) object);
            } else if (object instanceof List) {
                add(predicate, ((List) object));
            } else if (object instanceof Resource) {
                add(predicate, (Resource) object);
            } else {
                add(predicate, newLiteral(object));
            }
        });
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Resource add(IRI predicate, Map<Object, Object> map) {
        Resource r = newResource(predicate);
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object pred = entry.getKey();
            Object obj = entry.getValue();
            if (obj instanceof Map) {
                r.add(newPredicate(pred), (Map<Object, Object>) obj);
            } else if (obj instanceof List) {
                r.add(newPredicate(pred), ((List) obj));
            } else if (obj instanceof Resource) {
                r.add(newPredicate(pred), (Resource) obj);
            } else {
                r.add(newPredicate(pred), newLiteral(obj));
            }
        }
        return this;
    }

    @Override
    public Resource add(String predicate, String value) {
        return add(newPredicate(predicate), value);
    }

    @Override
    public Resource add(String predicate, Integer value) {
        return add(newPredicate(predicate), value);
    }

    @Override
    public Resource add(String predicate, Boolean value) {
        return add(newPredicate(predicate), value);
    }

    @Override
    public Resource add(String predicate, Literal value) {
        return add(newPredicate(predicate), value);
    }

    @Override
    public Resource add(String predicate, IRI externalResource) {
        return add(newPredicate(predicate), externalResource);
    }

    @Override
    public Resource add(String predicate, Resource resource) {
        return add(newPredicate(predicate), resource);
    }

    @Override
    public Resource add(String predicate, Map<Object, Object> map) {
        return add(newPredicate(predicate), map);
    }

    @Override
    public Resource add(String predicate, List<Object> list) {
        return add(newPredicate(predicate), list);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Resource add(Map<Object, Object> map) {
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object pred = entry.getKey();
            Object obj = entry.getValue();
            if (obj instanceof Map) {
                Resource r = newResource(newPredicate(pred));
                r.add((Map) obj);
            } else if (obj instanceof List) {
                add(newPredicate(pred), ((List) obj));
            } else if (obj instanceof Resource) {
                add(newPredicate(pred), (Resource) obj);
            } else {
                add(newPredicate(pred), newLiteral(obj));
            }
        }
        return this;
    }

    @Override
    public Resource rename(IRI oldPredicate, IRI newPredicate) {
        Collection<Node> node = attributes.remove(oldPredicate);
        if (node != null) {
            node.forEach(n -> attributes.put(newPredicate, n));
        }
        Resource resource = children.remove(oldPredicate);
        if (resource != null) {
            children.put(newPredicate, resource);
        }
        return this;
    }

    @Override
    public Resource rename(String oldPredicate, String newPredicate) {
        rename(newPredicate(oldPredicate), newPredicate(newPredicate));
        return this;
    }

    public Resource remove(IRI predicate) {
        if (predicate == null) {
            return this;
        }
        // check if child resource exists for any of the objects under this predicate and remove it
        embeddedResources(predicate).forEach(resource -> children.remove(resource.id()));
        attributes.remove(predicate);
        return this;
    }

    public Resource remove(IRI predicate, Node object) {
        if (predicate == null) {
            return this;
        }
        attributes.remove(predicate, object);
        return this;
    }

    @Override
    public Resource a(IRI externalResource) {
        add(newPredicate(RdfConstants.RDF_TYPE), externalResource);
        return this;
    }

    @Override
    public Set<IRI> predicates() {
        return attributes.keySet();
    }

    @Override
    public List<Node> objects(IRI predicate) {
        return attributes.containsKey(predicate) ? new ArrayList<>(attributes.get(predicate)) : new ArrayList<>();
    }

    @Override
    public List<Node> objects(String predicate) {
        return objects(newPredicate(predicate));
    }

    @Override
    public List<Resource> resources(IRI predicate) {
        return attributes.get(predicate).stream()
                .filter(n -> n instanceof Resource)
                .map(Resource.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public List<Resource> embeddedResources(IRI predicate) {
        return attributes.get(predicate).stream()
                .filter(n -> n instanceof Resource)
                .map(Resource.class::cast)
                .filter(Resource::isEmbedded)
                .collect(Collectors.toList());
    }

    @Override
    public List<Node> externalObjects(IRI predicate) {
        return attributes.get(predicate).stream()
                .filter(n -> !n.isEmbedded())
                .collect(Collectors.toList());
    }

    /**
     * Compact a predicate with a single blank node object.
     * If there is a single blank node object with values for the same predicate, the
     * blank node can be dropped and the values can be promoted to the predicate.
     *
     * @param predicate the predicate
     */
    @Override
    public void compactPredicate(IRI predicate) {
        List<Resource> resources = embeddedResources(predicate);
        if (resources.size() == 1) {
            Resource r = resources.iterator().next();
            attributes.remove(predicate, r);
            r.objects(predicate).forEach(object -> attributes.put(predicate, object));
        }
    }

    @Override
    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    @Override
    public int size() {
        return attributes.size();
    }

    @Override
    public Resource setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public Resource newResource(IRI predicate) {
        Resource r = new DefaultAnonymousResource();
        children.put(r.id(), r);
        attributes.put(predicate, r);
        return r;
    }

    @Override
    public Resource newResource(String predicate) {
        return newResource(newPredicate(predicate));
    }

    @Override
    public List<Triple> triples() {
        return new Triples(this, true).list();
    }

    @Override
    public List<Triple> properties() {
        return new Triples(this, false).list();
    }

    @Override
    public Resource newSubject(Object subject) {
        return subject == null ? null :
                subject instanceof Resource ? (Resource) subject :
                        subject instanceof IRI ? new DefaultResource((IRI) subject) :
                                new DefaultResource(IRI.builder().curie(subject.toString()).build());
    }

    @Override
    public IRI newPredicate(Object predicate) {
        return predicate == null ? null :
                predicate instanceof IRI ? (IRI) predicate :
                        IRI.builder().curie(predicate.toString()).build();
    }

    @Override
    public Node newObject(Object object) {
        return object == null ? null :
                object instanceof Literal ? (Literal) object :
                        object instanceof IRI ? new DefaultResource((IRI) object) :
                                new DefaultLiteral(object);
    }

    @Override
    public Literal newLiteral(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Literal) {
            return (Literal) value;
        }
        if (value instanceof Double) {
            return new DefaultLiteral(value).type(DOUBLE);
        }
        if (value instanceof Float) {
            return new DefaultLiteral(value).type(FLOAT);
        }
        if (value instanceof Long) {
            return new DefaultLiteral(value).type(LONG);
        }
        if (value instanceof Integer) {
            return new DefaultLiteral(value).type(INT);
        }
        if (value instanceof Boolean) {
            return new DefaultLiteral(value).type(BOOLEAN);
        }
        // untyped
        return new DefaultLiteral(value);
    }

    public List<Triple> find(IRI predicate, Literal literal) {
        return new Triples(this, predicate, literal).list();
    }

    private static class Triples {

        private final List<Triple> triples;

        private final boolean recursive;

        Triples(Resource resource, boolean recursive) {
            this.recursive = recursive;
            this.triples = unfold(resource);
        }

        Triples(Resource resource, IRI predicate, Literal literal) {
            this.recursive = true;
            this.triples = find(resource, predicate, literal);
        }

        List<Triple> list() {
            return triples;
        }

        private List<Triple> unfold(Resource resource) {
            final List<Triple> list = new ArrayList<>();
            if (resource == null) {
                return list;
            }
            for (IRI pred : resource.predicates()) {
                resource.objects(pred).forEach(node -> {
                    DefaultTriple defaultTriple = new DefaultTriple(resource, pred, node);
                    list.add(defaultTriple);
                    if (recursive && node instanceof Resource) {
                        list.addAll(unfold((Resource) node));
                    }
                });
            }
            return list;
        }

        private List<Triple> find(Resource resource, IRI predicate, Literal literal) {
            final List<Triple> list = new ArrayList<>();
            if (resource == null) {
                return list;
            }
            if (resource.predicates().contains(predicate)) {
                resource.objects(predicate).forEach(node -> {
                    if (literal.equals(node)) {
                        list.add(new DefaultTriple(resource, predicate, node));
                    }
                });
                if (!list.isEmpty()) {
                    return list;
                }
            } else {
                for (IRI pred : resource.predicates()) {
                    resource.objects(pred).forEach(node -> {
                        if (node instanceof Resource) {
                            list.addAll(find((Resource) node, predicate, literal));
                        }
                    });
                }
            }
            return list;
        }
    }

}
