package com.vincent.cpu.gui;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.annotation.JsonInclude;

public class ApiHelper {
    private static final String BASE_URL = "http://127.0.0.1:8001/run";
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)  // Force HTTP/1.1 to avoid upgrade issues
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static RunResponse runSchedule(RunRequest req) throws Exception {
        try {
            String requestBody = mapper.writeValueAsString(req);
            System.out.println("Sending request: " + requestBody); // Debug log
            System.out.println("Request length: " + requestBody.length() + " bytes"); // Debug log
            
            // Convert to bytes explicitly to ensure proper encoding
            byte[] bodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
            System.out.println("Body bytes: " + bodyBytes.length + " bytes"); // Debug log
            
            // Use byte array publisher with proper headers
            // Note: Don't include charset in Content-Type for FastAPI compatibility
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("User-Agent", "Java-HttpClient")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes))
                    .build();
            
            System.out.println("Request URI: " + request.uri()); // Debug log
            System.out.println("Request method: " + request.method()); // Debug log
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response status: " + response.statusCode()); // Debug log
            System.out.println("Response body: " + response.body()); // Debug log
            if (response.statusCode() != 200) {
                // Try to parse error response
                String errorMsg = response.body();
                try {
                    JsonNode errorNode = mapper.readTree(errorMsg);
                    if (errorNode.has("detail")) {
                        JsonNode detailNode = errorNode.get("detail");
                        if (detailNode.isArray()) {
                            // FastAPI validation errors are arrays
                            StringBuilder sb = new StringBuilder();
                            for (JsonNode item : detailNode) {
                                if (sb.length() > 0) sb.append("; ");
                                String loc = item.has("loc") ? item.get("loc").toString() : "";
                                String msg = item.has("msg") ? item.get("msg").asText() : "";
                                sb.append(loc).append(": ").append(msg);
                            }
                            errorMsg = sb.toString();
                        } else if (detailNode.isTextual()) {
                            errorMsg = detailNode.asText();
                        } else {
                            errorMsg = detailNode.toString();
                        }
                    }
                } catch (Exception e) {
                    // If parsing fails, use raw body
                }
                throw new Exception("Backend error: " + errorMsg);
            }
            return mapper.readValue(response.body(), RunResponse.class);
        } catch (java.net.ConnectException e) {
            throw new Exception("Cannot connect to backend server. Make sure the backend is running on http://127.0.0.1:8001");
        } catch (java.net.http.HttpTimeoutException e) {
            throw new Exception("Backend request timed out. The server may be slow or unresponsive.");
        } catch (java.io.IOException e) {
            throw new Exception("Network error: " + e.getMessage());
        } catch (Exception e) {
            // Re-throw with better error message if it's not already formatted
            if (e.getMessage() != null && e.getMessage().startsWith("Backend error: ")) {
                throw e;
            }
            if (e.getMessage() != null && e.getMessage().startsWith("Cannot connect")) {
                throw e;
            }
            // Log the full exception for debugging
            e.printStackTrace();
            throw new Exception("Error communicating with backend: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }
    }
}
