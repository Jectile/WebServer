package org.richt;

import org.richt.config.ConfigurationManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    public static void main(String[] args) {
        IO.print("Starting Server...\n\n\n");

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        var config = ConfigurationManager.getInstance().getCurrentConfiguration();

        IO.println("Using WebRoot: " + config.getWebroot());
        IO.println("Using Port: " + config.getPort());

        try {
            ServerSocket serverSocket = new ServerSocket(config.getPort());
            Socket socket = serverSocket.accept();

            InputStream requestReader = socket.getInputStream();
            OutputStream responseWriter = socket.getOutputStream();

            // process

            String dummyHTML =
                    "<html>" +
                            "<head>" +
                                "<title>DummyHTTPResponse</title>" +
                            "</head>" +
                            "<body>" +
                                "<h1>Dummy Page -- Hello User!</h1>" +
                            "</body>" +
                    "</html>";
            final String CRLF = "\n\r";
            String response = "HTTP/1.1 200 OK" + CRLF +
                    "Content-Length: " + dummyHTML.getBytes().length + CRLF + // Header
                    CRLF +
                    dummyHTML +
                    CRLF + CRLF;

            responseWriter.write(response.getBytes());


            requestReader.close();
            responseWriter.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}