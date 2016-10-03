package org.xbib.content.xml.util;

import org.junit.Test;
import org.xbib.content.xml.stream.SaxEventConsumer;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;

/**
 *
 */
public class SaxEventConsumerTest {

    private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    @Test
    public void testSaxEventConsumer() throws Exception {
        SaxEventConsumer c = new SaxEventConsumer(new DefaultHandler());
        List<Namespace> namespaces = new LinkedList<>();
        c.add(eventFactory.createStartElement(new QName("http://localhost/elems/", "elementname"),
                getAttributes().iterator(), namespaces.iterator()));
        c.add(eventFactory.createEndElement(new QName("http://localhost/elems/", "elementname"),
                namespaces.iterator()));
    }

    private List<Attribute> getAttributes() {
        List<Attribute> list = new LinkedList<>();
        QName q = new QName("http://localhost/attrs/", "attributename");
        list.add(eventFactory.createAttribute(q, "attributevalue"));
        return list;
    }

}
