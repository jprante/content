package org.xbib.content.rdf.io.sink;

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
     */
    void addNonLiteral(String subj, String pred, String obj);

    /**
     * Callback for handling triples with plain literal objects.
     *
     * @param subj    subject's IRI or BNode name
     * @param pred    predicate's IRI
     * @param content unescaped string representation of content
     * @param lang    content's lang, can be null if no language specified
     */
    void addPlainLiteral(String subj, String pred, String content, String lang);

    /**
     * Callback for handling triples with typed literal objects.
     *
     * @param subj    subject's IRI or BNode name
     * @param pred    predicate's IRI
     * @param content unescaped string representation of content
     * @param type    literal datatype's IRI
     */
    void addTypedLiteral(String subj, String pred, String content, String type);

}
