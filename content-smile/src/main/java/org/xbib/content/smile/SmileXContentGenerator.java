package org.xbib.content.smile;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.smile.SmileParser;
import org.xbib.content.XContent;
import org.xbib.content.io.BytesReference;
import org.xbib.content.json.JsonXContentGenerator;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public class SmileXContentGenerator extends JsonXContentGenerator {

    public SmileXContentGenerator(JsonGenerator generator) {
        super(generator);
    }

    @Override
    public XContent content() {
        return SmileXContent.smileContent();
    }

    @Override
    public void writeRawField(String fieldName, byte[] content, OutputStream outputStream) throws IOException {
        writeFieldName(fieldName);
        try (SmileParser parser = SmileXContent.smileFactory().createParser(content)) {
            parser.nextToken();
            ((JsonGenerator) generator).copyCurrentStructure(parser);
        }
    }

    @Override
    public void writeRawField(String fieldName, BytesReference content, OutputStream bos) throws IOException {
        writeFieldName(fieldName);
        try (SmileParser parser = SmileXContent.smileFactory().createParser(content.streamInput())) {
            parser.nextToken();
            ((JsonGenerator) generator).copyCurrentStructure(parser);
        }
    }

    @Override
    public void writeRawField(String fieldName, byte[] content, int offset, int length, OutputStream bos) throws IOException {
        writeFieldName(fieldName);
        try (SmileParser parser = SmileXContent.smileFactory().createParser(content, offset, length)) {
            parser.nextToken();
            ((JsonGenerator) generator).copyCurrentStructure(parser);
        }
    }
}
