import org.xbib.content.XContent;
module org.xbib.content.xml {
    exports org.xbib.content.xml;
    exports org.xbib.content.xml.json;
    exports org.xbib.content.xml.json.events;
    exports org.xbib.content.xml.stream;
    exports org.xbib.content.xml.transform;
    exports org.xbib.content.xml.util;
    requires transitive java.xml;
    requires transitive org.xbib.content.core;
    requires transitive org.xbib.content.resource;
    requires transitive com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.core;
    provides XContent with org.xbib.content.xml.XmlXContent;
}
