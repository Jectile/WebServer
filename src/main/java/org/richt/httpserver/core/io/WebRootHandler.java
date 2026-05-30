package org.richt.httpserver.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;

public class WebRootHandler {
    private final File webRoot;

    public WebRootHandler(String webRootPath) throws WebRootNotFoundException {
        webRoot = new File(webRootPath);
        if (!webRoot.exists() || !webRoot.isDirectory()) {
            throw new WebRootNotFoundException("Webroot Provided DNE");
        }
    }

    private boolean checkIfEndsWithSlash(String relativeFilePath) {
        return relativeFilePath.endsWith("/");
    }
    private boolean existsInWebRoot(String relativeFilePath) {
        File file = new File(webRoot, relativeFilePath);
        if (!file.exists()) return false;

        try {
            if (file.getCanonicalPath().startsWith(webRoot.getCanonicalPath())) return true;
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    public String getFileMimeType(String relativeFilePath) throws FileNotFoundException {
        if (checkIfEndsWithSlash(relativeFilePath)) relativeFilePath += "index.html";
        if (!existsInWebRoot(relativeFilePath)) throw new FileNotFoundException("File not found: " + relativeFilePath);

        File file = new File(webRoot, relativeFilePath);
        String mime = URLConnection.getFileNameMap().getContentTypeFor(file.getName());
        if (mime == null) return "application/octet-stream";

        return mime;
    }

    public byte[] getFileByteArray(String relativeFilePath ) throws FileNotFoundException, ReadFileException {
        if (checkIfEndsWithSlash(relativeFilePath)) relativeFilePath += "index.html";
        if (!existsInWebRoot(relativeFilePath)) throw new FileNotFoundException("File not found: " + relativeFilePath);

        File file = new File(webRoot, relativeFilePath);
        byte[] fileBytes = new byte[(int)file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int wasRead = fileInputStream.read(fileBytes);
        } catch (IOException e) {
            throw new ReadFileException(e);
        }
        return fileBytes;
    }
}
