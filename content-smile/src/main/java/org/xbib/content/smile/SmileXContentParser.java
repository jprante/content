package org.xbib.content.smile;

import com.fasterxml.jackson.core.JsonParser;
import org.xbib.content.XContent;
import org.xbib.content.json.JsonXContentParser;

/**
 *
 */
public class SmileXContentParser extends JsonXContentParser {

    public SmileXContentParser(JsonParser parser) {
        super(parser);
    }

    @Override
    public XContent content() {
        return SmileXContent.smileContent();
    }

}
