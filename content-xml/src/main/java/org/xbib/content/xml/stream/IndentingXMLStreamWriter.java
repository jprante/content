package org.xbib.content.xml.stream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * A filter that indents an XML stream. To apply it, construct a filter that
 * contains another {@link XMLStreamWriter}, which you pass to the constructor.
 * Then call methods of the filter instead of the contained stream. For example:
 * <pre>
 * {@link XMLStreamWriter} stream = ...
 * stream = new {@link IndentingXMLStreamWriter}(stream);
 * stream.writeStartDocument();
 * ...
 * </pre>
 * The filter inserts characters to format the document as an outline, with
 * nested elements indented. Basically, it inserts a line break and whitespace
 * before:
 * <ul>
 * <li>each DTD, processing instruction or comment that's not preceded by data</li>
 * <li>each starting tag that's not preceded by data</li>
 * <li>each ending tag that's preceded by nested elements but not data</li>
 * </ul>
 * This works well with 'data-oriented' XML, wherein each element contains
 * either data or nested elements but not both. It can work badly with other
 * styles of XML. For example, the data in a 'mixed content' document are apt to
 * be polluted with indentation characters.
 * Indentation can be adjusted by setting the newLine and indent properties. But
 * set them to whitespace only, for best results. Non-whitespace is apt to cause
 * problems, for example when this class attempts to insert newLine before the
 * root element.
 */
public class IndentingXMLStreamWriter extends StreamWriterDelegate implements Indentation {

    private static final int WROTE_MARKUP = 1;
    private static final int WROTE_DATA = 2;
    /**
     * How deeply nested the current scope is. The root element is depth 1.
     */
    private int depth = 0; // document scope
    /**
     * stack[depth] indicates what's been written into the current scope.
     */
    private int[] stack = new int[]{0, 0, 0, 0}; // nothing written yet
    private String indent = DEFAULT_INDENT;
    private String newLine = NORMAL_END_OF_LINE;
    /**
     * newLine followed by copies of indent.
     */
    private char[] linePrefix = null;

    public IndentingXMLStreamWriter(XMLStreamWriter out) {
        super(out);
    }

    @Override
    public String getIndent() {
        return indent;
    }

    @Override
    public void setIndent(String indent) {
        if (!indent.equals(this.indent)) {
            this.indent = indent;
            linePrefix = null;
        }
    }

    public String getNewLine() {
        return newLine;
    }

    public void setNewLine(String newLine) {
        if (!newLine.equals(this.newLine)) {
            this.newLine = newLine;
            linePrefix = null;
        }
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        beforeMarkup();
        out.writeStartDocument();
        afterMarkup();
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
        beforeMarkup();
        out.writeStartDocument(version);
        afterMarkup();
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        beforeMarkup();
        out.writeStartDocument(encoding, version);
        afterMarkup();
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException {
        beforeMarkup();
        out.writeDTD(dtd);
        afterMarkup();
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        beforeMarkup();
        out.writeProcessingInstruction(target);
        afterMarkup();
    }

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        beforeMarkup();
        out.writeProcessingInstruction(target, data);
        afterMarkup();
    }

    @Override
    public void writeComment(String data) throws XMLStreamException {
        beforeMarkup();
        out.writeComment(data);
        afterMarkup();
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        beforeMarkup();
        out.writeEmptyElement(localName);
        afterMarkup();
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        beforeMarkup();
        out.writeEmptyElement(namespaceURI, localName);
        afterMarkup();
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException {
        beforeMarkup();
        out.writeEmptyElement(prefix, localName, namespaceURI);
        afterMarkup();
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        beforeStartElement();
        out.writeStartElement(localName);
        afterStartElement();
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        beforeStartElement();
        out.writeStartElement(namespaceURI, localName);
        afterStartElement();
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException {
        beforeStartElement();
        out.writeStartElement(prefix, localName, namespaceURI);
        afterStartElement();
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        out.writeCharacters(text);
        afterData();
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        out.writeCharacters(text, start, len);
        afterData();
    }

    @Override
    public void writeCData(String data) throws XMLStreamException {
        out.writeCData(data);
        afterData();
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException {
        out.writeEntityRef(name);
        afterData();
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        beforeEndElement();
        out.writeEndElement();
        afterEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        while (depth > 0) {
            writeEndElement(); // indented
        }
        out.writeEndDocument();
        afterEndDocument();
    }

    protected void beforeMarkup() throws XMLStreamException {
        int soFar = stack[depth];
        if ((soFar & WROTE_DATA) == 0 && (depth > 0 || soFar != 0)) {
            writeNewLine(depth);
            if (depth > 0 && getIndent().length() > 0) {
                afterMarkup(); // indentation was written
            }
        }
    }

    protected void afterMarkup() {
        stack[depth] |= WROTE_MARKUP;
    }

    protected void afterData() {
        stack[depth] |= WROTE_DATA;
    }

    protected void beforeStartElement() throws XMLStreamException {
        beforeMarkup();
        if (stack.length <= depth + 1) {
            // Allocate more space for the stack:
            int[] newStack = new int[stack.length * 2];
            System.arraycopy(stack, 0, newStack, 0, stack.length);
            stack = newStack;
        }
        stack[depth + 1] = 0; // nothing written yet
    }

    protected void afterStartElement() {
        afterMarkup();
        ++depth;
    }

    protected void beforeEndElement() {
        if (depth > 0 && stack[depth] == WROTE_MARKUP) { // but not data
            try {
                writeNewLine(depth - 1);
            } catch (XMLStreamException e) {
                // swallow
            }
        }
    }

    protected void afterEndElement() {
        if (depth > 0) {
            --depth;
        }
    }

    protected void afterEndDocument() {
        if (stack[depth = 0] == WROTE_MARKUP) { // but not data
            try {
                writeNewLine(0);
            } catch (XMLStreamException e) {
                // swallow
            }
        }
        stack[depth] = 0; // start fresh
    }

    /**
     * Write a line separator followed by indentation.
     * @param indentation indent level
     * @throws XMLStreamException if XML stream can not be processed
     */
    protected void writeNewLine(int indentation) throws XMLStreamException {
        final int newLineLength = getNewLine().length();
        final int prefixLength = newLineLength + (getIndent().length() * indentation);
        if (prefixLength > 0) {
            if (linePrefix == null) {
                linePrefix = (getNewLine() + getIndent()).toCharArray();
            }
            while (prefixLength > linePrefix.length) {
                // make linePrefix longer:
                char[] newPrefix = new char[newLineLength
                        + ((linePrefix.length - newLineLength) * 2)];
                System.arraycopy(linePrefix, 0, newPrefix, 0, linePrefix.length);
                System.arraycopy(linePrefix, newLineLength, newPrefix, linePrefix.length,
                        linePrefix.length - newLineLength);
                linePrefix = newPrefix;
            }
            out.writeCharacters(linePrefix, 0, prefixLength);
        }
    }
}
