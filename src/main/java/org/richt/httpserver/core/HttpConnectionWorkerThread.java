package org.richt.httpserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
    
    private final Socket socket;
    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream requestReader = null;
        OutputStream responseWriter = null;

        try {
            requestReader = socket.getInputStream();
            responseWriter = socket.getOutputStream();

            // process
            var dummyHTML =
                    "<html>" +
                            "<head>" +
                            "<title>DummyHTTPResponse</title>" +
                            "</head>" +
                            "<body>" +
                            "<h1>Dummy Page -- Hello User!</h1>" +
                            "</body>" +
                            "</html>";
            final var CRLF = "\n\r";
            var response = "HTTP/1.1 200 OK" + CRLF +
                    "Content-Length: " + dummyHTML.getBytes().length + CRLF + // Header
                    CRLF +
                    dummyHTML +
                    CRLF + CRLF;
            
            responseWriter.write(response.getBytes());
            LOGGER.info(" * Processing finished");

        } catch (IOException e) {
            LOGGER.error(" ~ Problem with communication", e);
        } finally {
            try {
                if (requestReader != null) requestReader.close();
            } catch (IOException _) {}

            try {
                if (responseWriter != null) responseWriter.close();
            } catch (IOException _) {}
            try {
                if (socket != null) socket.close();
            } catch (IOException _) {}
        }
    }
}
