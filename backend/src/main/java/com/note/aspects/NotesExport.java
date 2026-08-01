package com.note.aspects;

import com.note.pojo.Note;
import org.apache.commons.io.FileUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.io.File;

@Aspect
@Component
public class NotesExport {
    final static String NOTE_PATH = "JSON_NOTES";

    /**
     * After a note is successfully saved, export its JSON content to a file.
     */
    @AfterReturning(pointcut = "execution(* com.note.service.NoteService.addUpdateNote(..))", returning = "savedNote")
    public void exportNoteToFile(Note savedNote) {
        if (savedNote == null || savedNote.getNotename() == null || savedNote.getNotebook() == null) {
            return;
        }

        try {
            File notebookDir = new File(NOTE_PATH, savedNote.getNotebook().getNotebookname());
            if (!notebookDir.exists()) {
                notebookDir.mkdirs();
            }
            File noteFile = new File(notebookDir, savedNote.getNotename() + ".json");
            FileUtils.writeStringToFile(noteFile, savedNote.getJsonnotes(), "UTF-8");
        } catch (Exception e) {
            // Log the error properly instead of swallowing it
            System.err.println("Failed to export note to file: " + e.getMessage());
        }
    }
}