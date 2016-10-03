package org.xbib.content.xml.transform;

import java.io.IOException;

/**
 * A callback listener for providing information about the content type and encoding of
 * the output.
 */
public interface ContentTypeListener {

    void setContentType(String contentType, String encoding) throws IOException;
}
