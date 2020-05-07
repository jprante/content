package org.xbib.content;

import org.xbib.content.io.BytesArray;
import org.xbib.content.io.BytesReference;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 *
 */
public class XContentService {

    private static final Map<String, XContent> xcontents = new HashMap<>();

    static {
        ServiceLoader<XContent> loader = ServiceLoader.load(XContent.class);
        for (XContent xContent : loader) {
            if (!xcontents.containsKey(xContent.name())) {
                xcontents.put(xContent.name(), xContent);
            }
        }
    }

    public static XContentBuilder builder(String name) throws IOException {
        return xcontents.containsKey(name) ? XContentBuilder.builder(xcontents.get(name)) : null;
    }

    public static XContent xContent(byte[] data, int offset, int length) {
        return xContent(new BytesArray(data, offset, length));
    }

    public static XContent xContent(String charSequence) {
        return xContent(new BytesArray(charSequence.getBytes(StandardCharsets.UTF_8)));
    }

    public static XContent xContent(BytesReference bytes) {
        for (XContent xcontent : xcontents.values()) {
            if (xcontent.isXContent(bytes)) {
                return xcontent;
            }
        }
        return null;
    }
}
