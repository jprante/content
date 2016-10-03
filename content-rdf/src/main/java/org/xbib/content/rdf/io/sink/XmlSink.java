package org.xbib.content.rdf.io.sink;

import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * Sink interface for streaming XML processors.
 */
public interface XmlSink extends Sink, ContentHandler, LexicalHandler {

}
