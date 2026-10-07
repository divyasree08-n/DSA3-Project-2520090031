package com.klh.dsa.web;

import com.klh.dsa.matcher.MatchingEngine;
import com.klh.dsa.model.Candidate;
import com.klh.dsa.model.Job;
import com.klh.dsa.nlp.TextProcessor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/** Embedded web server for the Resume-Job Matcher UI and API. */
public final class WebServer {
    private static final int PORT = 8080;
    private static final AuthService AUTH_SERVICE = new AuthService();

    private WebServer() {
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.setExecutor(Executors.newFixedThreadPool(8));

        server.createContext("/", new RootHandler());
        server.createContext("/api/register", new RegisterHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/logout", new LogoutHandler());
        server.createContext("/api/match", new MatchHandler());
        server.createContext("/api/health", new HealthHandler());

        server.start();
        System.out.println("=============================================================");
        System.out.println("  Resume-Job Matcher Web UI");
        System.out.println("  Server started at http://localhost:8080");
        System.out.println("  Admin credentials: admin / admin@123");
        System.out.println("=============================================================");
    }

    private static final class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                handleRequest(exchange);
            } catch (Exception exception) {
                sendJson(exchange, 500, JsonUtil.errorJson("Internal server error"));
            }
        }

        private void handleRequest(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if ("OPTIONS".equalsIgnoreCase(method)) {
                sendOptions(exchange);
                return;
            }
            if (!"GET".equalsIgnoreCase(method)) {
                sendJson(exchange, 405, JsonUtil.errorJson("Method not allowed"));
                return;
            }
            String path = sanitizePath(exchange.getRequestURI());
            if (path == null || path.isBlank() || "/".equals(path)) {
                path = "/web/index.html";
            }
            String resourcePath = resolveResourcePath(path);
            if (resourcePath == null) {
                sendNotFound(exchange);
                return;
            }
            byte[] bytes = readResource(resourcePath);
            if (bytes == null) {
                sendNotFound(exchange);
                return;
            }
            sendStatic(exchange, bytes, contentTypeFor(resourcePath));
        }
    }

    private static final class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendOptions(exchange);
                    return;
                }
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendJson(exchange, 405, JsonUtil.errorJson("Method not allowed"));
                    return;
                }
                Map<String, String> body = JsonUtil.parseSimpleJson(readRequestBody(exchange));
                String username = body.getOrDefault("username", "").trim();
                String password = body.getOrDefault("password", "");
                String role = body.getOrDefault("role", "USER");
                boolean success = AUTH_SERVICE.register(username, password, role);
                if (!success) {
                    sendJson(exchange, 400, JsonUtil.errorJson("Registration failed"));
                    return;
                }
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("success", true);
                response.put("message", "User registered successfully");
                response.put("username", username);
                response.put("role", role.toUpperCase());
                sendJson(exchange, 200, JsonUtil.toJson(response));
            } catch (Exception exception) {
                sendJson(exchange, 500, JsonUtil.errorJson(exception.getMessage() == null ? "Registration error" : exception.getMessage()));
            }
        }
    }

    private static final class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendOptions(exchange);
                    return;
                }
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendJson(exchange, 405, JsonUtil.errorJson("Method not allowed"));
                    return;
                }
                Map<String, String> body = JsonUtil.parseSimpleJson(readRequestBody(exchange));
                String username = body.getOrDefault("username", "").trim();
                String password = body.getOrDefault("password", "");
                String token = AUTH_SERVICE.login(username, password);
                if (token == null) {
                    sendJson(exchange, 401, JsonUtil.errorJson("Invalid username or password"));
                    return;
                }
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("success", true);
                response.put("token", token);
                response.put("username", username);
                response.put("role", AUTH_SERVICE.getRole(username));
                sendJson(exchange, 200, JsonUtil.toJson(response));
            } catch (Exception exception) {
                sendJson(exchange, 500, JsonUtil.errorJson(exception.getMessage() == null ? "Login error" : exception.getMessage()));
            }
        }
    }

    private static final class LogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendOptions(exchange);
                    return;
                }
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendJson(exchange, 405, JsonUtil.errorJson("Method not allowed"));
                    return;
                }
                Map<String, String> body = JsonUtil.parseSimpleJson(readRequestBody(exchange));
                String token = extractToken(exchange, body);
                AUTH_SERVICE.logout(token);
                sendJson(exchange, 200, JsonUtil.successJson("Logged out"));
            } catch (Exception exception) {
                sendJson(exchange, 500, JsonUtil.errorJson(exception.getMessage() == null ? "Logout error" : exception.getMessage()));
            }
        }
    }

    private static final class MatchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendOptions(exchange);
                    return;
                }
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendJson(exchange, 405, JsonUtil.errorJson("Method not allowed"));
                    return;
                }
                Map<String, String> body = JsonUtil.parseSimpleJson(readRequestBody(exchange));
                String token = extractToken(exchange, body);
                if (AUTH_SERVICE.validate(token) == null) {
                    sendJson(exchange, 401, JsonUtil.errorJson("Authentication required"));
                    return;
                }
                String resume = body.getOrDefault("resume", "");
                String job = body.getOrDefault("job", "");
                if (resume == null || job == null || resume.isBlank() || job.isBlank()) {
                    sendJson(exchange, 400, JsonUtil.errorJson("Resume and job text are required"));
                    return;
                }
                Candidate candidate = new Candidate("C1", "User", resume);
                for (String skill : TextProcessor.extractSkills(resume)) {
                    candidate.addSkill(skill);
                }
                for (String keyword : TextProcessor.extractKeywords(resume)) {
                    candidate.addKeyword(keyword);
                }

                Job targetJob = new Job("J1", "Target Job", job);
                for (String skill : TextProcessor.extractSkills(job)) {
                    targetJob.addRequiredSkill(skill);
                }
                for (String keyword : TextProcessor.extractKeywords(job)) {
                    targetJob.addKeyword(keyword);
                }

                MatchingEngine engine = new MatchingEngine(List.of(candidate), List.of(targetJob));
                double rawScore = engine.score(candidate, targetJob);
                Set<String> resumeSkills = new TreeSet<>(candidate.getSkills());
                Set<String> jobSkills = new TreeSet<>(targetJob.getRequiredSkills());
                Set<String> matched = new TreeSet<>(resumeSkills);
                matched.retainAll(jobSkills);
                Set<String> missing = new TreeSet<>(jobSkills);
                missing.removeAll(resumeSkills);
                Set<String> extra = new TreeSet<>(resumeSkills);
                extra.removeAll(jobSkills);

                Map<String, Object> response = new LinkedHashMap<>();
                response.put("success", true);
                response.put("score", formatScore(rawScore));
                response.put("matchedSkills", new ArrayList<>(matched));
                response.put("missingSkills", new ArrayList<>(missing));
                response.put("extraSkills", new ArrayList<>(extra));
                sendJson(exchange, 200, JsonUtil.toJson(response));
            } catch (Exception exception) {
                sendJson(exchange, 500, JsonUtil.errorJson(exception.getMessage() == null ? "Match failed" : exception.getMessage()));
            }
        }
    }

    private static final class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendOptions(exchange);
                    return;
                }
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendJson(exchange, 405, JsonUtil.errorJson("Method not allowed"));
                    return;
                }
                Map<String, Object> payload = new LinkedHashMap<>();
                payload.put("status", "OK");
                sendJson(exchange, 200, JsonUtil.toJson(payload));
            } catch (Exception exception) {
                sendJson(exchange, 500, JsonUtil.errorJson("Health check failed"));
            }
        }
    }

    private static String extractToken(HttpExchange exchange, Map<String, String> body) {
        if (body != null) {
            String token = body.get("token");
            if (token != null && !token.isBlank()) {
                return token;
            }
        }
        String authorization = exchange.getRequestHeaders().getFirst("Authorization");
        if (authorization == null || authorization.isBlank()) {
            return "";
        }
        if (authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return authorization.substring(7).trim();
        }
        return authorization.trim();
    }

    private static void sendOptions(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(204, -1);
    }

    private static void sendJson(HttpExchange exchange, int status, String jsonText) throws IOException {
        byte[] body = jsonText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(body);
        }
    }

    private static void sendStatic(HttpExchange exchange, byte[] body, String contentType) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(body);
        }
    }

    private static void sendNotFound(HttpExchange exchange) throws IOException {
        String page = "<html><head><title>404</title><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head><body style=\"font-family:Arial,sans-serif;background:#0D0D0D;color:#FFFFFF;padding:2rem;\"><h1 style=\"color:#E6E6FA;\">404 Not Found</h1><p>The requested page could not be found.</p></body></html>";
        byte[] body = page.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(404, body.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(body);
        }
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        if (inputStream == null) {
            return "{}";
        }
        byte[] bytes = inputStream.readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static String sanitizePath(URI uri) {
        if (uri == null) {
            return "/";
        }
        String path = uri.getPath();
        if (path == null || path.isBlank()) {
            return "/";
        }
        return path;
    }

    private static String resolveResourcePath(String requestPath) {
        String clean = requestPath;
        if (clean == null || clean.isBlank()) {
            return "/web/index.html";
        }
        if (clean.indexOf('?') >= 0) {
            clean = clean.substring(0, clean.indexOf('?'));
        }
        if (clean.contains("..")) {
            return null;
        }
        String candidate = clean;
        if (!candidate.startsWith("/")) {
            candidate = "/" + candidate;
        }
        if (!candidate.startsWith("/web/")) {
            candidate = "/web" + candidate;
        }
        if (candidate.endsWith("/")) {
            candidate = candidate + "index.html";
        }
        if (candidate.indexOf('.', candidate.lastIndexOf('/')) < 0) {
            candidate = candidate + ".html";
        }
        return candidate;
    }

    private static byte[] readResource(String resourcePath) {
        try (InputStream inputStream = WebServer.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return null;
            }
            return inputStream.readAllBytes();
        } catch (IOException exception) {
            return null;
        }
    }

    private static String contentTypeFor(String resourcePath) {
        String lower = resourcePath.toLowerCase();
        if (lower.endsWith(".html")) {
            return "text/html; charset=utf-8";
        }
        if (lower.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (lower.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        }
        if (lower.endsWith(".json")) {
            return "application/json; charset=utf-8";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".ico")) {
            return "image/x-icon";
        }
        return "text/plain; charset=utf-8";
    }

    private static double formatScore(double score) {
        double clamped = Math.max(0.0, Math.min(1.0, score));
        return Math.round(clamped * 1000.0) / 1000.0;
    }

    private static void populateSet(Set<String> destination, Set<String> source) {
        if (destination == null || source == null) {
            return;
        }
        destination.addAll(source);
    }
}
