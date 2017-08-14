package org.xbib.content.settings;

import org.xbib.content.XContent;
import org.xbib.content.XContentGenerator;
import org.xbib.content.XContentParser;
import org.xbib.content.io.BytesReference;
import org.xbib.content.io.BytesStreamOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Settings loader that loads (parses) the settings in a XContent format by flattening them
 * into a map.
 */
public abstract class AbstractSettingsLoader implements SettingsLoader {

    public abstract XContent content();

    @Override
    public Map<String, String> load(String source) throws IOException {
        try (XContentParser parser = content().createParser(source)) {
            return load(parser);
        }
    }

    public Map<String, String> load(BytesReference bytesReference) throws IOException {
        try (XContentParser parser = content().createParser(bytesReference)) {
            return load(parser);
        }
    }

    public String flatMapAsString(BytesReference bytesReference) throws IOException {
        try (XContentParser parser = content().createParser(bytesReference);
            BytesStreamOutput bytesStreamOutput = new BytesStreamOutput();
            XContentGenerator generator = content().createGenerator(bytesStreamOutput)) {
            generator.writeStartObject();
            for (Map.Entry<String, String> entry : load(parser).entrySet()) {
                generator.writeFieldName(entry.getKey());
                String value = entry.getValue();
                if (value == null) {
                    generator.writeNull();
                } else {
                    generator.writeString(value);
                }
            }
            generator.writeEndObject();
            generator.flush();
            return bytesStreamOutput.bytes().toUtf8();
        }
    }

    public Map<String, String> load(XContentParser xContentParser) throws IOException {
        StringBuilder sb = new StringBuilder();
        Map<String, String> map = new HashMap<>();
        List<String> path = new ArrayList<>();
        XContentParser.Token token = xContentParser.nextToken();
        if (token == null) {
            return map;
        }
        parseObject(map, sb, path, xContentParser, null);
        return map;
    }

    private void parseObject(Map<String, String> settings, StringBuilder sb, List<String> path,
                             XContentParser parser, String objFieldName) throws IOException {
        if (objFieldName != null) {
            path.add(objFieldName);
        }

        String currentFieldName = null;
        XContentParser.Token token;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.START_OBJECT) {
                parseObject(settings, sb, path, parser, currentFieldName);
            } else if (token == XContentParser.Token.START_ARRAY) {
                parseArray(settings, sb, path, parser, currentFieldName);
            } else if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else {
                parseValue(settings, sb, path, parser, currentFieldName);

            }
        }
        if (objFieldName != null) {
            path.remove(path.size() - 1);
        }
    }

    private void parseArray(Map<String, String> settings, StringBuilder sb, List<String> path,
                            XContentParser parser, String name) throws IOException {
        XContentParser.Token token;
        int counter = 0;
        String fieldName = name;
        while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
            if (token == XContentParser.Token.START_OBJECT) {
                parseObject(settings, sb, path, parser, fieldName + '.' + (counter++));
            } else if (token == XContentParser.Token.START_ARRAY) {
                parseArray(settings, sb, path, parser, fieldName + '.' + (counter++));
            } else if (token == XContentParser.Token.FIELD_NAME) {
                fieldName = parser.currentName();
            } else {
                parseValue(settings, sb, path, parser, fieldName + '.' + (counter++));
            }
        }
    }

    private void parseValue(Map<String, String> settings, StringBuilder sb, List<String> path,
                            XContentParser parser, String fieldName) throws IOException {
        sb.setLength(0);
        for (String s : path) {
            sb.append(s).append('.');
        }
        sb.append(fieldName);
        settings.put(sb.toString(), parser.text());
    }
}
