package main.java.com.ecohabit.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {
    
    // Simple JSON parser implementation
    public static String toJson(Object object) {
        if (object instanceof Map) {
            return mapToJson((Map<String, Object>) object);
        } else if (object instanceof List) {
            return listToJson((List<?>) object);
        } else if (object instanceof String) {
            return "\"" + escapeJsonString((String) object) + "\"";
        } else if (object instanceof Number || object instanceof Boolean) {
            return object.toString();
        } else if (object == null) {
            return "null";
        } else {
            return "\"" + escapeJsonString(object.toString()) + "\"";
        }
    }
    
    private static String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\"").append(escapeJsonString(entry.getKey())).append("\":");
            sb.append(toJson(entry.getValue()));
            first = false;
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    private static String listToJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        
        for (Object item : list) {
            if (!first) {
                sb.append(",");
            }
            sb.append(toJson(item));
            first = false;
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    private static String escapeJsonString(String str) {
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\b", "\\b")
                 .replace("\f", "\\f")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
    
    public static Map<String, Object> jsonToMap(String json) {
        String trimmed = json.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return parseObject(trimmed.substring(1, trimmed.length() - 1).trim());
        }
        throw new IllegalArgumentException("Invalid JSON object: " + json);
    }
    
    public static List<Map<String, Object>> jsonToList(String json) {
        String trimmed = json.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            return parseArray(trimmed.substring(1, trimmed.length() - 1).trim());
        }
        throw new IllegalArgumentException("Invalid JSON array: " + json);
    }
    
    private static Map<String, Object> parseObject(String json) {
        Map<String, Object> map = new HashMap<>();
        if (json.isEmpty()) return map;
        
        int index = 0;
        while (index < json.length()) {
            // Parse key
            int keyStart = json.indexOf('"', index);
            if (keyStart == -1) break;
            int keyEnd = json.indexOf('"', keyStart + 1);
            if (keyEnd == -1) break;
            
            String key = unescapeJsonString(json.substring(keyStart + 1, keyEnd));
            
            // Find colon
            int colonIndex = json.indexOf(':', keyEnd + 1);
            if (colonIndex == -1) break;
            
            // Parse value
            int valueStart = colonIndex + 1;
            while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
                valueStart++;
            }
            
            Object value = parseValue(json, valueStart);
            map.put(key, value);
            
            // Find next comma or end
            index = findNextTokenEnd(json, valueStart);
            if (index < json.length() && json.charAt(index) == ',') {
                index++;
            }
        }
        
        return map;
    }
    
    private static List<Map<String, Object>> parseArray(String json) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (json.isEmpty()) return list;
        
        int index = 0;
        while (index < json.length()) {
            // Parse object
            if (json.charAt(index) == '{') {
                int end = findMatchingBrace(json, index, '{', '}');
                if (end == -1) break;
                
                String objectJson = json.substring(index, end + 1);
                list.add(jsonToMap(objectJson));
                
                index = end + 1;
                if (index < json.length() && json.charAt(index) == ',') {
                    index++;
                }
            } else {
                // Skip non-object elements
                int nextComma = json.indexOf(',', index);
                if (nextComma == -1) break;
                index = nextComma + 1;
            }
        }
        
        return list;
    }
    
    private static Object parseValue(String json, int start) {
        if (start >= json.length()) return null;
        
        char firstChar = json.charAt(start);
        
        if (firstChar == '"') {
            // String
            int end = json.indexOf('"', start + 1);
            if (end == -1) return null;
            return unescapeJsonString(json.substring(start + 1, end));
        } else if (firstChar == '{') {
            // Object
            int end = findMatchingBrace(json, start, '{', '}');
            if (end == -1) return null;
            return jsonToMap(json.substring(start, end + 1));
        } else if (firstChar == '[') {
            // Array
            int end = findMatchingBrace(json, start, '[', ']');
            if (end == -1) return null;
            return jsonToList(json.substring(start, end + 1));
        } else if (json.startsWith("true", start)) {
            // Boolean true
            return true;
        } else if (json.startsWith("false", start)) {
            // Boolean false
            return false;
        } else if (json.startsWith("null", start)) {
            // Null
            return null;
        } else {
            // Number
            Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?");
            Matcher matcher = numberPattern.matcher(json.substring(start));
            if (matcher.find()) {
                String numStr = matcher.group();
                try {
                    if (numStr.contains(".") || numStr.toLowerCase().contains("e")) {
                        return Double.parseDouble(numStr);
                    } else {
                        return Long.parseLong(numStr);
                    }
                } catch (NumberFormatException e) {
                    return numStr;
                }
            }
        }
        
        return null;
    }
    
    private static int findMatchingBrace(String json, int start, char open, char close) {
        int count = 1;
        for (int i = start + 1; i < json.length(); i++) {
            if (json.charAt(i) == open) {
                count++;
            } else if (json.charAt(i) == close) {
                count--;
                if (count == 0) {
                    return i;
                }
            } else if (json.charAt(i) == '"') {
                // Skip strings
                i = json.indexOf('"', i + 1);
                if (i == -1) break;
            }
        }
        return -1;
    }
    
    private static int findNextTokenEnd(String json, int start) {
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == ',' || c == '}' || c == ']') {
                return i;
            } else if (c == '"') {
                i = json.indexOf('"', i + 1);
                if (i == -1) break;
            } else if (c == '{' || c == '[') {
                char close = (c == '{') ? '}' : ']';
                i = findMatchingBrace(json, i, c, close);
                if (i == -1) break;
            }
        }
        return json.length();
    }
    
    private static String unescapeJsonString(String str) {
        return str.replace("\\\"", "\"")
                 .replace("\\\\", "\\")
                 .replace("\\b", "\b")
                 .replace("\\f", "\f")
                 .replace("\\n", "\n")
                 .replace("\\r", "\r")
                 .replace("\\t", "\t");
    }
    
    public static void writeJsonToFile(Object object, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(toJson(object));
        }
    }
    
    public static Map<String, Object> readJsonFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return jsonToMap(content.toString());
        }
    }
    
    public static List<Map<String, Object>> readJsonArrayFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return jsonToList(content.toString());
        }
    }
    
    public static String convertToCsv(String json, String[] headers) {
        List<Map<String, Object>> data = jsonToList(json);
        StringBuilder csv = new StringBuilder();
        
        // Write headers
        csv.append(String.join(",", headers)).append("\n");
        
        // Write data
        for (Map<String, Object> row : data) {
            for (int i = 0; i < headers.length; i++) {
                if (i > 0) csv.append(",");
                Object value = row.get(headers[i]);
                if (value != null) {
                    String valueStr = value.toString().contains(",") ? 
                        "\"" + value.toString().replace("\"", "\"\"") + "\"" : value.toString();
                    csv.append(valueStr);
                }
            }
            csv.append("\n");
        }
        
        return csv.toString();
    }
    
    public static String escapeJson(String json) {
        return escapeJsonString(json);
    }
    
    public static boolean isValidJson(String json) {
        try {
            String trimmed = json.trim();
            if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
                jsonToMap(json);
                return true;
            } else if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                jsonToList(json);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static Map<String, Object> loadApplicationConfig() {
        try {
            return readJsonFromFile("config/application.json");
        } catch (IOException e) {
            System.err.println("Failed to load application config: " + e.getMessage());
            
            // Return default configuration
            Map<String, Object> config = new HashMap<>();
            config.put("theme", "dark");
            config.put("font_size", 14);
            config.put("notifications", true);
            config.put("auto_backup", true);
            config.put("data_retention_days", 30);
            return config;
        }
    }
    
    public static void saveApplicationConfig(Map<String, Object> config) {
        try {
            new java.io.File("config").mkdirs();
            writeJsonToFile(config, "config/application.json");
        } catch (IOException e) {
            System.err.println("Failed to save application config: " + e.getMessage());
        }
    }
    
    public static Map<String, Object> loadUserPreferences(String userId) {
        try {
            String filePath = "config/user_" + userId + "_preferences.json";
            return readJsonFromFile(filePath);
        } catch (IOException e) {
            System.err.println("Failed to load user preferences: " + e.getMessage());
            
            // Return default preferences
            Map<String, Object> prefs = new HashMap<>();
            prefs.put("daily_reminder", true);
            prefs.put("weekly_report", true);
            prefs.put("goal_notifications", true);
            prefs.put("achievement_alerts", true);
            prefs.put("eco_tips_frequency", "daily");
            return prefs;
        }
    }
    
    public static void saveUserPreferences(String userId, Map<String, Object> preferences) {
        try {
            String filePath = "config/user_" + userId + "_preferences.json";
            new java.io.File("config").mkdirs();
            writeJsonToFile(preferences, filePath);
        } catch (IOException e) {
            System.err.println("Failed to save user preferences: " + e.getMessage());
        }
    }
    
    public static List<Map<String, Object>> loadEcoTips() {
        try {
            return readJsonArrayFromFile("data/eco_tips.json");
        } catch (IOException e) {
            System.err.println("Failed to load eco tips: " + e.getMessage());
            
            // Return default tips
            List<Map<String, Object>> tips = new ArrayList<>();
            
            Map<String, Object> tip1 = new HashMap<>();
            tip1.put("tip", "Switch to cold water washing to save up to 90% of laundry energy");
            tip1.put("category", "Energy");
            tip1.put("impact", "High");
            tip1.put("difficulty", "Easy");
            tips.add(tip1);
            
            Map<String, Object> tip2 = new HashMap<>();
            tip2.put("tip", "Choose plant-based meals to reduce your carbon footprint");
            tip2.put("category", "Food");
            tip2.put("impact", "High");
            tip2.put("difficulty", "Medium");
            tips.add(tip2);
            
            Map<String, Object> tip3 = new HashMap<>();
            tip3.put("tip", "Use reusable bags instead of plastic bags");
            tip3.put("category", "Waste");
            tip3.put("impact", "Medium");
            tip3.put("difficulty", "Easy");
            tips.add(tip3);
            
            return tips;
        }
    }
    
    public static void exportUserData(String userId, List<Map<String, Object>> data, String format) {
        try {
            String directory = "exports/user_" + userId;
            new java.io.File(directory).mkdirs();
            
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = directory + "/export_" + timestamp + "." + format.toLowerCase();
            
            if ("json".equalsIgnoreCase(format)) {
                writeJsonToFile(data, fileName);
            } else if ("csv".equalsIgnoreCase(format)) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                    if (!data.isEmpty()) {
                        String[] headers = data.get(0).keySet().toArray(new String[0]);
                        writer.write(convertToCsv(toJson(data), headers));
                    }
                }
            }
            
            System.out.println("Data exported successfully to: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to export user data: " + e.getMessage());
        }
    }

	public static void saveApplicationState() {
		// TODO Auto-generated method stub
		
	}

	public static boolean hasValidSession() {
		// TODO Auto-generated method stub
		return false;
	}
}