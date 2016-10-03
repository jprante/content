package org.xbib.content.rdf.io.json;

import org.xbib.content.rdf.io.xml.AbstractXmlResourceHandler;

/**
 *
 */
public abstract class JsonResourceHandler extends AbstractXmlResourceHandler<JsonContentParams> {

    public JsonResourceHandler(JsonContentParams params) {
        super(params);
        super.setDefaultNamespace("", "http://json.org");
    }

}
