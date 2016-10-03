package org.xbib.content.yaml;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.xbib.content.XContent;
import org.xbib.content.XContentBuilder;
import org.xbib.content.XContentGenerator;
import org.xbib.content.XContentParser;
import org.xbib.content.io.BytesReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * A YAML based content implementation using Jackson.
 */
public class YamlXContent implements XContent {

    private static final YamlXContent yamlXContent;
    private static final YAMLFactory yamlFactory;

    static {
        yamlFactory = new YAMLFactory();
        yamlXContent = new YamlXContent();
    }

    public static YamlXContent yamlContent() {
        return yamlXContent;
    }

    public static YAMLFactory yamlFactory() {
        return yamlFactory;
    }

    public static XContentBuilder contentBuilder() throws IOException {
        return XContentBuilder.builder(yamlXContent);
    }

    public static XContentBuilder contentBuilder(OutputStream outputStream) throws IOException {
        return XContentBuilder.builder(yamlXContent, outputStream);
    }

    @Override
    public String name() {
        return "yaml";
    }

    @Override
    public XContentGenerator createGenerator(OutputStream os) throws IOException {
        return new YamlXContentGenerator(yamlFactory.createGenerator(os, JsonEncoding.UTF8));
    }


    @Override
    public XContentGenerator createGenerator(Writer writer) throws IOException {
        return new YamlXContentGenerator(yamlFactory.createGenerator(writer));
    }


    @Override
    public XContentParser createParser(String content) throws IOException {
        return new YamlXContentParser(yamlFactory
                .createParser(content.getBytes(StandardCharsets.UTF_8)));
    }


    @Override
    public XContentParser createParser(InputStream is) throws IOException {
        return new YamlXContentParser(yamlFactory.createParser(is));
    }


    @Override
    public XContentParser createParser(byte[] data) throws IOException {
        return new YamlXContentParser(yamlFactory.createParser(data));
    }


    @Override
    public XContentParser createParser(byte[] data, int offset, int length) throws IOException {
        return new YamlXContentParser(yamlFactory.createParser(data, offset, length));
    }


    @Override
    public XContentParser createParser(BytesReference bytes) throws IOException {
        return createParser(bytes.streamInput());
    }


    @Override
    public XContentParser createParser(Reader reader) throws IOException {
        return new YamlXContentParser(yamlFactory.createParser(reader));
    }

    @Override
    public boolean isXContent(BytesReference bytes) {
        int length = bytes.length() < 20 ? bytes.length() : 20;
        if (length == 0) {
            return false;
        }
        byte first = bytes.get(0);
        return length > 2 && first == '-' && bytes.get(1) == '-' && bytes.get(2) == '-';
    }
}
