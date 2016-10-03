package org.xbib.content.smile;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.dataformat.smile.SmileConstants;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;
import org.xbib.content.XContent;
import org.xbib.content.XContentBuilder;
import org.xbib.content.XContentGenerator;
import org.xbib.content.XContentParser;
import org.xbib.content.io.BytesReference;
import org.xbib.content.json.JsonXContentParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * A JSON based content implementation using Jackson.
 */
public class SmileXContent implements XContent {

    private static final SmileXContent smileXContent;
    private static final SmileFactory smileFactory;

    static {
        smileFactory = new SmileFactory();
        // for now, this is an overhead, might make sense for web sockets
        smileFactory.configure(SmileGenerator.Feature.ENCODE_BINARY_AS_7BIT, false);
        smileXContent = new SmileXContent();
    }

    /**
     * Empty constructor for {@link java.util.ServiceLoader}.
     */
    public SmileXContent() {
        // for ServiceLoader
    }

    public static SmileFactory smileFactory() {
        return smileFactory;
    }

    public static SmileXContent smileContent() {
        return smileXContent;
    }

    public static XContentBuilder contentBuilder() throws IOException {
        return XContentBuilder.builder(smileXContent);
    }

    public static XContentBuilder contentBuilder(OutputStream outputStream) throws IOException {
        return XContentBuilder.builder(smileXContent, outputStream);
    }

    @Override
    public String name() {
        return "smile";
    }

    @Override
    public XContentGenerator createGenerator(OutputStream os) throws IOException {
        return new SmileXContentGenerator(smileFactory.createGenerator(os, JsonEncoding.UTF8));
    }


    @Override
    public XContentGenerator createGenerator(Writer writer) throws IOException {
        return new SmileXContentGenerator(smileFactory.createGenerator(writer));
    }

    @Override
    public XContentParser createParser(String content) throws IOException {
        return new SmileXContentParser(smileFactory
                .createParser(content.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public XContentParser createParser(InputStream is) throws IOException {
        return new SmileXContentParser(smileFactory.createParser(is));
    }

    @Override
    public XContentParser createParser(byte[] data) throws IOException {
        return new SmileXContentParser(smileFactory.createParser(data));
    }

    @Override
    public XContentParser createParser(byte[] data, int offset, int length) throws IOException {
        return new SmileXContentParser(smileFactory.createParser(data, offset, length));
    }

    @Override
    public XContentParser createParser(BytesReference bytes) throws IOException {
        return createParser(bytes.streamInput());
    }

    @Override
    public XContentParser createParser(Reader reader) throws IOException {
        return new JsonXContentParser(smileFactory.createParser(reader));
    }

    @Override
    public boolean isXContent(BytesReference bytes) {
        int length = bytes.length() < 20 ? bytes.length() : 20;
        if (length == 0) {
            return false;
        }
        byte first = bytes.get(0);
        return length > 2 && first == SmileConstants.HEADER_BYTE_1 && bytes.get(1) == SmileConstants.HEADER_BYTE_2
                && bytes.get(2) == SmileConstants.HEADER_BYTE_3;
    }
}
