package org.xbib.content.resource.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.xbib.content.resource.url.UrlBuilder.forHost;
import static org.xbib.content.resource.url.UrlBuilder.fromUrl;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.CharacterCodingException;

/**
 *
 */
public final class UrlBuilderTest {

    private static void assertUrlEquals(String expected, String actual)
            throws URISyntaxException, MalformedURLException {
        assertEquals(expected, actual);
        assertEquals(expected, new URI(actual).toString());
        assertEquals(expected, new URL(actual).toString());
    }

    @Test
    public void testNoUrlParts() throws Exception {
        assertUrlEquals("http://foo.com", forHost("http", "foo.com").toUrlString());
    }

    @Test
    public void testWithPort() throws Exception {
        assertUrlEquals("http://foo.com:33", forHost("http", "foo.com", 33).toUrlString());
    }

    @Test
    public void testSimplePath() throws Exception {
        UrlBuilder ub = forHost("http", "foo.com");
        ub.pathSegment("seg1").pathSegment("seg2");
        assertUrlEquals("http://foo.com/seg1/seg2", ub.toUrlString());
    }

    @Test
    public void testPathWithReserved() throws Exception {
        // RFC 1738 S3.3
        UrlBuilder ub = forHost("http", "foo.com");
        ub.pathSegment("seg/;?ment").pathSegment("seg=&2");
        assertUrlEquals("http://foo.com/seg%2F%3B%3Fment/seg=&2", ub.toUrlString());
    }

    @Test
    public void testPathSegments() throws Exception {
        UrlBuilder ub = forHost("http", "foo.com");
        ub.pathSegments("seg1", "seg2", "seg3");
        assertUrlEquals("http://foo.com/seg1/seg2/seg3", ub.toUrlString());
    }

    @Test
    public void testMatrixWithoutPathHasLeadingSlash() throws Exception {
        UrlBuilder ub = forHost("http", "foo.com");
        ub.matrixParam("foo", "bar");
        assertUrlEquals("http://foo.com/;foo=bar", ub.toUrlString());
    }

    @Test
    public void testMatrixWithReserved() throws Exception {
        UrlBuilder ub = forHost("http", "foo.com")
                .pathSegment("foo")
                .matrixParam("foo", "bar")
                .matrixParam("res;=?#/erved", "value")
                .pathSegment("baz");
        assertUrlEquals("http://foo.com/foo;foo=bar;res%3B%3D%3F%23%2Ferved=value/baz", ub.toUrlString());
    }

    @Test
    public void testUrlEncodedPathSegmentUtf8() throws Exception {
        // 1 UTF-16 char
        UrlBuilder ub = forHost("http", "foo.com");
        ub.pathSegment("snowman").pathSegment("\u2603");
        assertUrlEquals("http://foo.com/snowman/%E2%98%83", ub.toUrlString());
    }

    @Test
    public void testUrlEncodedPathSegmentUtf8SurrogatePair() throws Exception {
        UrlBuilder ub = forHost("http", "foo.com");
        // musical G clef: 1d11e, has to be represented in surrogate pair form
        ub.pathSegment("clef").pathSegment("\ud834\udd1e");
        assertUrlEquals("http://foo.com/clef/%F0%9D%84%9E", ub.toUrlString());
    }

    @Test
    public void testQueryParamNoPath() throws Exception {
        UrlBuilder ub = forHost("http", "foo.com");
        ub.queryParam("foo", "bar");
        String s = ub.toUrlString();
        assertUrlEquals("http://foo.com?foo=bar", s);
    }

    @Test
    public void testQueryParamsDuplicated() throws Exception {
        UrlBuilder ub = forHost("http", "foo.com");
        ub.queryParam("foo", "bar");
        ub.queryParam("foo", "bar2");
        ub.queryParam("baz", "quux");
        ub.queryParam("baz", "quux2");
        assertUrlEquals("http://foo.com?foo=bar&foo=bar2&baz=quux&baz=quux2", ub.toUrlString());
    }

    @Test
    public void testEncodeQueryParams() throws Exception {
        UrlBuilder ub = forHost("http", "foo.com");
        ub.queryParam("foo", "bar&=#baz");
        ub.queryParam("foo", "bar?/2");
        assertUrlEquals("http://foo.com?foo=bar%26%3D%23baz&foo=bar?/2", ub.toUrlString());
    }

    @Test
    public void testEncodeQueryParamWithSpaceAndPlus() throws Exception {
        UrlBuilder ub = forHost("http", "foo.com");
        ub.queryParam("foo", "spa ce");
        ub.queryParam("fo+o", "plus+");
        assertUrlEquals("http://foo.com?foo=spa%20ce&fo%2Bo=plus%2B", ub.toUrlString());
    }

    @Test
    public void testPlusInVariousParts() throws Exception {
        UrlBuilder ub = forHost("http", "foo.com");

        ub.pathSegment("has+plus")
                .matrixParam("plusMtx", "pl+us")
                .queryParam("plusQp", "pl+us")
                .fragment("plus+frag");

        assertUrlEquals("http://foo.com/has+plus;plusMtx=pl+us?plusQp=pl%2Bus#plus+frag", ub.toUrlString());
    }

