package org.xbib.content.xml.transform;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * An {@link javax.xml.transform.ErrorListener} that reacts to errors when transforming (applying) a
 * stylesheet.
 */
public final class TransformerErrorListener implements ErrorListener {

    public TransformerErrorListener() {
    }

    /**
     * We store the exception internally as a workaround to xalan, which reports
     * {@link javax.xml.transform.TransformerException} as {@link RuntimeException} (wrapped).
     */
    private TransformerException exception;

    /*
     *
     */
    @Override
    public void warning(TransformerException e) throws TransformerException {
        //logger.log(Level.WARNING, "Warning (recoverable): " + e.getMessage(), e);
    }

    /*
     *
     */
    @Override
    public void error(TransformerException e) throws TransformerException {
        //logger.log(Level.WARNING, "Error (recoverable): " + e.getMessage(), e);
    }

    /**
     * Unrecoverable errors cause an exception to be rethrown.
     */
    @Override
    public void fatalError(TransformerException e) throws TransformerException {
        //logger.log(Level.SEVERE, "Fatal error: " + e.getMessage(), e);
        this.exception = e;
        throw e;
    }

    public Exception getException() {
        return exception;
    }
}
