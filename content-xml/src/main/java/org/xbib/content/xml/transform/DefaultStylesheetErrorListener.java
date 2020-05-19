package org.xbib.content.xml.transform;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * An {@link javax.xml.transform.ErrorListener} that reacts to errors when parsing (compiling) the stylesheet.
 */
public class DefaultStylesheetErrorListener implements ErrorListener {

    @Override
    public void warning(TransformerException e) throws TransformerException {
        //logger.log(Level.WARNING, "warning (recoverable): " + e.getMessage(), e);
    }

    @Override
    public void error(TransformerException e) throws TransformerException {
        //logger.log(Level.WARNING, "error (recoverable): " + e.getMessage(), e);
    }

    /**
     * Unrecoverable errors cause an exception to be rethrown.
     */
    @Override
    public void fatalError(TransformerException e) throws TransformerException {
        //logger.log(Level.WARNING, "fatal error: " + e.getMessage(), e);
        throw e;
    }
}
