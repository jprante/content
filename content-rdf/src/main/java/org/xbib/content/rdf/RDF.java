package org.xbib.content.rdf;

/**
 * Defines URIs for the RDF vocabulary terms and bnode constans used by framework.
 */
public interface RDF {

    String NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    String BNODE_PREFIX = "_:";
    String SHORTENABLE_BNODE_SUFFIX = "sbl";
    String PROPERTY_ELEMENT = NS + "Property";
    String XML_LITERAL = NS + "XMLLiteral";
    String TYPE = NS + "type";
    String VALUE = NS + "value";
    String ALT_ELEMENT = NS + "Alt";
    String BAG_ELEMENT = NS + "Bag";
    String SEQ_ELEMENT = NS + "Seq";
    String LIST_ELEMENT = NS + "List";
    String FIRST = NS + "first";
    String NIL = NS + "nil";
    String REST = NS + "rest";
    String STATEMENT_ELEMENT = NS + "Statement";
    String OBJECT = NS + "object";
    String PREDICATE = NS + "predicate";
    String SUBJECT = NS + "subject";
    String DESCRIPTION_ELEMENT = NS + "Description";
    String ID = NS + "ID";
    String RDF_ELEMENT = NS + "RDF";
    String ABOUT = NS + "about";
    String DATATYPE = NS + "datatype";
    String LI = NS + "li";
    String NODEID = NS + "nodeID";
    String PARSE_TYPE = NS + "parseType";
    String RESOURCE = NS + "resource";

}
