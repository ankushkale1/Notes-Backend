package com.note.service;

import com.note.pojo.Note;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Service
public class ImageProcessingService {

    private static final Logger log = LoggerFactory.getLogger(ImageProcessingService.class);

    // Modern HTTP Client - Thread-safe, reuses connections, prevents hung sockets
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    // Jackson is thread-safe for reading, and the standard in Spring Boot
    private final ObjectMapper objectMapper;

    public ImageProcessingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void processNoteImages(Note note) {
        if (note == null || note.getJsonnotes() == null || note.getJsonnotes().isBlank()) {
            return;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(note.getJsonnotes());
            JsonNode opsNode = rootNode.path("ops");

            if (!opsNode.isArray()) return;
            ArrayNode opsArray = (ArrayNode) opsNode;

            // Process images concurrently using CompletableFuture
            List<CompletableFuture<Void>> processingTasks = IntStream.range(0, opsArray.size())
                    .mapToObj(i -> {
                        JsonNode opNode = opsArray.get(i);
                        JsonNode insertNode = opNode.path("insert");

                        if (insertNode.isObject() && insertNode.has("image")) {
                            String originalImageUrl = insertNode.get("image").asText();

                            return CompletableFuture.supplyAsync(() -> processSingleImage(originalImageUrl))
                                    .thenAccept(processedImage -> {
                                        if (processedImage != null && !processedImage.equals(originalImageUrl)) {
                                            // Synchronize ONLY the mutation of the shared JSON tree
                                            synchronized (opsArray) {
                                                ((ObjectNode) insertNode).put("image", processedImage);
                                            }
                                        }
                                    })// Add this block to isolate failures to the individual thread
                                    .exceptionally(ex -> {
                                        log.error("Fatal async pipeline error for image. Keeping original. Error: {}", ex.getMessage(), ex);
                                        // Returning null resolves the future successfully, leaving the original JSON node untouched.
                                        return null;
                                    });
                        }
                        return CompletableFuture.completedFuture((Void) null);
                    })
                    .toList();

            // Wait for all Radxa CPU cores to finish the WebP conversions
            CompletableFuture.allOf(processingTasks.toArray(new CompletableFuture[0])).join();

            note.setJsonnotes(objectMapper.writeValueAsString(rootNode));

        } catch (Exception e) {
            log.error("Failed to parse or process note JSON", e);
        }
    }

    private String processSingleImage(String imageUrl) {
        String currentImage = imageUrl;

        if (currentImage.startsWith("http")) {
            String downloaded = downloadImageAsBase64(currentImage);
            if (downloaded != null) {
                currentImage = downloaded;
            }
        }

        if (currentImage.startsWith("data:image") && !currentImage.contains("webp") && !currentImage.contains("gif")) {
            return convertToWebP(currentImage);
        }

        return currentImage;
    }

    private String convertToWebP(String base64Image) {
        try {
            int commaIndex = base64Image.indexOf(",");
            if (commaIndex == -1) return base64Image;

            String base64Data = base64Image.substring(commaIndex + 1);
            byte[] rawBytes = Base64.getDecoder().decode(base64Data);

            // Scrimage 4.6.5 automatically handles the aarch64 native execution
            ImmutableImage image = ImmutableImage.loader().fromBytes(rawBytes);
            byte[] webpBytes = image.bytes(WebpWriter.DEFAULT);

            return "data:image/webp;base64," + Base64.getEncoder().encodeToString(webpBytes);

        } catch (Exception e) {
            log.error("Failed to convert image to WebP using Scrimage on Radxa", e);
            return base64Image; // Failsafe fallback
        }
    }

    private String downloadImageAsBase64(String imageUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                String contentType = response.headers().firstValue("Content-Type").orElse("image/jpeg");
                return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(response.body());
            } else {
                log.warn("Failed to download external image. HTTP Status: {}", response.statusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Network error while downloading image from: {}", imageUrl, e);
            return null;
        }
    }
}