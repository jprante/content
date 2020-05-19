module org.xbib.content.json {
    exports org.xbib.content.json.diff;
    exports org.xbib.content.json.jackson;
    exports org.xbib.content.json.mergepatch;
    exports org.xbib.content.json.patch;
    exports org.xbib.content.json.pointer;
    requires com.fasterxml.jackson.databind;
}
