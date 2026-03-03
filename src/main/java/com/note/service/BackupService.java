package com.note.service;

import com.note.backup.repo.BackupNoteRepository;
import com.note.backup.repo.BackupNotebookRepository;
import com.note.pojo.Note;
import com.note.pojo.Notebook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BackupService {

    @Autowired
    private BackupNoteRepository backupNoteRepository;

    @Autowired
    private BackupNotebookRepository backupNotebookRepository;

    @Transactional("backupTransactionManager")
    public void saveNote(Note note) {
        backupNoteRepository.save(note);
    }

    @Transactional("backupTransactionManager")
    public void saveNotes(List<Note> notes) {
        backupNoteRepository.saveAll(notes);
    }

    @Transactional("backupTransactionManager")
    public void deleteNote(Integer id) {
        backupNoteRepository.deleteById(id);
    }

    @Transactional("backupTransactionManager")
    public void deleteNotes(List<Integer> ids) {
        backupNoteRepository.deleteAllById(ids);
    }

    @Transactional("backupTransactionManager")
    public void saveNotebook(Notebook notebook) {
        backupNotebookRepository.save(notebook);
    }

    @Transactional("backupTransactionManager")
    public void saveNotebooks(List<Notebook> notebooks) {
        backupNotebookRepository.saveAll(notebooks);
    }

    @Transactional("backupTransactionManager")
    public void deleteNotebook(Integer id) {
        backupNotebookRepository.deleteById(id);
    }

    @Transactional("backupTransactionManager")
    public void deleteNotebooks(List<Integer> ids) {
        backupNotebookRepository.deleteAllById(ids);
    }
    
    @Transactional(value = "backupTransactionManager", readOnly = true)
    public List<Note> findAllNotes() {
        return backupNoteRepository.findAll();
    }

    @Transactional(value = "backupTransactionManager", readOnly = true)
    public List<Notebook> findAllNotebooks() {
        return backupNotebookRepository.findAll();
    }
}
