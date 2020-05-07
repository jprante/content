package org.xbib.content.yaml;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import org.xbib.content.XContent;
import org.xbib.content.io.BytesReference;
import org.xbib.content.json.JsonXContentGenerator;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public class YamlXContentGenerator extends JsonXContentGenerator {

    public YamlXContentGenerator(JsonGenerator generator) {
        super(generator);
    }

    @Override
    public XContent content() {
        return YamlXContent.yamlContent();
    }

    @Override
    public void writeRawField(String fieldName, byte[] content, OutputStream outputStream) throws IOException {
        writeFieldName(fieldName);
        try (YAMLParser parser = YamlXContent.yamlFactory().createParser(content)) {
            parser.nextToken();
            YamlXContent.yamlFactory().createGenerator(outputStream).copyCurrentStructure(parser);
        }
    }

    @Override
    public void writeRawField(String fieldName, BytesReference content, OutputStream outputStream) throws IOException {
        writeFieldName(fieldName);
        try (YAMLParser parser = YamlXContent.yamlFactory().createParser(content.streamInput())) {
            parser.nextToken();
            YamlXContent.yamlFactory().createGenerator(outputStream).copyCurrentStructure(parser);
        }
    }

    @Override
    public void writeRawField(String fieldName, byte[] content, int offset, int length, OutputStream outputStream)
            throws IOException {
        writeFieldName(fieldName);
        try (YAMLParser parser = YamlXContent.yamlFactory().createParser(content, offset, length)) {
            parser.nextToken();
            YamlXContent.yamlFactory().createGenerator(outputStream).copyCurrentStructure(parser);
        }
    }
}
