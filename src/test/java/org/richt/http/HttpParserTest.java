package org.richt.http;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpParserTest {

    private HttpParser httpParser;

    @BeforeAll
    public void beforeClass() {
        httpParser = new HttpParser();
    }

    /**
     * Tests parsing/exception handling on a perfectly normal default request
     */
    @Test
    void parseValidHttpRequestTest1() {
        HttpRequest validRequest = null;
        try {
            validRequest = httpParser.parseHttpRequest(generateValidGETTestCase());
        } catch (HttpParsingException e) {
            fail("Bad parsing exception");
        }
        assertNotNull(validRequest);
        assertEquals(HttpMethod.GET, validRequest.getMethod());
        assertEquals("/", validRequest.getRequestTarget());
        assertEquals("HTTP/1.1", validRequest.getOriginalHttpVersion());
        assertEquals(HttpVersion.HTTP_1_1, validRequest.getBestCompatibleHttpVersion());
    }

    /**
     * Tests parsing/exception handling on invalid method name
     *
     * The example is invalid by case-sensitivity
     */
    @Test
    void parseBadHttpRequestTest1() {
        try {
            httpParser.parseHttpRequest(generateInvalidGETTestCase1());
            fail("Expected HttpParsingException to be thrown");
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED, e.getErrorCode());
        };
    }

    /**
     * Tests http parsing for extra long method name
     *
     * Parser will intelligently detect a request as
     * malicious if the method name is longer than any valid,
     * and thereby stop parsing the request
     */
    @Test
    void parseBadHttpRequestTest2() {
        try {
            httpParser.parseHttpRequest(generateInvalidGETTestCase2());
            fail("Expected HttpParsingException to be thrown");
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED, e.getErrorCode());
        };
    }

    /**
     * Tests http parsing/exception handling for invalid number of
     * line items
     */
    @Test
    void parseBadHttpRequestTest3() {
        try {
            httpParser.parseHttpRequest(generateInvalidGETTestCase3());
            fail("Expected HttpParsingException to be thrown");
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        };
    }

    /**
     * Tests parsing/exception handling for empty method heading
     */
    @Test
    void parseHttpEmptyMethodTest() {
        try {
            httpParser.parseHttpRequest(generateEmptyMethodCase());
            fail("Expected HttpParsingException to be thrown");
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        }
    }

    /**
     * Tests parsing/exception handling for missing line feed
     */
    @Test
    void parseHttpNoLFTest() {
        try {
            httpParser.parseHttpRequest(generateNoLFCase());
            fail("Expected HttpParsingException to be thrown");
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        }
    }

    /**
     * Tests parsing/exception handling on invalid version
     */
    @Test
    void parseBadHttpVersion() {
        try {
            httpParser.parseHttpRequest(generateBadVersionRequest());
            fail("Expected HttpParsingException to be thrown");
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        }
    }

    /**
     * Tests parsing/exception handling on unsupported version
     */
    @Test
    void parseUnsupportedHttpVersion() {
        try {
            httpParser.parseHttpRequest(generateUnsupportedVersionRequest());
            fail("Expected HttpParsingException to be thrown");
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.SERVER_ERROR_505_VERSION_NOT_SUPPORTED, e.getErrorCode());
        }
    }

    /**
     * Tests parsing/exception handling on same major, different minor version
     */
    @Test
    void parseHigherHttpVersion() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(generateHigherVersionRequest());
            assertNotNull(request);
            assertEquals(HttpVersion.HTTP_1_1, request.getBestCompatibleHttpVersion());
            assertEquals("HTTP/1.2", request.getOriginalHttpVersion());
        } catch (HttpParsingException e) {
            fail("Unexpected Exception");
        }
    }

    private InputStream generateValidGETTestCase() {
        String rawTestData = "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Chromium\";v=\"148\", \"Google Chrome\";v=\"148\", \"Not/A)Brand\";v=\"99\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en-US,en;q=0.9,pl;q=0.8\r\n" +
                "\r\n";
        return new ByteArrayInputStream(rawTestData.getBytes(StandardCharsets.US_ASCII));
    }
    private InputStream generateInvalidGETTestCase1() {
        String rawTestData = "GeT / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en-US,en;q=0.9,pl;q=0.8\r\n" +
                "\r\n";
        return new ByteArrayInputStream(rawTestData.getBytes(StandardCharsets.US_ASCII));
    }
    private InputStream generateInvalidGETTestCase2() {
        String rawTestData = "GETTTTTTTTTTTTTTTTTTTTTTTT / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en-US,en;q=0.9,pl;q=0.8\r\n" +
                "\r\n";
        return new ByteArrayInputStream(rawTestData.getBytes(StandardCharsets.US_ASCII));
    }
    private InputStream generateInvalidGETTestCase3() {
        String rawTestData = "GET / AAAAAA HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en-US,en;q=0.9,pl;q=0.8\r\n" +
                "\r\n";
        return new ByteArrayInputStream(rawTestData.getBytes(StandardCharsets.US_ASCII));
    }
    private InputStream generateEmptyMethodCase() {
        String rawTestData = "\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en-US,en;q=0.9,pl;q=0.8\r\n" +
                "\r\n";
        return new ByteArrayInputStream(rawTestData.getBytes(StandardCharsets.US_ASCII));
    }
    private InputStream generateNoLFCase() {
        String rawTestData = "GET / HTTP/1.1\r" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en-US,en;q=0.9,pl;q=0.8\r\n" +
                "\r\n";
        return new ByteArrayInputStream(rawTestData.getBytes(StandardCharsets.US_ASCII));
    }
    private InputStream generateBadVersionRequest() {
        String rawTestData = "GET / HTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Chromium\";v=\"148\", \"Google Chrome\";v=\"148\", \"Not/A)Brand\";v=\"99\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en-US,en;q=0.9,pl;q=0.8\r\n" +
                "\r\n";
        return new ByteArrayInputStream(rawTestData.getBytes(StandardCharsets.US_ASCII));
    }
    private InputStream generateUnsupportedVersionRequest() {
        String rawTestData = "GET / HTTP/2.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Chromium\";v=\"148\", \"Google Chrome\";v=\"148\", \"Not/A)Brand\";v=\"99\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en-US,en;q=0.9,pl;q=0.8\r\n" +
                "\r\n";
        return new ByteArrayInputStream(rawTestData.getBytes(StandardCharsets.US_ASCII));
    }
    private InputStream generateHigherVersionRequest() {
        String rawTestData = "GET / HTTP/1.2\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Chromium\";v=\"148\", \"Google Chrome\";v=\"148\", \"Not/A)Brand\";v=\"99\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en-US,en;q=0.9,pl;q=0.8\r\n" +
                "\r\n";
        return new ByteArrayInputStream(rawTestData.getBytes(StandardCharsets.US_ASCII));
    }
}