module org.xbib.content.xml {
    exports org.xbib.content.xml;
    exports org.xbib.content.xml.json;
    exports org.xbib.content.xml.json.events;
    exports org.xbib.content.xml.stream;
    exports org.xbib.content.xml.transform;
    exports org.xbib.content.xml.util;
    requires java.xml;
    requires org.xbib.content.core;
    requires org.xbib.content.resource;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.dataformat.xml;
    provides org.xbib.content.XContent with
            org.xbib.content.xml.XmlXContent;
}
