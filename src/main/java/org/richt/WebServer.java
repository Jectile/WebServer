package org.richt;

import org.richt.config.ConfigurationManager;
import org.richt.core.ServerListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;


public class WebServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Server...\n\n\n");

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        var config = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Using WebRoot: " + config.getWebroot());
        LOGGER.info("Using Port: " + config.getPort());

        ServerListenerThread serverListenerThread = null;
        try {
            serverListenerThread = new ServerListenerThread(config.getPort(), config.getWebroot());
            serverListenerThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}