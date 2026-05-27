package org.richt.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.richt.util.Json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigurationManager {
    private static ConfigurationManager configManager;
    private static Configuration currentConfig;

    private ConfigurationManager() {

    }

    public static ConfigurationManager getInstance() {
        if (configManager == null) configManager = new ConfigurationManager();
        return configManager;
    }

    public void loadConfigurationFile(String filePath) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filePath);
        } catch (FileNotFoundException fnf) {
            throw new HttpConfigurationException(fnf);
        }

        var buffer = new StringBuffer();
        int i;
        try {
            while((i = fileReader.read()) != -1) {
                buffer.append((char) i);
            }
        } catch (IOException fnf) {
            throw new HttpConfigurationException(fnf);
        }
        JsonNode conf = null;
        try {
            conf = Json.parse(buffer.toString());
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error parsing Config file", e);
        }
        try {
            currentConfig = Json.fromJson(conf, Configuration.class);
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error parsing Config file internal", e);
        }
    }

    public Configuration getCurrentConfiguration() {
        if (currentConfig == null) throw new HttpConfigurationException("No Current Configuration Set");
        return currentConfig;
    }
}