    @Test
    public void testFragment() throws Exception {
        UrlBuilder ub = forHost("http", "foo.com");
        ub.queryParam("foo", "bar");
        ub.fragment("#frag/?");
        assertUrlEquals("http://foo.com?foo=bar#%23frag/?", ub.toUrlString());
    }

    @Test
    public void testAllParts() throws Exception {
        UrlBuilder ub = forHost("https", "foo.bar.com", 3333);
        ub.pathSegment("foo");
        ub.pathSegment("bar");
        ub.matrixParam("mtx1", "val1");
        ub.matrixParam("mtx2", "val2");
        ub.queryParam("q1", "v1");
        ub.queryParam("q2", "v2");
        ub.fragment("zomg it's a fragment");

        assertEquals("https://foo.bar.com:3333/foo/bar;mtx1=val1;mtx2=val2?q1=v1&q2=v2#zomg%20it's%20a%20fragment",
                ub.toUrlString());
    }

    @Test
    public void testIPv4Literal() throws Exception {
        UrlBuilder ub = forHost("http", "127.0.0.1");
        assertUrlEquals("http://127.0.0.1", ub.toUrlString());
    }

    @Test
    public void testBadIPv4LiteralDoesntChoke() throws Exception {
        UrlBuilder ub = forHost("http", "300.100.50.1");
        assertUrlEquals("http://300.100.50.1", ub.toUrlString());
    }

    @Test
    public void testIPv6LiteralLocalhost() throws Exception {
        UrlBuilder ub = forHost("http", "[::1]");
        assertUrlEquals("http://[::1]", ub.toUrlString());
    }

    @Test
    public void testIPv6Literal() throws Exception {
        UrlBuilder ub = forHost("http", "[2001:db8:85a3::8a2e:370:7334]");
        assertUrlEquals("http://[2001:db8:85a3::8a2e:370:7334]", ub.toUrlString());
    }

    @Test
    public void testEncodedRegNameSingleByte() throws Exception {
        UrlBuilder ub = forHost("http", "host?name;");
        assertUrlEquals("http://host%3Fname;", ub.toUrlString());
    }

    @Test
    public void testEncodedRegNameMultiByte() throws Exception {
        UrlBuilder ub = forHost("http", "snow\u2603man");
        assertUrlEquals("http://snow%E2%98%83man", ub.toUrlString());
    }

    @Test
    public void testForceTrailingSlash() throws Exception {
        UrlBuilder ub = forHost("https", "foo.com").forceTrailingSlash().pathSegments("a", "b", "c");

        assertUrlEquals("https://foo.com/a/b/c/", ub.toUrlString());
    }

    @Test
    public void testForceTrailingSlashWithQueryParams() throws Exception {
        UrlBuilder ub =
                forHost("https", "foo.com").forceTrailingSlash().pathSegments("a", "b", "c").queryParam("foo", "bar");

        assertUrlEquals("https://foo.com/a/b/c/?foo=bar", ub.toUrlString());
    }

    @Test
    public void testForceTrailingSlashNoPathSegmentsWithMatrixParams() throws Exception {
        UrlBuilder ub = forHost("https", "foo.com").forceTrailingSlash().matrixParam("m1", "v1");

        assertUrlEquals("https://foo.com/;m1=v1/", ub.toUrlString());
    }

    @Test
    public void testIntermingledMatrixParamsAndPathSegments() throws Exception {

        UrlBuilder ub = forHost("http", "foo.com")
                .pathSegments("seg1", "seg2")
                .matrixParam("m1", "v1")
                .pathSegment("seg3")
                .matrixParam("m2", "v2");

        assertUrlEquals("http://foo.com/seg1/seg2;m1=v1/seg3;m2=v2", ub.toUrlString());
    }

    @Test
    public void testFromUrlWithEverything() throws Exception {
        String orig =
          "https://foo.bar.com:33/foo/ba%20r;mtx1=val1;mtx2=val%202/seg%203;m2=v2?q1=v1&q2=v%202#zomg%20it's%20a%20fragm";
        assertUrlBuilderRoundtrip(orig);
    }

