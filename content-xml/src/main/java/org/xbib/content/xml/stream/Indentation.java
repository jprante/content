package org.xbib.content.xml.stream;

/**
 * Characters that represent line breaks and indentation. These are represented
 * as String-valued JavaBean properties.
 */
public interface Indentation {

    /**
     * Two spaces; the default indentation.
     */
    String DEFAULT_INDENT = "  ";
    /**
     * "\n"; the normalized representation of end-of-line in
     * <a href="http://www.w3.org/TR/xml11/#sec-line-ends">XML</a>.
     */
    String NORMAL_END_OF_LINE = "\n";

    /**
     * The characters used for one level of indentation.
     * @return the indentation string
     */
    String getIndent();

    /**
     * Set the characters used for one level of indentation. The default is
     * {@link #DEFAULT_INDENT}. "\t" is a popular alternative.
     * @param indent the indentation string
     */
    void setIndent(String indent);
}
