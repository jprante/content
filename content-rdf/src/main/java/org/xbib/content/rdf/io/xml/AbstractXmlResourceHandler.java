package org.xbib.content.rdf.io.xml;

import org.xbib.content.rdf.Literal;
import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.rdf.Resource;
import org.xbib.content.resource.IRI;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.namespace.QName;

/**
 * The XML resource handler can create nested RDF resources from arbitrary XML.
 *
 * @param <P> parameter type
 */
public abstract class AbstractXmlResourceHandler<P extends RdfContentParams>
        extends AbstractXmlHandler<P> implements XmlResourceHandler<P> {

    protected final LinkedList<Resource> stack = new LinkedList<>();

    public AbstractXmlResourceHandler(RdfContentParams params) {
        super(params);
    }

    @Override
    public void openResource() throws IOException {
        super.openResource();
        stack.push(getResource());
    }

    @Override
    public void closeResource() throws IOException {
        super.closeResource();
        stack.clear();
    }

    /**
     * Open a predicate. Create new resource, even if there will be only a single literal.
     * It will be compacted later.
     *
     * @param parent the parent
     * @param name   the name
     * @param level  the level
     */
    @Override
    public void openPredicate(QName parent, QName name, int level) {
        String elementName = makePrefix(name.getPrefix(), name.getLocalPart());
        IRI p = toProperty(getResource().newPredicate(elementName));
        stack.push(stack.peek().newResource(p));
    }

    @Override
    public void addToPredicate(QName parent, String content) {
    }

    @Override
    public void closePredicate(QName parent, QName name, int level) {
        Resource r = stack.pop();
        String elementName = makePrefix(name.getPrefix(), name.getLocalPart());
        if (level < 0 && !stack.isEmpty() && !r.isEmpty()) {
            IRI p = toProperty(getResource().newPredicate(elementName));
            stack.peek().add(p, r);
        } else {
            // it's a property with object
            String s = content();
            if (s != null && !stack.isEmpty()) {
                IRI p = toProperty(getResource().newPredicate(elementName));
                Object o = getResource().newObject(toObject(name, s));
                if (o instanceof Literal) {
                    r.add(p, (Literal) o);
                } else if (o instanceof Resource) {
                    Resource resource = (Resource) o;
                    if (!resource.isEmpty()) {
                        r.add(p, resource);
                    }
                }
                // compact predicate because it has only a single value
                stack.peek().compactPredicate(p);
                // optional rename. This can help if OAI XML source
                // emits both string.object under same element name which leads
                // to ElasticsearchIllegalArgumentException "unknown property"
                if (o instanceof Literal) {
                    String newElementName = toElementName(elementName);
                    if (!elementName.equals(newElementName)) {
                        stack.peek().rename(elementName, newElementName);
                    }
                }
            }
        }
    }

    public IRI toProperty(IRI property) {
        return property;
    }

    public String toElementName(String elementName) {
        return elementName;
    }

    @Override
    public Object toObject(QName name, String content) {
        return content;
    }
}
