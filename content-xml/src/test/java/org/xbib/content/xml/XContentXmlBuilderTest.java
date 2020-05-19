package org.xbib.content.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xbib.content.XContentBuilder;
import org.xbib.content.XContentHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 *
 */
public class XContentXmlBuilderTest {

    @Test
    public void testConstructorForServiceLoader() {
        XmlXContent xmlXContent = new XmlXContent();
        assertEquals("xml", xmlXContent.name());
    }

    @Test
    public void testEmpty() throws Exception {
        QName root = new QName("root");
        XContentBuilder builder = XmlXContent.contentBuilder(new XmlXParams(root));
        builder.startObject().field("Hello", "World").endObject();
        assertEquals("<root><Hello>World</Hello></root>", builder.string());
    }

    @Test
    public void testContextNamespace() throws Exception {
        QName root = new QName("root");
        XmlNamespaceContext context = XmlNamespaceContext.newInstance();
        XContentBuilder builder = XmlXContent.contentBuilder(new XmlXParams(root, context));
        builder.startObject().field("Hello", "World").endObject();
        assertEquals("<root><Hello>World</Hello></root>", builder.string());
    }

    @Test
    public void testXml() throws Exception {
        XContentBuilder builder = XmlXContent.contentBuilder();
        builder.startObject().field("Hello", "World").endObject();
        assertEquals("<root><Hello>World</Hello></root>", builder.string());
    }

    @Test
    public void testXmlParams() throws Exception {
        XmlXParams params = new XmlXParams();
        XContentBuilder builder = XmlXContent.contentBuilder(params);
        builder.startObject().field("Hello", "World").endObject();
        assertEquals("<root><Hello>World</Hello></root>", builder.string());
    }

    @Test
    public void testXmlObject() throws Exception {
        QName root = XmlXParams.getDefaultParams().getRoot();
        XmlXParams params = new XmlXParams(root);
        XContentBuilder builder = XmlXContent.contentBuilder(params);
        builder.startObject()
                .startObject("author")
                .field("creator", "John Doe")
                .field("role", "writer")
                .endObject()
                .startObject("author")
                .field("creator", "Joe Smith")
                .field("role", "illustrator")
                .endObject()
                .endObject();
        assertEquals("<root><author><creator>John Doe</creator><role>writer</role></author><author>"
                + "<creator>Joe Smith</creator><role>illustrator</role></author></root>",
                builder.string());
    }

    @Test
    public void testXmlAttributes() throws Exception {
        QName root = XmlXParams.getDefaultParams().getRoot();
        XmlXParams params = new XmlXParams(root);
        XContentBuilder builder = XmlXContent.contentBuilder(params);
        builder.startObject()
                .startObject("author")
                .field("@name", "John Doe")
                .field("@id", 1)
                .endObject()
                .endObject();
        assertEquals("<root><author><name>John Doe</name><id>1</id></author></root>", builder.string());
    }

    @Test
    public void testXmlArrayOfValues() throws Exception {
        QName root = XmlXParams.getDefaultParams().getRoot();
        XmlXParams params = new XmlXParams(root);
        XContentBuilder builder = XmlXContent.contentBuilder(params);
        builder.startObject()
                .array("author", "John Doe", "Joe Smith")
                .endObject();
        assertEquals("<root><author>John Doe</author><author>Joe Smith</author></root>", builder.string());
    }

    @Test
    public void testXmlArrayOfObjects() throws Exception {
        QName root = XmlXParams.getDefaultParams().getRoot();
        XmlXParams params = new XmlXParams(root);
        XContentBuilder builder = XmlXContent.contentBuilder(params);
        builder.startObject()
                .startArray("author")
                .startObject()
                .field("creator", "John Doe")
                .field("role", "writer")
                .endObject()
                .startObject()
                .field("creator", "Joe Smith")
                .field("role", "illustrator")
                .endObject()
                .endArray()
                .endObject();
        assertEquals("<root><author><creator>John Doe</creator><role>writer</role></author><author>"
                + "<creator>Joe Smith</creator><role>illustrator</role></author></root>",
                builder.string());
    }

