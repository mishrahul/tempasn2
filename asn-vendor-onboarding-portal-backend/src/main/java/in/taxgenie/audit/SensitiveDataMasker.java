package in.taxgenie.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for masking sensitive data in logs and responses
 * Supports both JSON and unstructured text masking
 */
public class SensitiveDataMasker {

    private static final Set<String> FIELDS_TO_MASK_FULLY = Set.of(
            "email", "emailId", "emailId1", "emailId2", "contactNumber", "phoneNo",
            "mobileNo", "address", "address1", "address2", "name", "legalName", "tradeName", 
            "supplierCompanyName", "firstName", "lastName", "supplierName", "buyerLegalName", 
            "buyerTradeName", "vendorName", "shipPhoneNumber", "shipEmail", "gstin", "gstinNo", 
            "legalname", "tradename", "contactPerson", "phone"
    );

    private static final Set<String> FIELDS_MASK_LAST_4_VISIBLE = Set.of(
            "panNumber", "narrationOfGstin", "vendorCode"
    );

    private static final Map<String, Pattern> FULL_MASK_PATTERNS = new ConcurrentHashMap<>();
    private static final Map<String, Pattern> PARTIAL_MASK_PATTERNS = new ConcurrentHashMap<>();
    private static final Set<String> NORMALIZED_FIELDS_TO_MASK_FULLY =
            FIELDS_TO_MASK_FULLY.stream().map(String::toLowerCase).collect(Collectors.toSet());
    private static final Set<String> NORMALIZED_FIELDS_MASK_LAST_4_VISIBLE =
            FIELDS_MASK_LAST_4_VISIBLE.stream().map(String::toLowerCase).collect(Collectors.toSet());

    static {
        for (String field : FIELDS_TO_MASK_FULLY) {
            FULL_MASK_PATTERNS.put(field.trim().toLowerCase(), buildFieldPattern(field));
        }
        for (String field : FIELDS_MASK_LAST_4_VISIBLE) {
            PARTIAL_MASK_PATTERNS.put(field.trim().toLowerCase(), buildFieldPattern(field));
        }
    }

    private static Pattern buildFieldPattern(String field) {
        // (?i)\bfield\b\s*[:=]?\s*(value in {..} or ".." or plain)
        String regex = "(?i)\\b" + Pattern.quote(field) + "\\b\\s*[:=]?\\s*(\\{[^}]+\\}|\"[^\"]+\"|\\S+)";
        return Pattern.compile(regex);
    }

    /**
     * Entry point for masking sensitive data
     * @param message Message to mask
     * @return Masked message
     */
    public static String mask(String message) {
        if (message == null) return null;

        // Check if valid JSON, if yes, skip fallback masking
        if (isValidJson(message)) {
            return maskJsonFields(message);
        }

        return maskUnstructuredMessage(message);
    }

    /**
     * Check if string is valid JSON
     * @param input Input string
     * @return true if valid JSON, false otherwise
     */
    public static boolean isValidJson(String input) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * For plain logs (key: value), not JSON
     * @param message Message to mask
     * @return Masked message
     */
    private static String maskUnstructuredMessage(String message) {
        // Full mask fields
        for (Map.Entry<String, Pattern> entry : FULL_MASK_PATTERNS.entrySet()) {
            String field = entry.getKey();
            Pattern pattern = entry.getValue();
            Matcher matcher = pattern.matcher(message);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, "\"" + field + "\": \"***\"");
            }
            matcher.appendTail(sb);
            message = sb.toString();
        }

        // Partial mask fields (e.g. PAN)
        for (Map.Entry<String, Pattern> entry : PARTIAL_MASK_PATTERNS.entrySet()) {
            String field = entry.getKey();
            Pattern pattern = entry.getValue();
            Matcher matcher = pattern.matcher(message);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String rawValue = matcher.group(1); // includes quotes/braces
                String cleanValue = rawValue.replaceAll("[\"{}]", "").trim();
                String masked = "\"" + maskExceptLast4(cleanValue) + "\"";
                matcher.appendReplacement(sb, "\"" + field + "\": " + masked);
            }
            matcher.appendTail(sb);
            message = sb.toString();
        }

        return message;
    }

    /**
     * Mask all but last 4 characters
     * @param value Value to mask
     * @return Masked value
     */
    private static String maskExceptLast4(String value) {
        if (value == null || value.length() <= 4) return "***";
        return "*".repeat(value.length() - 4) + value.substring(value.length() - 4);
    }

    /**
     * JSON masking support
     * @param json JSON string to mask
     * @return Masked JSON string
     */
    private static String maskJsonFields(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            maskJsonRecursive(root);
            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            return json; // fallback: not JSON
        }
    }

    /**
     * Recursive masking for nested objects/arrays
     * @param node JSON node to mask
     */
    private static void maskJsonRecursive(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objNode = (ObjectNode) node;
            Iterator<String> fieldNames = objNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode child = objNode.get(fieldName);

                if (child.isValueNode() && child.isTextual()) {
                    String masked = maskField(fieldName, child.asText());
                    objNode.put(fieldName, masked);
                } else {
                    maskJsonRecursive(child);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                maskJsonRecursive(item);
            }
        }
    }

    /**
     * Actual mask logic per field
     * @param key Field name
     * @param value Field value
     * @return Masked value
     */
    private static String maskField(String key, String value) {
        if (value == null) return null;

        String normalizedKey = key.trim().toLowerCase();
        if (NORMALIZED_FIELDS_TO_MASK_FULLY.contains(normalizedKey)) {
            return "***";
        } else if (NORMALIZED_FIELDS_MASK_LAST_4_VISIBLE.contains(normalizedKey)) {
            return maskExceptLast4(value);
        }
        return value;
    }
}
