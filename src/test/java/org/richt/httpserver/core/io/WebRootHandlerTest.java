package org.richt.httpserver.core.io;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WebRootHandlerTest {
    private WebRootHandler socials;
    private Method slashCheck;
    private Method existsInWebRoot;

    @BeforeAll
    public void beforeClass() throws WebRootNotFoundException, NoSuchMethodException {
        socials = new WebRootHandler("../../webpages/social-links");
        Class<WebRootHandler> cls = WebRootHandler.class;
        slashCheck = cls.getDeclaredMethod("checkIfEndsWithSlash", String.class);
        slashCheck.setAccessible(true);
        existsInWebRoot = cls.getDeclaredMethod("existsInWebRoot", String.class);
        existsInWebRoot.setAccessible(true);
    }

    @Test
    void constructorGoodPath() {
        try {
            WebRootHandler handler = new WebRootHandler(
                    "C:\\Users\\Simon\\Code Projects\\webpages");
        } catch (WebRootNotFoundException e) {
            fail(e);
        }
    }

    @Test
    void constructorBadPath() {
        try {
            WebRootHandler handler = new WebRootHandler(
                    "C:\\Users\\Simon\\Code Projects\\poo");
            fail("Not throwing on bad path :(");
        } catch (WebRootNotFoundException _) {
        }
    }

    @Test
    void relativeGoodPath() {
        try {
            WebRootHandler handler = new WebRootHandler("src");
        } catch (WebRootNotFoundException e) {
            fail(e);
        }
    }

    @Test
    void relativeBadPath() {
        try {
            WebRootHandler handler = new WebRootHandler(
                    "poo");
            fail("Not throwing on bad path :(");
        } catch (WebRootNotFoundException _) {
        }
    }

    @Test
    void endsWithSlashCheck1() {
        try {
            assertFalse((Boolean) slashCheck.invoke(socials, "social-links/index.html"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void endsWithSlashCheck2() {
        try {
            assertTrue((Boolean) slashCheck.invoke(socials, "social-links/index.html/"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void endsWithSlashCheck3() {
        try {
            assertFalse((Boolean) slashCheck.invoke(socials, "what the what?!?"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void checkValidPathInWebRoot1() {
        try {
            assertTrue((Boolean) existsInWebRoot.invoke(socials, "index.html"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void checkValidPathInWebRoot2() {
        try {
            assertFalse((Boolean) existsInWebRoot.invoke(socials, "epicPics.png"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void checkValidPathOutsideWebRoot() {
        try {
            assertFalse((Boolean) existsInWebRoot.invoke(socials, "../../../.lesshst"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void testGetFileMimeTypeText() {
        try {
            assertEquals("text/html", socials.getFileMimeType("/"));
        } catch (FileNotFoundException e) { fail(e); }
    }

    @Test
    void testGetFileMimeTypeMD() {
        try {
            assertEquals("text/markdown", socials.getFileMimeType("README.MD"));
        } catch (FileNotFoundException e) { fail(e); }
    }

    @Test
    void testGetFileMimeTypePNG() {
        try {
            assertEquals("image/png", socials.getFileMimeType("/assets/images/favicon-32x32.PNG"));
        } catch (FileNotFoundException e) { fail(e); }
    }

    @Test
    void testGetFileMimeTypeJPEG() {
        try {
            assertEquals("image/jpeg", socials.getFileMimeType("/assets/images/avatar-jessica.JPEG"));
        } catch (FileNotFoundException e) { fail(e); }
    }

    @Test
    void testGetFileByteArray() {
        try {
            assertTrue(socials.getFileByteArray("/").length > 0);
        } catch (Exception e) {fail(e);}
    }

    @Test
    void testGetBadFileByteArray() {
        try {
            byte[] bytes = socials.getFileByteArray("/test.html");
            fail("No exception called");
        } catch (FileNotFoundException _) {}
        catch (Exception e) { fail(e); }
    }
}