package org.richt.http;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpHeadersParseTest {
    private HttpParser parser;
    private Method headers;
    private HttpRequest request;

    @BeforeEach
    public void setupRequest() {
        request = new HttpRequest();
    }

    @BeforeAll
    public void beforeClass() throws NoSuchMethodException {
        parser = new HttpParser();
        Class<HttpParser> cls = HttpParser.class;
        headers = cls.getDeclaredMethod("parseRequestHeaders",
                InputStreamReader.class,
                HttpRequest.class
        );
        headers.setAccessible(true);
    }

    /**
     * Tests parsing a simple header
     */
    @Test
    void testSimpleSingleHeader() throws InvocationTargetException, IllegalAccessException {
        headers.invoke(
                parser,
                generateSimpleSingleHeader(),
                request
        );
        assertNotNull(request);
        assertEquals(1, request.getHeaderNames().size());
        assertEquals("localhost:8080", request.getHeader("host"));
    }

    /**
     * Tests parsing multiple headers
     */
    @Test
    void testMultipleHeader() throws InvocationTargetException, IllegalAccessException {
        headers.invoke(
                parser,
                generateMultipleHeader(),
                request
        );
        assertNotNull(request);
        assertEquals(10, request.getHeaderNames().size());
        assertEquals("localhost:8080", request.getHeader("host"));
    }

    private InputStreamReader generateSimpleSingleHeader() {
        String rawTestData =
                "Host: localhost:8080\r\n";
        InputStream stream = new ByteArrayInputStream(rawTestData.getBytes(StandardCharsets.US_ASCII));
        return new InputStreamReader(stream, StandardCharsets.US_ASCII);
    }
    private InputStreamReader generateMultipleHeader() {
        String rawTestData = "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: en-US,en;q=0.9,es;q=0.8,pt;q=0.7,de-DE;q=0.6,de;q=0.5,la;q=0.4\r\n" +
                "\r\n";
        InputStream stream = new ByteArrayInputStream(rawTestData.getBytes(StandardCharsets.US_ASCII));
        return new InputStreamReader(stream, StandardCharsets.US_ASCII);
    }
}