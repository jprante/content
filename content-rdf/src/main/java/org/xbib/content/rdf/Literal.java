package org.xbib.content.rdf;

import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;

/**
 * A literal is a value with a type and/or a language.
 */
public interface Literal extends Node, XSDResourceIdentifiers {

    /**
     * Set value for the literal.
     *
     * @param value the value
     * @return literal
     */
    Literal object(Object value);

    /**
     * Get the value.
     *
     * @return the value
     */
    Object object();

    /**
     * Set type of the literal.
     *
     * @param type the type
     * @return literal
     */
    Literal type(IRI type);

    /**
     * Get type of the literal.
     *
     * @return the type
     */
    IRI type();

    /**
     * Set the language of the literal.
     *
     * @param language the W3C language tag
     * @return literal
     */
    Literal lang(String language);

    /**
     * Get language of the literal.
     *
     * @return the language
     */
    String lang();

}
