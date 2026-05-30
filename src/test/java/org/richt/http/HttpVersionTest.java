package org.richt.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpVersionTest {
    @Test
    void getBestCompatibleVersionExactMatch() {
        HttpVersion v = null;
        try {
            v = HttpVersion.getBestCompatibleVersion("HTTP/1.1");
        } catch (BadHttpVersionException e) {
            fail("Unexpected Exception");
        }
        assertNotNull(v);
        assertEquals(HttpVersion.HTTP_1_1, v);
    }

    @Test
    void getBestCompatibleBadFormat() {
        HttpVersion v = null;
        try {
            v = HttpVersion.getBestCompatibleVersion("HtTP/1.1");
            fail("Expected BadHttpVersionException");
        } catch (BadHttpVersionException _) {}
    }

    @Test
    void getBestCompatibleHigherVersion() {
        HttpVersion v = null;
        try {
            v = HttpVersion.getBestCompatibleVersion("HTTP/1.2");
            assertNotNull(v);
            assertEquals(HttpVersion.HTTP_1_1, v);
        } catch (BadHttpVersionException _) { fail("Unexpected Exception");}
    }
}