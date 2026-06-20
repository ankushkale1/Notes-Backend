package com.note.aspects;

import tools.jackson.databind.ObjectMapper;
import com.note.pojo.Note;
import io.github.biezhi.webp.WebpIO;
import org.apache.commons.io.FileUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Aspect
@Component
public class NotesExport {
    final static String NOTE_PATH = "JSON_NOTES";

    @Autowired
    ObjectMapper objectMapper;

    @AfterReturning(pointcut = "execution(* com.note.service.*.addUpdateNote(*))"
            , returning = "note")
    public void exportNote(Note note) {
        try {
            IO.println("Called before save / update Note: " + note.getNotename());
            new File(NOTE_PATH + "/" + note.getNotebook().getNotebookname()).mkdirs();
            FileUtils.write(
                    new File(NOTE_PATH + "/" + note.getNotebook().getNotebookname()
                            + "/" + note.getNotename() + ".json"),
                    note.getJsonnotes());
        } catch (Exception e) {
        }
    }

    @Before("execution(* com.note.service.*.addUpdateNote(*))")
    public void convertImages(JoinPoint point) {
        try {
            Object[] args = point.getArgs();
            Note note = (Note) args[0];
            IO.println("Converting Images Note: " + note.getNotename());
            //convertImages(note);
            Map<String, Object> quillDeltaJson = objectMapper.readValue(note.getJsonnotes(), Map.class);
            processAndSaveNote(quillDeltaJson);
            note.setJsonnotes(objectMapper.writeValueAsString(quillDeltaJson));
        } catch (Exception e) {
        }
    }

    @AfterReturning(pointcut = "execution(* com.note.service.NoteService.*(..))"
            , returning = "result")
    public void logNotesOps(JoinPoint joinPoint, Object result) {
        IO.println("Executed for every note service method..");
        //System.out.println("Method: "+joinPoint.getSignature().getName()
        //+" Args: "+joinPoint.getArgs()
        //+" Return Value: "+result);
    }

    public void convertImages(Note note) {
        JSONObject jobj = new JSONObject(note.getJsonnotes());
        JSONArray arr = jobj.getJSONArray("ops");

        for (int i = 0; i < arr.length(); i++) {
            JSONObject op = (JSONObject) arr.get(i);

            if (op.get("insert") instanceof JSONObject image) {

                if ((image.getString("image") != null) &&
                        (image.getString("image").indexOf("base64") > -1)) //i.e this is an image node & have base64 data
                {
                    //System.out.println("Image length: "+image.getString("image").length());
                    String img_src = image.getString("image");
                    String img_type = image.getString("image").substring(0, image.getString("image").indexOf(";"));
                    String ext = img_type.substring(img_type.indexOf("/") + 1);
                    //System.out.println("Img type: "+img_type+" File Ext: "+ext);

                    if (!img_type.trim().contains("webp") && !img_type.trim().contains("gif")) {
                        try {
                            File src = File.createTempFile(note.getNote_id() + "_" + i, null);
                            OutputStream out = new FileOutputStream(src);

                            int idx = img_src.indexOf(", ") > -1 ? 2 : 1; //ie space is there then skip it

                            String b64 = img_src.substring(img_src.indexOf(",") + idx);
                            byte[] rdata = Base64.getDecoder().decode(b64);
                            out.write(rdata);
                            out.flush();

                            File dest = File.createTempFile(note.getNote_id() + "_" + i + "_webp", null);

                            WebpIO.create().toWEBP(src, dest);

                            byte[] wbytes = FileUtils.readFileToByteArray(dest);
                            String b64_w = Base64.getEncoder().encodeToString(wbytes);

                            //System.out.println("Outlength: "+b64_w.length());

                            image.put("image", "data:image/webp;base64," + b64_w);

                            src.delete();
                            dest.delete();
                        } catch (Exception e) {
                            e.printStackTrace(System.out);
                        }
                    }
                }
            }
        }

        note.setJsonnotes(jobj.toString());
    }

    // Method to process Quill Delta and save the note
    public String processAndSaveNote(Map<String, Object> quillDeltaJson) {
        // Fetch ops list from Quill Delta JSON
        List<Map<String, Object>> ops = (List<Map<String, Object>>) quillDeltaJson.get("ops");

        List<Map<String, Object>> updatedOps = new ArrayList<>();

        // Iterate over the Delta ops to find images
        for (Map<String, Object> op : ops) {
            if (op.containsKey("insert")) {
                Object insertValue = op.get("insert");

                if (insertValue instanceof LinkedHashMap) {
                    LinkedHashMap<String, Object> insertMap = (LinkedHashMap<String, Object>) insertValue;

                    if (insertMap.containsKey("image")) {
                        String imageUrl = (String) insertMap.get("image");

                        // Check if it's an external image (starts with http)
                        if (imageUrl.startsWith("http")) {
                            try {
                                // Convert external image to Base64
                                String base64Image = convertImageToBase64(imageUrl);
                                insertMap.put("image", base64Image); // Replace URL with Base64
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            // Add the updated op to the list
            updatedOps.add(op);
        }

        // Now you have updated ops with base64 images. Save the note (this could be a DB save operation).
        // In this example, we will just print the updated JSON
        quillDeltaJson.put("ops", updatedOps);
        IO.println("Updated Quill Delta JSON with Base64 images: " + quillDeltaJson);

        // Return success message (you can save this to a database if needed)
        return "Note saved successfully with Base64 images.";
    }

    // Convert an image from URL to Base64 format
    private String convertImageToBase64(String imageUrl) throws Exception {
        IO.println(">> Converting Image to Base64..." + imageUrl);
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(2000);  // Timeout for establishing connection
        connection.setReadTimeout(5000);     // Timeout for reading data
        String contentType = connection.getContentType();
        try (InputStream is = connection.getInputStream()) {
            byte[] imageBytes = is.readAllBytes();
            IO.println(">> Done Converting Image to Base64..." + imageUrl);
            return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
        }
    }
}
