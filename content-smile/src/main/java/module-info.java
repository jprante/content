module org.xbib.content.smile {
    exports org.xbib.content.smile;
    requires org.xbib.content.core;
    requires com.fasterxml.jackson.dataformat.smile;
    provides org.xbib.content.XContent with
            org.xbib.content.smile.SmileXContent;
}
