module org.xbib.content.smile {
    exports org.xbib.content.smile;
    requires transitive org.xbib.content.core;
    requires transitive com.fasterxml.jackson.dataformat.smile;
    requires com.fasterxml.jackson.core;
    provides org.xbib.content.XContent with
            org.xbib.content.smile.SmileXContent;
}
