package com.zetyun.daemon.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.IOException;
import java.util.*;

public class ModuleExtractor {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Integer> extractModules(List wideTableFields, String jsonString) {
        Map<String, Integer> result = new HashMap<>();
        try {

            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode dataNode = rootNode.get("data");

            if (dataNode != null && dataNode.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    JsonNode moduleNode = entry.getValue();
                    String moduleType = moduleNode.get("moduleType").asText();
                    if (wideTableFields.contains(moduleType.toLowerCase(Locale.ROOT))) {
                        int moduleCount = moduleNode.get("moduleCount").asInt();
                        result.put(moduleType, moduleCount);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static double extractValue(String jsonString) {
        Map<String, String> dataMap = new HashMap<>();
        try {
            // 解析 JSON 并提取 occupancy 对象
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonString);
            JsonNode occupancyNode = rootNode.path("data").path("detail").path("occupancy");

            if (!occupancyNode.isMissingNode()) {
                return occupancyNode.path("value").asDouble();
            } else {
                return 0.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
