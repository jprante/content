package org.xbib.content;

/**
 *
 */
public class XContentBuilderString {

    private final XContentString string;

    public XContentBuilderString(String value) {
        string = new XContentString(value);
    }

    public XContentString string() {
        return string;
    }

}