    @Test
    public void testFromUrlWithEmptyPath() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com");
    }

    @Test
    public void testFromUrlWithEmptyPathAndSlash() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/", "http://foo.com");
    }

    @Test
    public void testFromUrlWithPort() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com:1234");
    }

    @Test
    public void testFromUrlWithEmptyPathSegent() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/foo//", "http://foo.com/foo");
    }

    @Test
    public void testFromUrlWithEncodedHost() throws Exception {
        assertUrlBuilderRoundtrip("http://f%20oo.com/bar");
    }

    @Test
    public void testFromUrlWithEncodedPathSegment() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/foo/b%20ar");
    }

    @Test
    public void testFromUrlWithEncodedMatrixParam() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/foo;m1=v1;m%202=v%202");
    }

    @Test
    public void testFromUrlWithEncodedQueryParam() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/foo?q%201=v%202&q2=v2");
    }

    @Test
    public void testFromUrlWithEncodedQueryParamDelimiter() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/foo?q1=%3Dv1&%26q2=v2");
    }

    @Test
    public void testFromUrlWithEncodedFragment() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/foo#b%20ar");
    }

    @Test
    public void testFromUrlWithMalformedMatrixPair() throws Exception {
        try {
            fromUrl(new URL("http://foo.com/foo;m1=v1=v2"));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Malformed matrix param: <m1=v1=v2>", e.getMessage());
        }
    }

    @Test
    public void testFromUrlWithEmptyPathSegmentWithMatrixParams() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/foo/;m1=v1");
    }

    @Test
    public void testFromUrlWithEmptyPathWithMatrixParams() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/;m1=v1");
    }

    @Test
    public void testFromUrlWithEmptyPathWithMultipleMatrixParams() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/;m1=v1;m2=v2");
    }

    @Test
    public void testFromUrlWithPathSegmentEndingWithSemicolon() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/foo;", "http://foo.com/foo");
    }

    @Test
    public void testPercentDecodeInvalidPair() throws MalformedURLException, CharacterCodingException {
        try {
            fromUrl(new URL("http://foo.com/fo%2o"));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid %-tuple <%2o>", e.getMessage());
        }
    }

    @Test
    public void testFromUrlMalformedQueryParamMultiValues() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/foo?q1=v1=v2");
    }

    @Test
    public void testFromUrlMalformedQueryParamNoValue() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/foo?q1=v1&q2");
    }

    @Test
    public void testFromUrlUnstructuredQueryWithEscapedChars() throws Exception {
        assertUrlBuilderRoundtrip("http://foo.com/foo?query==&%23");
    }

    @Test
    public void testCantUseQueryParamAfterQuery() {
        UrlBuilder ub = forHost("http", "foo.com").unstructuredQuery("q");

        try {
            ub.queryParam("foo", "bar");
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Cannot call queryParam() when this already has an unstructured query specified",
                    e.getMessage());
        }
    }

    @Test
    public void testCantUseQueryAfterQueryParam() {
        UrlBuilder ub = forHost("http", "foo.com").queryParam("foo", "bar");

        try {
            ub.unstructuredQuery("q");

            fail();
        } catch (IllegalStateException e) {
            assertEquals("Cannot call unstructuredQuery() when this already has queryParam pairs specified",
                    e.getMessage());
        }
    }

    @Test
    public void testUnstructuredQueryWithNoSpecialChars() throws Exception {
        assertUrlEquals("http://foo.com?q", forHost("http", "foo.com").unstructuredQuery("q").toUrlString());
    }

    @Test
    public void testUnstructuredQueryWithOkSpecialChars() throws Exception {
        assertUrlEquals("http://foo.com?q?/&=", forHost("http", "foo.com").unstructuredQuery("q?/&=").toUrlString());
    }

    @Test
    public void testUnstructuredQueryWithEscapedSpecialChars() throws Exception {
        assertUrlEquals("http://foo.com?q%23%2B", forHost("http", "foo.com").unstructuredQuery("q#+").toUrlString());
    }

    @Test
    public void testClearQueryRemovesQueryParam() throws Exception {
        UrlBuilder ub = forHost("http", "host")
                .queryParam("foo", "bar")
                .clearQuery();
        assertUrlEquals("http://host", ub.toUrlString());
    }

    @Test
    public void testClearQueryRemovesUnstructuredQuery() throws Exception {
        UrlBuilder ub = forHost("http", "host")
                .unstructuredQuery("foobar")
                .clearQuery();
        assertUrlEquals("http://host", ub.toUrlString());
    }

    @Test
    public void testClearQueryAfterQueryParamAllowsQuery() throws Exception {
        UrlBuilder ub = forHost("http", "host")
                .queryParam("foo", "bar")
                .clearQuery()
                .unstructuredQuery("foobar");
        assertUrlEquals("http://host?foobar", ub.toUrlString());
    }

    @Test
    public void testClearQueryAfterQueryAllowsQueryParam() throws Exception {
        UrlBuilder ub = forHost("http", "host")
                .unstructuredQuery("foobar")
                .clearQuery()
                .queryParam("foo", "bar");
        assertUrlEquals("http://host?foo=bar", ub.toUrlString());
    }

    private void assertUrlBuilderRoundtrip(String url)
            throws MalformedURLException, CharacterCodingException, URISyntaxException {
        assertUrlBuilderRoundtrip(url, url);
    }

    /**
     * @param origUrl  the url that will be used to create a URL
     * @param finalUrl the URL string it should end up as
     */
    private void assertUrlBuilderRoundtrip(String origUrl, String finalUrl)
            throws MalformedURLException, CharacterCodingException, URISyntaxException {
        assertUrlEquals(finalUrl, fromUrl(new URL(origUrl)).toUrlString());
    }
}