    @Test
    public void testParseJson() throws Exception {
        XmlNamespaceContext context = XmlNamespaceContext.newInstance();
        context.addNamespace("bib", "info:srw/cql-context-set/1/bib-v1/");
        context.addNamespace("xbib", "http://xbib.org/");
        context.addNamespace("abc", "http://localhost/");
        context.addNamespace("lia", "http://xbib.org/namespaces/lia/");
        InputStream in = getClass().getResourceAsStream("dc.json");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        byte[] buf = out.toByteArray();
        Map<String, Object> map = XContentHelper.convertToMap(buf, false);
        assertEquals("{dc:description={xbib:creatorDescription=Otis Gospodnetić ; Erik Hatcher, "
                + "dcterms:extent=XXXIV, 421 S. : Ill.}, dc:language={xbib:languageISO6392b=eng, "
                + "xbib:languageISO6391=en}, dc:subject={xbib:rswk={xbib:subjectTopic=Lucene, "
                + "xbib:hasGND=4800725-0}}, dc:relation=[{xbib:relationName=Später u.d.T., "
                + "xbib:relationValue=McCandless, Michael: Lucene in action}, "
                + "{lia:lia={lia:access=[{lia:library={lia:name=DE-1010, lia:authority=ISIL}, "
                + "lia:name=DE-605, lia:authority=ISIL, lia:service=[inter-library-loan, copy], "
                + "lia:item={lia:preferredTransport=physical, lia:identifier=DE-1010 GE 01TYD1110, "
                + "lia:shelfmark=01TYD1110, lia:type=hard-cover, lia:preferredDelivery=physical, "
                + "lia:number=GE}}, {lia:library={lia:name=DE-6-282, lia:authority=ISIL}, lia:name=DE-605, "
                + "lia:authority=ISIL, lia:service=restricted, lia:item={lia:preferredTransport=physical, "
                + "lia:identifier=DE-6-282 00071001 WI 14 822, lia:shelfmark=WI 14 822, lia:type=hard-cover, "
                + "lia:preferredDelivery=physical, lia:number=00071001}}, {lia:library={lia:name=DE-361, "
                + "lia:authority=ISIL}, lia:name=DE-605, lia:authority=ISIL, lia:service=[inter-library-loan, copy], "
                + "lia:item={lia:preferredTransport=physical, lia:identifier=DE-361 00000010 HK520 G676, "
                + "lia:shelfmark=HK520 G676, lia:type=hard-cover, lia:preferredDelivery=physical, "
                + "lia:number=00000010}}, {lia:library={lia:name=DE-6, lia:authority=ISIL}, lia:name=DE-605, "
                + "lia:authority=ISIL, lia:service=[inter-library-loan, copy], "
                + "lia:item={lia:preferredTransport=physical, lia:identifier=DE-6 00000040 3W 622, "
                + "lia:shelfmark=3W 622, lia:type=hard-cover, lia:preferredDelivery=physical, lia:number=00000040}}, "
                + "{lia:library={lia:name=DE-1044, lia:authority=ISIL}, lia:name=DE-605, lia:authority=ISIL, "
                + "lia:service=[inter-library-loan, copy], lia:item={lia:preferredTransport=physical, "
                + "lia:identifier=DE-1044 00000001 11 = TYD6810, lia:shelfmark=11 = TYD6810, lia:type=hard-cover, "
                + "lia:preferredDelivery=physical, lia:number=00000001}}, {lia:library={lia:name=DE-1044, "
                + "lia:authority=ISIL}, lia:name=DE-605, lia:authority=ISIL, lia:service=[inter-library-loan, copy], "
                + "lia:item={lia:preferredTransport=physical, lia:identifier=DE-1044 00000001 11 = TYD6810+1, "
                + "lia:shelfmark=11 = TYD6810+1, lia:type=hard-cover, lia:preferredDelivery=physical, "
                + "lia:number=00000001}}, {lia:library={lia:name=DE-467, lia:authority=ISIL}, lia:name=DE-605, "
                + "lia:authority=ISIL, lia:service=[inter-library-loan, copy], "
                + "lia:item={lia:preferredTransport=physical, lia:identifier=DE-467 51 51TWYD3730, "
                + "lia:shelfmark=51TWYD3730, lia:type=hard-cover, lia:preferredDelivery=physical, "
                + "lia:number=51}}]}}], dc:creator=[{xbib:namePersonalID=HP02672128, "
                + "bib:namePersonal=Gospodnetić, Otis}, {xbib:namePersonalAlt=Hatcher, E., "
                + "xbib:namePersonalID=128668350, bib:namePersonal=Hatcher, Erik}], "
                + "xbib:updated=2012-05-17T17:02:24Z, dc:format={dcterms:format=print, dcterms:medium=paper}, "
                + "dc:type={bib:issuance=monographic, xbib:recordType=h}, dc:contributor=[{xbib:namePersonalRole=cre, "
                + "xbib:namePersonalID=HP02672128, bib:namePersonal=Gospodnetić, Otis}, "
                + "{xbib:namePersonalAlt=Hatcher, E., xbib:namePersonalRole=cre, xbib:namePersonalID=128668350, "
                + "bib:namePersonal=Hatcher, Erik}], dc:title={xbib:title=Lucene in action, "
                + "xbib:titleSub=[a guide to the Java search engine]}, dc:date={dcterms:issued=2005}, "
                + "dc:identifier={xbib:identifierAuthorityEKI=HBZHT014262244, xbib:sysID=013391972, "
                + "xbib:identifierAuthorityISIL=[DE-1010, DE-6-282, DE-361, DE-6, DE-1044, DE-467], "
                + "xbib:identifierAuthorityOriginISBN=1-932394-28-1, xbib:identifierAuthorityMAB=HT014262244, "
                + "xbib:identifierAuthorityISBN=1932394281, xbib:identifierAuthorityEAN=9781932394283}, "
                + "dc:publisher={xbib:publisherPlace=Greenwich, Conn., xbib:publisherName=Manning}}",
                map.toString());
    }

    @Test
    public void testInvalidWhiteSpaceCharacter() throws Exception {
        QName root = new QName("root");
        XContentBuilder builder = XmlXContent.contentBuilder(new XmlXParams(root));
        builder.startObject().field("Hello", "World\u001b").endObject();
        assertEquals("<root><Hello>World</Hello></root>", builder.string());
    }

    @Test
    public void testSuppressEmptyNamespace() throws Exception {
        XmlNamespaceContext context = XmlNamespaceContext.newInstance();
        context.addNamespace("", "");
        QName root = new QName("root");
        XContentBuilder builder = XmlXContent.contentBuilder(new XmlXParams(root, context));
        builder.startObject().field("Hello", "World").endObject();
        assertEquals("<root><Hello>World</Hello></root>", builder.string());
    }

    private void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }
}
