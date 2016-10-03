package org.xbib.content.yaml;

import com.fasterxml.jackson.core.JsonParser;
import org.xbib.content.XContent;
import org.xbib.content.json.JsonXContentParser;

/**
 *
 */
public class YamlXContentParser extends JsonXContentParser {

    public YamlXContentParser(JsonParser parser) {
        super(parser);
    }

    @Override
    public XContent content() {
        return YamlXContent.yamlContent();
    }
}
