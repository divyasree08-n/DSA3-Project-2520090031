package com.klh.dsa.web;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Minimal JSON helper utilities for the embedded web UI. */
public final class JsonUtil {
    private JsonUtil() {
    }

    public static String escape(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                case '\b' -> builder.append("\\b");
                case '\f' -> builder.append("\\f");
                default -> {
                    if (ch < 0x20) {
                        builder.append("\\u");
                        builder.append(String.format("%04x", (int) ch));
                    } else {
                        builder.append(ch);
                    }
                }
            }
        }
        return builder.toString();
    }

    public static String toJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                builder.append(",");
            }
            first = false;
            builder.append('"').append(escape(entry.getKey())).append('"')
                    .append(":")
                    .append(toJsonValue(entry.getValue()));
        }
        builder.append("}");
        return builder.toString();
    }

    public static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> result = new LinkedHashMap<>();
        if (json == null || json.isBlank()) {
            return result;
        }
        String trimmed = json.trim();
        if (trimmed.isEmpty() || "{}".equals(trimmed)) {
            return result;
        }
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            return result;
        }
        String body = trimmed.substring(1, trimmed.length() - 1).trim();
        if (body.isEmpty()) {
            return result;
        }
        List<String> parts = splitTopLevel(body);
        for (String part : parts) {
            String field = part.trim();
            if (field.isEmpty()) {
                continue;
            }
            int colonIndex = indexOfUnquoted(field, ':');
            if (colonIndex < 0) {
                continue;
            }
            String key = parseString(field.substring(0, colonIndex).trim());
            String value = parseString(field.substring(colonIndex + 1).trim());
            if (key != null && !key.isEmpty()) {
                result.put(key, value);
            }
        }
        return result;
    }

    public static String errorJson(String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("success", false);
        payload.put("error", message == null ? "Request failed" : message);
        return toJson(payload);
    }

    public static String successJson(String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("success", true);
        payload.put("message", message == null ? "Success" : message);
        return toJson(payload);
    }

    private static String toJsonValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return '"' + escape((String) value) + '"';
        }
        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        }
        if (value instanceof Map<?, ?>) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                map.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            return toJson(map);
        }
        if (value instanceof Iterable<?>) {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            boolean first = true;
            for (Object item : (Iterable<?>) value) {
                if (!first) {
                    builder.append(",");
                }
                first = false;
                builder.append(toJsonValue(item));
            }
            builder.append("]");
            return builder.toString();
        }
        return '"' + escape(String.valueOf(value)) + '"';
    }

    private static List<String> splitTopLevel(String content) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        boolean escaped = false;
        int bracketDepth = 0;
        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            if (inString) {
                current.append(ch);
                if (escaped) {
                    escaped = false;
                } else if (ch == '\\') {
                    escaped = true;
                } else if (ch == '"') {
                    inString = false;
                }
                continue;
            }
            if (ch == '"') {
                inString = true;
                current.append(ch);
            } else if (ch == '{' || ch == '[') {
                bracketDepth++;
                current.append(ch);
            } else if (ch == '}' || ch == ']') {
                bracketDepth = Math.max(0, bracketDepth - 1);
                current.append(ch);
            } else if (ch == ',' && bracketDepth == 0) {
                tokens.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }
        return tokens;
    }

    private static int indexOfUnquoted(String text, char target) {
        boolean inString = false;
        boolean escaped = false;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (ch == '\\') {
                    escaped = true;
                } else if (ch == '"') {
                    inString = false;
                }
            } else if (ch == '"') {
                inString = true;
            } else if (ch == target) {
                return i;
            }
        }
        return -1;
    }

    private static String parseString(String value) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            String inner = trimmed.substring(1, trimmed.length() - 1);
            StringBuilder builder = new StringBuilder();
            boolean escaped = false;
            for (int i = 0; i < inner.length(); i++) {
                char ch = inner.charAt(i);
                if (escaped) {
                    switch (ch) {
                        case '"' -> builder.append('"');
                        case '\\' -> builder.append('\\');
                        case 'n' -> builder.append('\n');
                        case 'r' -> builder.append('\r');
                        case 't' -> builder.append('\t');
                        case 'b' -> builder.append('\b');
                        case 'f' -> builder.append('\f');
                        default -> builder.append(ch);
                    }
                    escaped = false;
                    continue;
                }
                if (ch == '\\') {
                    escaped = true;
                } else {
                    builder.append(ch);
                }
            }
            return builder.toString();
        }
        return trimmed.equals("null") ? null : trimmed;
    }
}
