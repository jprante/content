import org.xbib.content.XContent;

module org.xbib.content.json {
    exports org.xbib.content.json;
    exports org.xbib.content.json.diff;
    exports org.xbib.content.json.jackson;
    exports org.xbib.content.json.mergepatch;
    exports org.xbib.content.json.patch;
    exports org.xbib.content.json.pointer;
    requires org.xbib.content.core;
    requires com.fasterxml.jackson.databind;
    provides XContent with org.xbib.content.json.JsonXContent;
}
