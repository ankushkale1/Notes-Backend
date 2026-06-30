package com.note.service;

import com.note.pojo.Note;
import com.note.pojo.Notebook;
import com.note.repo.NoteRepository;
import com.note.repo.NotebookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Collections;

@Service
public class NoteService {
    @Autowired
    NoteRepository note_repo;

    //@Autowired
    //BackupService backupService;

//    @Autowired
//    SearchService search;

    @Autowired
    NotebookRepository notebook_repo;

    @Transactional(readOnly = true)
    public List<Notebook> listNoteBooksOnly() {
        List<Notebook> notebooks = notebook_repo.findAllWithSubNotebooks();
        for (Notebook notebook : notebooks) {
            notebook.setNotes(notebook_repo.findNoteInfoByNotebookId(notebook.getNotebook_id()));
        }
        return notebooks;
    }

    @Transactional(readOnly = true)
    public Note getNoteDetails(Integer note_id) {
        Optional<Note> note = note_repo.findById(note_id);
        if (note.isPresent()) {
            Notebook book = note.get().getNotebook(); //so that we get notebook id
            note.get().setNotebook_id(book.getNotebook_id());
            return note.get();
        } else
            return new Note();
    }

    public Note addUpdateNote(Note note) {
        Note res = null;
        res = note_repo.save(note);
        res.setNotebook_id(res.getNotebook().getNotebook_id());
        //convertImages(note);
        //saveToBackup(res);
        return res;
    }

    public void saveToBackup(Note note) {
        //backupService.saveNote(note);
    }

    public Notebook addNotebook(Notebook note) {
        Notebook res = null;
        if (note.getParent() != null) //i.e parent passed
        {
            Optional<Notebook> parent = notebook_repo.findById(note.getParent().getNotebook_id());
            note.setParent(parent.isPresent() ? parent.get() : null);
        }

        res = notebook_repo.save(note);
        //saveNotebookToBackup(res);
        return res;
    }

    public void saveNotebookToBackup(Notebook notebook) {
        //backupService.saveNotebook(notebook);
    }

    public boolean deleteNote(Integer note_id) {
        boolean res = true;
        note_repo.deleteById(note_id);
        //deleteNoteFromBackup(note_id);
        return res;
    }

    public void deleteNoteFromBackup(Integer note_id) {
        //backupService.deleteNote(note_id);
    }

    public boolean deleteNotebook(Integer notebook_id) {
        boolean res = true;
        notebook_repo.deleteById(notebook_id);
        //deleteNotebookFromBackup(notebook_id);
        return res;
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