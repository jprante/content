package org.xbib.content.yaml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import org.xbib.content.XContent;
import org.xbib.content.XContentParser;
import org.xbib.content.core.AbstractXContentParser;
import org.xbib.content.core.MapFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class YamlXContentParser extends AbstractXContentParser {

    private final YAMLParser parser;

    public YamlXContentParser(YAMLParser parser) {
        this.parser = parser;
    }

    @Override
    public XContent content() {
        return YamlXContent.yamlContent();
    }


    @Override
    public XContentParser.Token nextToken() throws IOException {
        return convertToken(parser.nextToken());
    }

    @Override
    public void skipChildren() throws IOException {
        parser.skipChildren();
    }

    @Override
    public XContentParser.Token currentToken() {
        return convertToken(parser.getCurrentToken());
    }

    @Override
    public XContentParser.NumberType numberType() throws IOException {
        return convertNumberType(parser.getNumberType());
    }

    @Override
    public boolean estimatedNumberType() {
        return true;
    }

    @Override
    public String currentName() throws IOException {
        return parser.getCurrentName();
    }

    @Override
    protected MapFactory getMapFactory() {
        return HashMap::new;
    }

    @Override
    protected MapFactory getOrderedMapFactory() {
        return LinkedHashMap::new;
    }

    @Override
    protected boolean doBooleanValue() throws IOException {
        return parser.getBooleanValue();
    }

    @Override
    public String text() throws IOException {
        return parser.getText();
    }

    @Override
    public boolean hasTextCharacters() {
        return parser.hasTextCharacters();
    }

    @Override
    public char[] textCharacters() throws IOException {
        return parser.getTextCharacters();
    }

    @Override
    public int textLength() throws IOException {
        return parser.getTextLength();
    }

    @Override
    public int textOffset() throws IOException {
        return parser.getTextOffset();
    }

    @Override
    public Number numberValue() throws IOException {
        return parser.getNumberValue();
    }

    @Override
    public BigInteger bigIntegerValue() throws IOException {
        return parser.getBigIntegerValue();
    }

    @Override
    public BigDecimal bigDecimalValue() throws IOException {
        return parser.getDecimalValue();
    }

    @Override
    public short doShortValue() throws IOException {
        return parser.getShortValue();
    }

    @Override
    public int doIntValue() throws IOException {
        return parser.getIntValue();
    }

    @Override
    public long doLongValue() throws IOException {
        return parser.getLongValue();
    }

    @Override
    public float doFloatValue() throws IOException {
        return parser.getFloatValue();
    }

    @Override
    public double doDoubleValue() throws IOException {
        return parser.getDoubleValue();
    }

    @Override
    public byte[] binaryValue() throws IOException {
        return parser.getBinaryValue();
    }

    @Override
    public void close() throws IOException {
        parser.close();
    }

    private XContentParser.NumberType convertNumberType(JsonParser.NumberType numberType) {
        switch (numberType) {
            case INT:
                return XContentParser.NumberType.INT;
            case LONG:
                return XContentParser.NumberType.LONG;
            case FLOAT:
                return XContentParser.NumberType.FLOAT;
            case DOUBLE:
                return XContentParser.NumberType.DOUBLE;
            case BIG_DECIMAL:
                return XContentParser.NumberType.BIG_DECIMAL;
            case BIG_INTEGER:
                return XContentParser.NumberType.BIG_INTEGER;
            default:
                break;
        }
        throw new IllegalStateException("No matching token for number_type [" + numberType + "]");
    }

    private XContentParser.Token convertToken(JsonToken token) {
        if (token == null) {
            return null;
        }
        switch (token) {
            case FIELD_NAME:
                return XContentParser.Token.FIELD_NAME;
            case VALUE_FALSE:
            case VALUE_TRUE:
                return XContentParser.Token.VALUE_BOOLEAN;
            case VALUE_STRING:
                return XContentParser.Token.VALUE_STRING;
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
                return XContentParser.Token.VALUE_NUMBER;
            case VALUE_NULL:
                return XContentParser.Token.VALUE_NULL;
            case START_OBJECT:
                return XContentParser.Token.START_OBJECT;
            case END_OBJECT:
                return XContentParser.Token.END_OBJECT;
            case START_ARRAY:
                return XContentParser.Token.START_ARRAY;
            case END_ARRAY:
                return XContentParser.Token.END_ARRAY;
            case VALUE_EMBEDDED_OBJECT:
                return XContentParser.Token.VALUE_EMBEDDED_OBJECT;
            default:
                break;
        }
        throw new IllegalStateException("No matching token for json_token [" + token + "]");
    }
}
