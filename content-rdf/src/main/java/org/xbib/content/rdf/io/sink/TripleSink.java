package org.xbib.content.rdf.io.sink;

import java.io.IOException;

/**
 * Interface for triple consuming.
 */
public interface TripleSink extends Sink {

    /**
     * Callback for handling triples with non literal object.
     *
     * @param subj subject's IRI or BNode name
     * @param pred predicate's IRI
     * @param obj  object's IRI or BNode name
     * @throws IOException if handling of triple fails
     */
    void addNonLiteral(String subj, String pred, String obj) throws IOException;

    /**
     * Callback for handling triples with plain literal objects.
     *
     * @param subj    subject's IRI or BNode name
     * @param pred    predicate's IRI
     * @param content unescaped string representation of content
     * @param lang    content's lang, can be null if no language specified
     * @throws IOException if handling of triple fails
     */
    void addPlainLiteral(String subj, String pred, String content, String lang) throws IOException;

    /**
     * Callback for handling triples with typed literal objects.
     *
     * @param subj    subject's IRI or BNode name
     * @param pred    predicate's IRI
     * @param content unescaped string representation of content
     * @param type    literal datatype's IRI
     * @throws IOException if handling of triple fails
     */
    void addTypedLiteral(String subj, String pred, String content, String type) throws IOException;
}
