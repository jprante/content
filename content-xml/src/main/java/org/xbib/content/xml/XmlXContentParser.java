package org.xbib.content.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.xbib.content.core.AbstractXContentParser;
import org.xbib.content.XContent;
import org.xbib.content.XContentParser;
import org.xbib.content.core.MapFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 */
public class XmlXContentParser extends AbstractXContentParser {

    protected final JsonParser parser;

    public XmlXContentParser(JsonParser parser) {
        this.parser = parser;
    }

    @Override
    public XContent content() {
        return XmlXContent.xmlXContent();
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

    private NumberType convertNumberType(JsonParser.NumberType numberType) {
        switch (numberType) {
            case INT:
                return NumberType.INT;
            case LONG:
                return NumberType.LONG;
            case FLOAT:
                return NumberType.FLOAT;
            case DOUBLE:
                return NumberType.DOUBLE;
            case BIG_INTEGER:
                return NumberType.BIG_INTEGER;
            case BIG_DECIMAL:
                return NumberType.BIG_DECIMAL;
            default:
                break;
        }
        throw new IllegalStateException("No matching token for number_type [" + numberType + "]");
    }

    private Token convertToken(JsonToken token) {
        if (token == null) {
            return null;
        }
        switch (token) {
            case FIELD_NAME:
                return Token.FIELD_NAME;
            case VALUE_FALSE:
            case VALUE_TRUE:
                return Token.VALUE_BOOLEAN;
            case VALUE_STRING:
                return Token.VALUE_STRING;
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
                return Token.VALUE_NUMBER;
            case VALUE_NULL:
                return Token.VALUE_NULL;
            case START_OBJECT:
                return Token.START_OBJECT;
            case END_OBJECT:
                return Token.END_OBJECT;
            case START_ARRAY:
                return Token.START_ARRAY;
            case END_ARRAY:
                return Token.END_ARRAY;
            case VALUE_EMBEDDED_OBJECT:
                return Token.VALUE_EMBEDDED_OBJECT;
            default:
                break;
        }
        throw new IllegalStateException("No matching token for json_token [" + token + "]");
    }
}
