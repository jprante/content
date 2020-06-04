module org.xbib.content.json {
    exports org.xbib.content.json.diff;
    exports org.xbib.content.json.jackson;
    exports org.xbib.content.json.mergepatch;
    exports org.xbib.content.json.patch;
    exports org.xbib.content.json.pointer;
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
}
