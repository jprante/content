package org.xbib.content.xml;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/**
 * XML parameters for XML content.
 */
public class XmlXParams {

    private static final XmlXParams DEFAULT_PARAMS =
            new XmlXParams(new QName("root"),
                    XmlNamespaceContext.newInstance(),
                    createXmlFactory(createXMLInputFactory(), createXMLOutputFactory()));

    private final XmlNamespaceContext namespaceContext;

    private XmlFactory xmlFactory;

    private QName root;

    private boolean fatalNamespaceErrors;

    public XmlXParams() {
        this(null, null, null);
    }

    public XmlXParams(QName root) {
        this(root, null, null);
    }

    public XmlXParams(XmlNamespaceContext namespaceContext) {
        this(null, namespaceContext, null);
    }

    public XmlXParams(QName root, XmlNamespaceContext namespaceContext) {
        this(root, namespaceContext, null);
    }

    public XmlXParams(QName root, XmlNamespaceContext namespaceContext, XmlFactory xmlFactory) {
        this.namespaceContext = namespaceContext == null ?
                XmlNamespaceContext.newInstance() :
                namespaceContext;
        this.xmlFactory = xmlFactory == null ?
                createXmlFactory(createXMLInputFactory(), createXMLOutputFactory()) :
                xmlFactory;
        this.root = root == null ? new QName("root") : root;
        String prefix = this.root.getPrefix();
        if (prefix != null && !prefix.isEmpty()) {
            this.namespaceContext.addNamespace(prefix, this.root.getNamespaceURI());
        }
    }

    public static XmlXParams getDefaultParams() {
        return DEFAULT_PARAMS;
    }

    protected static XMLInputFactory createXMLInputFactory() {
        // load from service factories in META-INF/services
        // default impl is "com.sun.xml.internal.stream.XMLInputFactoryImpl"
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        inputFactory.setProperty("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
        inputFactory.setProperty("javax.xml.stream.isValidating", Boolean.FALSE);
        inputFactory.setProperty("javax.xml.stream.isCoalescing", Boolean.TRUE);
        inputFactory.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.FALSE);
        inputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
        return inputFactory;
    }

    protected static XMLOutputFactory createXMLOutputFactory() {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.FALSE);
        return outputFactory;
    }

    protected static XmlFactory createXmlFactory(XMLInputFactory inputFactory, XMLOutputFactory outputFactory) {
        XmlFactory xmlFactory = new XmlFactory(inputFactory, outputFactory);
        xmlFactory.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
        return xmlFactory;
    }

    public XmlNamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

    public XmlFactory getXmlFactory() {
        return xmlFactory;
    }

    public QName getRoot() {
        return root;
    }

    public XmlXParams setFatalNamespaceErrors() {
        this.fatalNamespaceErrors = true;
        return this;
    }

    public boolean isFatalNamespaceErrors() {
        return fatalNamespaceErrors;
    }
}
