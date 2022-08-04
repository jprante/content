package org.xbib.content.core;

import org.xbib.content.XContent;
import org.xbib.content.XContentBuilder;
import org.xbib.content.io.BytesArray;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

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

    private XContentService() {
    }

    public static XContentBuilder builder(String name) throws IOException {
        return xcontents.containsKey(name) ? DefaultXContentBuilder.builder(xcontents.get(name)) : null;
    }

    public static XContent xContent(String charSequence) {
        BytesArray bytesArray = new BytesArray(charSequence.getBytes(StandardCharsets.UTF_8));
        return xContent(bytesArray.toBytes(), 0, bytesArray.length());
    }

    public static XContent xContent(byte[] data, int offset, int length) {
        for (XContent xcontent : xcontents.values()) {
            if (xcontent.isXContent(data, offset, length)) {
                return xcontent;
            }
        }
        return null;
    }
}
