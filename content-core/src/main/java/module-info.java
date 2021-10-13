import org.xbib.content.XContent;

module org.xbib.content.core {
    uses XContent;
    exports org.xbib.content.io;
    exports org.xbib.content.util.geo;
    exports org.xbib.content.core;
    requires transitive org.xbib.content.api;
    requires com.fasterxml.jackson.core;
}
