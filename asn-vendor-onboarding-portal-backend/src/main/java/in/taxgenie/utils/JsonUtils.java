package in.taxgenie.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Utility class for JSON parsing and manipulation
 * Provides common JSON operations used across the application
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Component
@Slf4j
public class JsonUtils {

    private final ObjectMapper objectMapper;

    public JsonUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Parse JSON string to Map
     * 
     * @param json JSON string
     * @return Map representation of JSON, empty map if parsing fails
     */
    public Map<String, Object> parseJsonToMap(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON to Map: {}", json, e);
            return new HashMap<>();
        }
    }

    /**
     * Parse JSON string to List
     * 
     * @param json JSON string
     * @return List representation of JSON, empty list if parsing fails
     */
    public List<String> parseJsonToList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON to List: {}", json, e);
            return new ArrayList<>();
        }
    }

    /**
     * Parse JSON array string to List (simple parsing without Jackson)
     * Handles format: ["item1", "item2", "item3"]
     * 
     * @param json JSON array string
     * @return List of strings
     */
    public List<String> parseSimpleJsonArray(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // Remove brackets and split by comma
            String cleaned = json.trim().replaceAll("^\\[|\\]$", "");
            if (cleaned.isEmpty()) {
                return new ArrayList<>();
            }

            return Arrays.stream(cleaned.split(","))
                    .map(s -> s.trim().replaceAll("\"", ""))
                    .filter(s -> !s.isEmpty())
                    .toList();
        } catch (Exception e) {
            log.warn("Failed to parse simple JSON array: {}", json, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get BigDecimal value from JSON map
     * 
     * @param map JSON map
     * @param key Key to retrieve
     * @return BigDecimal value, or BigDecimal.ZERO if not found or invalid
     */
    public BigDecimal getBigDecimalFromMap(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) {
            return BigDecimal.ZERO;
        }
        
        Object value = map.get(key);
        if (value == null) {
            return BigDecimal.ZERO;
        }
        
        try {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            } else if (value instanceof Number) {
                return BigDecimal.valueOf(((Number) value).doubleValue());
            } else if (value instanceof String) {
                return new BigDecimal((String) value);
            }
        } catch (Exception e) {
            log.warn("Failed to convert value to BigDecimal for key {}: {}", key, value, e);
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Get String value from JSON map
     * 
     * @param map JSON map
     * @param key Key to retrieve
     * @param defaultValue Default value if not found
     * @return String value or default
     */
    public String getStringFromMap(Map<String, Object> map, String key, String defaultValue) {
        if (map == null || !map.containsKey(key)) {
            return defaultValue;
        }
        
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * Get Integer value from JSON map
     * 
     * @param map JSON map
     * @param key Key to retrieve
     * @param defaultValue Default value if not found
     * @return Integer value or default
     */
    public Integer getIntegerFromMap(Map<String, Object> map, String key, Integer defaultValue) {
        if (map == null || !map.containsKey(key)) {
            return defaultValue;
        }
        
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
        } catch (Exception e) {
            log.warn("Failed to convert value to Integer for key {}: {}", key, value, e);
        }
        
        return defaultValue;
    }

    /**
     * Convert object to JSON string
     * 
     * @param object Object to convert
     * @return JSON string representation
     */
    public String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON string", e);
            return "{}";
        }
    }

    /**
     * Convert object to pretty JSON string
     * 
     * @param object Object to convert
     * @return Pretty formatted JSON string
     */
    public String toPrettyJsonString(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to pretty JSON string", e);
            return "{}";
        }
    }

    /**
     * Parse JSON to specific type
     * 
     * @param json JSON string
     * @param valueType Target class type
     * @param <T> Type parameter
     * @return Parsed object or null if parsing fails
     */
    public <T> T parseJson(String json, Class<T> valueType) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON to type {}: {}", valueType.getName(), json, e);
            return null;
        }
    }

    /**
     * Check if string is valid JSON
     * 
     * @param json String to check
     * @return true if valid JSON, false otherwise
     */
    public boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * Merge two JSON objects (maps)
     * 
     * @param base Base map
     * @param override Override map (values from this map take precedence)
     * @return Merged map
     */
    public Map<String, Object> mergeJsonMaps(Map<String, Object> base, Map<String, Object> override) {
        Map<String, Object> result = new HashMap<>(base);
        if (override != null) {
            result.putAll(override);
        }
        return result;
    }
}

