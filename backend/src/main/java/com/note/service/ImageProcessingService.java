package com.note.service;

import com.note.pojo.Note;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import javax.imageio.ImageIO;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.StreamSupport;

@Service
public class ImageProcessingService {

    static {
        // Prioritize in-memory processing to avoid disk I/O limits on embedded systems.
        ImageIO.setUseCache(false);
    }

    private static final Logger log = LoggerFactory.getLogger(ImageProcessingService.class);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    private final ObjectMapper objectMapper;
    private final ExecutorService virtualThreadExecutor;

    public ImageProcessingService(ObjectMapper objectMapper, @Qualifier("virtualThreadExecutor") ExecutorService virtualThreadExecutor) {
        this.objectMapper = objectMapper;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    public void processNoteImages(Note note) {
        if (note == null || note.getJsonnotes() == null || note.getJsonnotes().isBlank()) {
            return;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(note.getJsonnotes());
            ArrayNode opsArray = (ArrayNode) rootNode.path("ops");
            if (opsArray.isMissingNode()) return;

            List<CompletableFuture<Void>> futures = StreamSupport.stream(opsArray.spliterator(), false)
                    .map(opNode -> (ObjectNode) opNode.path("insert"))
                    .filter(insertNode -> !insertNode.isMissingNode() && insertNode.has("image"))
                    .map(insertNode -> {
                        String originalUrl = insertNode.get("image").asText();
                        return CompletableFuture.supplyAsync(() -> processSingleImage(originalUrl), virtualThreadExecutor)
                                .thenAccept(newUrl -> {
                                    if (newUrl != null && !newUrl.equals(originalUrl)) {
                                        // Synchronize write-back to the shared JSON structure.
                                        synchronized (insertNode) {
                                            insertNode.put("image", newUrl);
                                        }
                                    }
                                })
                                .exceptionally(ex -> {
                                    log.error("Image processing failed for '{}': {}", originalUrl, ex.getMessage());
                                    return null; // Gracefully complete the future.
                                });
                    })
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            note.setJsonnotes(objectMapper.writeValueAsString(rootNode));

        } catch (Exception e) {
            log.error("Failed to parse or process note JSON for note ID {}", note.getNote_id(), e);
        }
    }

    private String processSingleImage(String imageUrl) {
        if (imageUrl.startsWith("http")) {
            return downloadAndConvertToWebP(imageUrl);
        } else if (imageUrl.startsWith("data:image") && !isWebpOrUnsupported(imageUrl)) {
            return convertToWebP(imageUrl);
        }
        return imageUrl; // Return original if no processing is needed.
    }

    private String downloadAndConvertToWebP(String imageUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(imageUrl)).GET().build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                String contentType = response.headers().firstValue("Content-Type").orElse("image/jpeg");
                if (isWebpOrUnsupported("data:" + contentType)) {
                    return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(response.body());
                }
                return convertToWebP("data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(response.body()));
            }
            log.warn("Failed to download image from {}. Status: {}", imageUrl, response.statusCode());
        } catch (Exception e) {
            log.error("Error downloading image from {}: {}", imageUrl, e.getMessage());
        }
        return imageUrl; // Fallback to original URL on failure.
    }

    private String convertToWebP(String base64Image) {
        try {
            String base64Data = base64Image.substring(base64Image.indexOf(",") + 1);
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            ImmutableImage image = ImmutableImage.loader().fromBytes(imageBytes);
            byte[] webpBytes = image.bytes(WebpWriter.DEFAULT);

            return "data:image/webp;base64," + Base64.getEncoder().encodeToString(webpBytes);
        } catch (Exception e) {
            log.error("Failed to convert to WebP: {}", e.getMessage());
            return base64Image; // Fallback to original on conversion failure.
        }
    }

    private boolean isWebpOrUnsupported(String dataUrl) {
        return dataUrl.contains("image/webp") || dataUrl.contains("image/gif") || dataUrl.contains("image/svg");
    }
}