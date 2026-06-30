package com.note.service;

import com.note.pojo.Note;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.stream.IntStream;

@Service
public class ImageProcessingService {

    /**
     * Processes a Note object to convert images to WebP and embed external images.
     * This is the main entry point for image processing.
     */
    public void processNoteImages(Note note) {
        if (note == null || note.getJsonnotes() == null || note.getJsonnotes().isEmpty()) {
            return;
        }

        try {
            JSONObject jobj = new JSONObject(note.getJsonnotes());
            JSONArray ops = jobj.optJSONArray("ops");
            if (ops == null) return;

            IntStream.range(0, ops.length())
                    .parallel() // Use a parallel stream for concurrent processing
                    .forEach(i -> {
                        JSONObject op = ops.optJSONObject(i);
                        if (op == null) return;

                        JSONObject insert = op.optJSONObject("insert");
                        if (insert != null && insert.has("image")) {
                            String imageUrl = insert.getString("image");

                            if (imageUrl.startsWith("http")) {
                                imageUrl = convertExternalImageToBase64(imageUrl);
                            }

                            if (imageUrl != null && imageUrl.startsWith("data:image") && !imageUrl.contains("webp")) {
                                String webpImage = convertToWebP(imageUrl);
                                // JSONObject is thread-safe, so this is fine
                                insert.put("image", webpImage);
                            }
                        }
                    });

            note.setJsonnotes(jobj.toString());
        } catch (Exception e) {
            System.err.println("Error processing note images: " + e.getMessage());
        }
    }

    private String convertToWebP(String base64Image) {
        String imgType = base64Image.substring(0, base64Image.indexOf(";"));
        if (imgType.contains("gif")) {
            return base64Image; // Don't convert GIFs
        }

        try {
            String b64 = base64Image.substring(base64Image.indexOf(",") + 1);
            byte[] rdata = Base64.getDecoder().decode(b64);

            ImmutableImage image = ImmutableImage.loader().fromBytes(rdata);
            byte[] webpBytes = image.bytes(WebpWriter.DEFAULT);

            return "data:image/webp;base64," + Base64.getEncoder().encodeToString(webpBytes);
        } catch (Exception e) {
            System.err.println("Failed to convert image to WebP: " + e.getMessage());
            return base64Image; // Return original on failure
        }
    }

    private String convertExternalImageToBase64(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(5000);
            String contentType = connection.getContentType();

            try (InputStream is = connection.getInputStream()) {
                byte[] imageBytes = is.readAllBytes();
                return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
            }
        } catch (Exception e) {
            System.err.println("Failed to download external image: " + e.getMessage());
            return null; // Return null on failure
        }
    }
}