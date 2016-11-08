package org.xbib.content.xml.json.events;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/**
 *
 */
public abstract class JsonReaderXmlEvent {

    private Location location;

    private QName name;

    private String text;

    private List<Attribute> attributes;

    protected void setQName(QName name) {
        this.name = name;
    }

    public abstract int getEventType();

    public boolean isAttribute() {
        return false;
    }

    public boolean isCharacters() {
        return false;
    }

    public boolean isEndDocument() {
        return false;
    }

    public boolean isEndElement() {
        return false;
    }

    public boolean isEntityReference() {
        return false;
    }

    public boolean isNamespace() {
        return false;
    }

    public boolean isProcessingInstruction() {
        return false;
    }

    public boolean isStartDocument() {
        return false;
    }

    public boolean isStartElement() {
        return false;
    }

    public int getAttributeCount() {
        return (null != attributes) ? attributes.size() : 0;
    }

    public String getAttributeLocalName(int index) {
        if ((null == attributes) || (index >= attributes.size())) {
            throw new IndexOutOfBoundsException();
        }
        return attributes.get(index).name.getLocalPart();
    }

    public QName getAttributeName(int index) {
        return attributes.get(index).name;
    }

    public String getAttributePrefix(int index) {
        if ((null == attributes) || (index >= attributes.size())) {
            throw new IndexOutOfBoundsException();
        }
        return null;
    }

    public String getAttributeType(int index) {
        return null;
    }

    public String getAttributeNamespace(int index) {
        return null;
    }

    public String getAttributeValue(int index) {
        if ((null == attributes) || (index >= attributes.size())) {
            throw new IndexOutOfBoundsException();
        }
        return attributes.get(index).value;
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        if ((null == attributes) || (null == localName) || ("".equals(localName))) {
            throw new NoSuchElementException();
        }
        for (Attribute a : attributes) {
            if (localName.equals(a.name.getLocalPart())) {
                return a.value;
            }
        }
        throw new NoSuchElementException();
    }

    public boolean isAttributeSpecified(int index) {
        return (null != attributes) && (attributes.size() >= index);
    }

    public String getText() {
        if (null != text) {
            return text;
        } else {
            throw new IllegalStateException();
        }
    }

    protected void setText(String text) {
        this.text = text;
    }

    public char[] getTextCharacters() {
        if (null != text) {
            return text.toCharArray();
        } else {
            throw new IllegalStateException();
        }
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        if (null != text) {
            System.arraycopy(text.toCharArray(), sourceStart, target, targetStart, length);
            return length;
        } else {
            throw new IllegalStateException();
        }
    }

    public int getTextStart() {
        if (null != text) {
            return 0;
        } else {
            throw new IllegalStateException();
        }
    }

    public int getTextLength() {
        if (null != text) {
            return text.length();
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean hasName() {
        return null != name;
    }

    public QName getName() {
        if (null != name) {
            return name;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public String getLocalName() {
        if (null != name) {
            return name.getLocalPart();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public String getPrefix() {
        if (null != name) {
            return name.getPrefix();
        } else {
            return null;
        }
    }

    public Location getLocation() {
        return location;
    }

    protected void setLocation(Location location) {
        this.location = location;
    }

    public void addAttribute(QName name, String value) {
        if (null == attributes) {
            attributes = new LinkedList<>();
        }
        attributes.add(new Attribute(name, value));
    }

    /**
     *
     */
    private static class Attribute {
        private final QName name;
        private final String value;

        Attribute(QName name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
