package org.xbib.content.rdf;

import org.xbib.content.rdf.io.json.JsonContent;
import org.xbib.content.rdf.io.ntriple.NTripleContent;
import org.xbib.content.rdf.io.rdfxml.RdfXmlContent;
import org.xbib.content.rdf.io.turtle.TurtleContent;
import org.xbib.content.rdf.io.xml.XmlContent;

/**
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public enum StandardRdfContentType implements RdfContentType {

    NTRIPLE(0) {
        @Override
        public String contentType() {
            return "application/n-triples";
        }

        @Override
        public String shortName() {
            return "n-triples";
        }

        @Override
        public RdfContent rdfContent() {
            return NTripleContent.nTripleContent();
        }
    },

    RDFXML(1) {
        @Override
        public String contentType() {
            return "application/rdf+xml";
        }

        @Override
        public String shortName() {
            return "rdf/xml";
        }

        @Override
        public RdfContent rdfContent() {
            return RdfXmlContent.rdfXmlContent();
        }
    },

    TURTLE(2) {
        @Override
        public String contentType() {
            return "text/turtle";
        }

        @Override
        public String shortName() {
            return "ttl";
        }

        @Override
        public RdfContent rdfContent() {
            return TurtleContent.turtleContent();
        }
    },

    XML(3) {
        @Override
        public String contentType() {
            return "text/xml";
        }

        @Override
        public String shortName() {
            return "xml";
        }

        @Override
        public RdfContent rdfContent() {
            return XmlContent.xmlContent();
        }
    },

    JSON(4) {
        @Override
        public String contentType() {
            return "text/json";
        }

        @Override
        public String shortName() {
            return "json";
        }

        @Override
        public RdfContent rdfContent() {
            return JsonContent.jsonContent();
        }
    };

    private int index;

    StandardRdfContentType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public abstract String contentType();

    @Override
    public abstract String shortName();

    @Override
    public abstract RdfContent rdfContent();
}
