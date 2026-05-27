package org.richt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

public class Json {
    private static ObjectMapper mapper = defaultMapper();
    private static ObjectMapper defaultMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false
        );
        return objectMapper;
    }

    public static JsonNode parse(String jsonSrc) throws JsonProcessingException {
        return mapper.readTree(jsonSrc);
    }

    public static <T> T fromJson(JsonNode node, Class<T> typeClass) throws JsonProcessingException {
        return mapper.treeToValue(node, typeClass);
    }

    public static JsonNode toJson(Object obj) {
        return mapper.valueToTree(obj);
    }

    private static String generateJson(Object obj, boolean pretty) throws JsonProcessingException {
        return pretty ?
                mapper.writer().with(SerializationFeature.INDENT_OUTPUT).writeValueAsString(obj) :
                mapper.writer().writeValueAsString(obj);
    }

    public static String stringify(JsonNode node) throws JsonProcessingException {
        return generateJson(node, false);
    }

    public static String stringifyPretty(JsonNode node) throws JsonProcessingException {
        return generateJson(node, true);
    }

}
