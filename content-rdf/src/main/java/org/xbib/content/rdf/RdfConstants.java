package org.xbib.content.rdf;

import org.xbib.content.resource.IRI;

/**
 *
 */
public interface RdfConstants {

    String NS_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    String NS_PREFIX = "rdf";

    IRI RDF = IRI.create(NS_URI);
    IRI RDF_RDF = IRI.create(NS_URI + "RDF");
    IRI RDF_DESCRIPTION = IRI.create(NS_URI + "Description");
    IRI RDF_ABOUT = IRI.create(NS_URI + "about");
    IRI RDF_RESOURCE = IRI.create(NS_URI + "resource");
    IRI RDF_NODE_ID = IRI.create(NS_URI + "nodeID");
    IRI RDF_ID = IRI.create(NS_URI + "ID");
    IRI RDF_LI = IRI.create(NS_URI + "li");
    IRI RDF_TYPE = IRI.create(NS_URI + "type");
    IRI RDF_SUBJECT = IRI.create(NS_URI + "subject");
    IRI RDF_PREDICATE = IRI.create(NS_URI + "predicate");
    IRI RDF_OBJECT = IRI.create(NS_URI + "object");
    IRI RDF_STATEMENT = IRI.create(NS_URI + "Statement");
    IRI RDF_XMLLITERAL = IRI.create(NS_URI + "XMLLiteral");
    IRI RDF_NIL = IRI.create(NS_URI + "nil");
    IRI RDF_FIRST = IRI.create(NS_URI + "first");
    IRI RDF_REST = IRI.create(NS_URI + "rest");
    IRI RDF_LANGUAGE = IRI.create("xmlns:lang");

    String RDF_STRING = RDF.toString();

    String NODE_ID = "nodeID";

    String ABOUT = "about";

}
