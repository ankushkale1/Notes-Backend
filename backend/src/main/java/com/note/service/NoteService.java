package com.note.service;

import com.note.exception.ResourceNotFoundException;
import com.note.pojo.Note;
import com.note.pojo.NoteInfo;
import com.note.pojo.Notebook;
import com.note.repo.NoteRepository;
import com.note.repo.NotebookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NoteService {
    @Autowired
    NoteRepository note_repo;

    @Autowired
    ImageProcessingService imageProcessingService;

    @Autowired
    NotebookRepository notebook_repo;

    @Transactional(readOnly = true)
    public List<Notebook> listNoteBooksOnly() {
        List<Notebook> notebooks = notebook_repo.findAll();
        List<NoteInfo> allNotes = notebook_repo.findAllNoteInfo();

        Map<Integer, List<NoteInfo>> notesByNotebook = allNotes.stream()
                .collect(Collectors.groupingBy(NoteInfo::getNotebook_id));

        for (Notebook notebook : notebooks) {
            notebook.setNotes(notesByNotebook.get(notebook.getNotebook_id()));
        }
        return notebooks;
    }

    @Transactional(readOnly = true)
    public Note getNoteDetails(Integer note_id) {
        Note note = note_repo.findById(note_id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + note_id));

        note.setNotebook_id(note.getNotebook().getNotebook_id());
        return note;
    }

    @Transactional
    public Note addUpdateNote(Note note) {
        // Ensure the notebook relationship is correctly managed
        if (note.getNotebook() == null && note.getNotebook_id() != null) {
            Notebook notebook = notebook_repo.findById(note.getNotebook_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Notebook not found with id: " + note.getNotebook_id()));
            note.setNotebook(notebook);
        }

        imageProcessingService.processNoteImages(note);
        Note res = note_repo.save(note);
        res.setNotebook_id(res.getNotebook().getNotebook_id());
        return res;
    }

    public void saveToBackup(Note note) {
        //backupService.saveNote(note);
    }

    @Transactional
    public Notebook addNotebook(Notebook notebook) {
        if (notebook.getParent() != null && notebook.getParent().getNotebook_id() != null) {
            Notebook parentNotebook = notebook_repo.getReferenceById(notebook.getParent().getNotebook_id());
            notebook.setParent(parentNotebook);
        } else {
            notebook.setParent(null);
        }
        return notebook_repo.save(notebook);
    }

    public void saveNotebookToBackup(Notebook notebook) {
        //backupService.saveNotebook(notebook);
    }

    public boolean deleteNote(Integer note_id) {
        if (!note_repo.existsById(note_id)) {
            throw new ResourceNotFoundException("Note not found with id: " + note_id);
        }
        note_repo.deleteById(note_id);
        //deleteNoteFromBackup(note_id);
        return true;
    }

    public void deleteNoteFromBackup(Integer note_id) {
        //backupService.deleteNote(note_id);
    }

    public boolean deleteNotebook(Integer notebook_id) {
        if (!notebook_repo.existsById(notebook_id)) {
            throw new ResourceNotFoundException("Notebook not found with id: " + notebook_id);
        }
        notebook_repo.deleteById(notebook_id);
        //deleteNotebookFromBackup(notebook_id);
        return true;
    }

    public void deleteNotebookFromBackup(Integer notebook_id) {
        //backupService.deleteNotebook(notebook_id);
    }

    @Transactional(readOnly = true)
    public Set<Note> searchNotes(String txt) {
        Set<Note> res = Collections.EMPTY_SET;
//        res = search.fuzzySearch(txt);
        return res;
    }
}