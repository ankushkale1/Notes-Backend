package com.note.backup.repo;

import com.note.pojo.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupNoteRepository extends JpaRepository<Note, Integer> {
}
