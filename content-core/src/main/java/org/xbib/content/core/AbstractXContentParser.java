package org.xbib.content.core;

import org.xbib.content.XContentParser;
import org.xbib.datastructures.tiny.TinyMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class AbstractXContentParser implements XContentParser {

    private static final MapFactory SIMPLE_MAP_FACTORY = HashMap::new;

    private static final MapFactory TINY_MAP_FACTORY = TinyMap::builder;

    private boolean losslessDecimals;

    private boolean base16Checks;

    private static Map<String, Object> readMap(XContentParser parser) throws IOException {
        return readMap(parser, SIMPLE_MAP_FACTORY);
    }

    private static Map<String, Object> readOrderedMap(XContentParser parser) throws IOException {
        return readMap(parser, TINY_MAP_FACTORY);
    }

    private static Map<String, Object> readMap(XContentParser parser, MapFactory mapFactory) throws IOException {
        Map<String, Object> map = mapFactory.newMap();
        XContentParser.Token t = parser.currentToken();
        if (t == null) {
            t = parser.nextToken();
        }
        if (t == XContentParser.Token.START_OBJECT) {
            t = parser.nextToken();
        }
        for (; t == XContentParser.Token.FIELD_NAME; t = parser.nextToken()) {
            String fieldName = parser.currentName();
            t = parser.nextToken();
            Object value = readValue(parser, mapFactory, t);
            map.put(fieldName, value);
        }
        return map;
    }

    private static List<Object> readList(XContentParser parser, MapFactory mapFactory) throws IOException {
        ArrayList<Object> list = new ArrayList<>();
        Token t;
        while ((t = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
            list.add(readValue(parser, mapFactory, t));
        }
        return list;
    }

    private static Object readValue(XContentParser parser, MapFactory mapFactory, XContentParser.Token t) throws IOException {
        if (t == XContentParser.Token.VALUE_NULL) {
            return null;
        } else if (t == XContentParser.Token.VALUE_STRING) {
            if (parser.isBase16Checks()) {
                return XContentHelper.parseBase16(parser.text());
            }
            return parser.text();
        } else if (t == XContentParser.Token.VALUE_NUMBER) {
            XContentParser.NumberType numberType = parser.numberType();
            if (numberType == XContentParser.NumberType.INT) {
                return parser.isLosslessDecimals() ? parser.bigIntegerValue() : parser.intValue();
            } else if (numberType == XContentParser.NumberType.LONG) {
                return parser.isLosslessDecimals() ? parser.bigIntegerValue() : parser.longValue();
            } else if (numberType == XContentParser.NumberType.FLOAT) {
                return parser.isLosslessDecimals() ? parser.bigDecimalValue() : parser.floatValue();
            } else if (numberType == XContentParser.NumberType.DOUBLE) {
                return parser.isLosslessDecimals() ? parser.bigDecimalValue() : parser.doubleValue();
            } else if (numberType == NumberType.BIG_INTEGER) {
                return parser.bigIntegerValue();
            } else if (numberType == NumberType.BIG_DECIMAL) {
                return parser.bigDecimalValue();
            }
        } else if (t == XContentParser.Token.VALUE_BOOLEAN) {
            return parser.booleanValue();
        } else if (t == XContentParser.Token.START_OBJECT) {
            return readMap(parser, mapFactory);
        } else if (t == XContentParser.Token.START_ARRAY) {
            return readList(parser, mapFactory);
        } else if (t == XContentParser.Token.VALUE_EMBEDDED_OBJECT) {
            return parser.binaryValue();
        }
        return null;
    }

    @Override
    public boolean isBooleanValue() throws IOException {
        switch (currentToken()) {
            case VALUE_BOOLEAN:
                return true;
            case VALUE_NUMBER:
                NumberType numberType = numberType();
                return numberType == NumberType.LONG || numberType == NumberType.INT;
            case VALUE_STRING:
                return isBoolean(textCharacters(), textOffset(), textLength());
            default:
                return false;
        }
    }

    @Override
    public boolean booleanValue() throws IOException {
        Token token = currentToken();
        if (token == Token.VALUE_NUMBER) {
            return intValue() != 0;
        } else if (token == Token.VALUE_STRING) {
            String s = new String(textCharacters(), textOffset(), textLength());
            return Boolean.parseBoolean(s);
        }
        return doBooleanValue();
    }

    protected abstract boolean doBooleanValue() throws IOException;

    @Override
    public short shortValue() throws IOException {
        Token token = currentToken();
        if (token == Token.VALUE_STRING) {
            return Short.parseShort(text());
        }
        return doShortValue();
    }

    protected abstract short doShortValue() throws IOException;

    @Override
    public int intValue() throws IOException {
        Token token = currentToken();
        if (token == Token.VALUE_STRING) {
            return Integer.parseInt(text());
        }
        return doIntValue();
    }

    protected abstract int doIntValue() throws IOException;

    @Override
    public long longValue() throws IOException {
        Token token = currentToken();
        if (token == Token.VALUE_STRING) {
            return Long.parseLong(text());
        }
        return doLongValue();
    }

    protected abstract long doLongValue() throws IOException;

    @Override
    public float floatValue() throws IOException {
        Token token = currentToken();
        if (token == Token.VALUE_STRING) {
            return Float.parseFloat(text());
        }
        return doFloatValue();
    }

    protected abstract float doFloatValue() throws IOException;

    @Override
    public double doubleValue() throws IOException {
        Token token = currentToken();
        if (token == Token.VALUE_STRING) {
            return Double.parseDouble(text());
        }
        return doDoubleValue();
    }

    protected abstract double doDoubleValue() throws IOException;

    @Override
    public XContentParser losslessDecimals(boolean losslessDecimals) {
        this.losslessDecimals = losslessDecimals;
        return this;
    }

    @Override
    public boolean isLosslessDecimals() {
        return losslessDecimals;
    }

    @Override
    public XContentParser enableBase16Checks(boolean base16Checks) {
        this.base16Checks = base16Checks;
        return this;
    }

    @Override
    public boolean isBase16Checks() {
        return base16Checks;
    }

    @Override
    public String textOrNull() throws IOException {
        if (currentToken() == Token.VALUE_NULL) {
            return null;
        }
        return text();
    }

    @Override
    public Map<String, Object> map() throws IOException {
        return readMap(this);
    }

    @Override
    public Map<String, Object> mapOrdered() throws IOException {
        return readOrderedMap(this);
    }

    @Override
    public Map<String, Object> mapAndClose() throws IOException {
        try {
            return map();
        } finally {
            close();
        }
    }

    @Override
    public Map<String, Object> mapOrderedAndClose() throws IOException {
        try {
            return mapOrdered();
        } finally {
            close();
        }
    }

    @FunctionalInterface
    interface MapFactory {
        Map<String, Object> newMap();
    }

    /**
     * Returns true if the a sequence of chars is one of "true","false","on","off","yes","no","0","1".
     *
     * @param text   sequence to check
     * @param offset offset to start
     * @param length length to check
     * @return true if it is a boolean
     */
    private static boolean isBoolean(char[] text, int offset, int length) {
        if (text == null || length == 0) {
            return false;
        }
        if (length == 1) {
            return text[offset] == '0' || text[offset] == '1';
        }
        if (length == 2) {
            return (text[offset] == 'n' && text[offset + 1] == 'o') || (text[offset] == 'o' && text[offset + 1] == 'n');
        }
        if (length == 3) {
            return (text[offset] == 'o' && text[offset + 1] == 'f' && text[offset + 2] == 'f') ||
                    (text[offset] == 'y' && text[offset + 1] == 'e' && text[offset + 2] == 's');
        }
        if (length == 4) {
            return text[offset] == 't' && text[offset + 1] == 'r' && text[offset + 2] == 'u' && text[offset + 3] == 'e';
        }
        return length == 5 && (text[offset] == 'f' && text[offset + 1] == 'a' && text[offset + 2] == 'l'
                && text[offset + 3] == 's' && text[offset + 4] == 'e');
    }
}
