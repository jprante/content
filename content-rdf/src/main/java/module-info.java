module org.xbib.content.rdf {
    exports org.xbib.content.rdf;
    exports org.xbib.content.rdf.internal;
    exports org.xbib.content.rdf.io.json;
    exports org.xbib.content.rdf.io.nquads;
    exports org.xbib.content.rdf.io.ntriple;
    exports org.xbib.content.rdf.io.rdfxml;
    exports org.xbib.content.rdf.io.sink;
    exports org.xbib.content.rdf.io.source;
    exports org.xbib.content.rdf.io.turtle;
    exports org.xbib.content.rdf.io.xml;
    exports org.xbib.content.rdf.util;
    requires org.xbib.content.resource;
    requires org.xbib.content.xml;
    requires org.xbib.content.json;
}
