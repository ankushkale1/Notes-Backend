package com.note.service;

import com.note.pojo.Note;
import com.note.pojo.Notebook;
import com.note.repo.NoteRepository;
import com.note.repo.NotebookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataSyncService {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncService.class);

    @PersistenceContext(unitName = "backup") // Use the name from your config
    private EntityManager backupEntityManager;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private NotebookRepository notebookRepository;

    @Autowired
    private BackupService backupService;

    @EventListener(ApplicationReadyEvent.class)
    @Async
    @Transactional("backupTransactionManager")
    public void syncData() {
        logger.info("Starting data sync from MySQL to H2...");

        try {
            // 1. Sync Notebooks (Insert/Update)
            syncNotebooksUpsert();
            
            // 2. Sync Notes (Insert/Update)
            syncNotesUpsert();

            // 3. Cleanup Stale Notes (Delete)
            //cleanupStaleNotes();

            // 4. Cleanup Stale Notebooks (Delete)
            //cleanupStaleNotebooks();

            logger.info("Data sync completed successfully.");
        } catch (Exception e) {
            logger.error("Error during data sync", e);
        }
    }

    public void syncNotebooksUpsert() {
        logger.info("Upserting Notebooks...");
        List<Notebook> allNotebooks = notebookRepository.findAll(); // From MySQL

        // 1. Temporarily disable FK checks for bulk sync
        backupEntityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        try {
            for (Notebook mysqlNote : allNotebooks) {
                Notebook backup = new Notebook();
                backup.setNotebook_id(mysqlNote.getNotebook_id());
                backup.setNotebookname(mysqlNote.getNotebookname());
                backup.setCdate(mysqlNote.getCdate());
                backup.setUdate(mysqlNote.getUdate());

                // Create a stub parent with only the ID
                if (mysqlNote.getParent() != null) {
                    Notebook parentStub = new Notebook();
                    parentStub.setNotebook_id(mysqlNote.getParent().getNotebook_id());
                    backup.setParent(parentStub);
                }

                // 2. merge() detects existing IDs and prevents 1,2,3 overwrites
                backupEntityManager.merge(backup);
            }
            backupEntityManager.flush();
        } finally {
            // 3. Always re-enable integrity for data safety
            backupEntityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        }
    }

    public void syncNotesUpsert() {
        logger.info("Upserting Notes...");
        List<Note> notes = noteRepository.findAll();
        if (!notes.isEmpty()) {
            backupService.saveNotes(notes);
            logger.info("Upserted {} notes.", notes.size());
        }
    }

    public void cleanupStaleNotes() {
        logger.info("Cleaning up stale Notes...");
        List<Integer> mysqlIds = noteRepository.findAll().stream().map(Note::getNote_id).toList();
        List<Integer> backupIds = backupService.findAllNotes().stream().map(Note::getNote_id).toList();

        List<Integer> idsToDelete = backupIds.stream()
                .filter(id -> !mysqlIds.contains(id))
                .collect(Collectors.toList());

        if (!idsToDelete.isEmpty()) {
            backupService.deleteNotes(idsToDelete);
            logger.info("Deleted {} stale notes.", idsToDelete.size());
        } else {
            logger.info("No stale notes found.");
        }
    }

    public void cleanupStaleNotebooks() {
        logger.info("Cleaning up stale Notebooks...");
        List<Integer> mysqlIds = notebookRepository.findAll().stream().map(Notebook::getNotebook_id).toList();
        List<Integer> backupIds = backupService.findAllNotebooks().stream().map(Notebook::getNotebook_id).toList();

        List<Integer> idsToDelete = backupIds.stream()
                .filter(id -> !mysqlIds.contains(id))
                .collect(Collectors.toList());

        if (!idsToDelete.isEmpty()) {
            backupService.deleteNotebooks(idsToDelete);
            logger.info("Deleted {} stale notebooks.", idsToDelete.size());
        } else {
            logger.info("No stale notebooks found.");
        }
    }
}
