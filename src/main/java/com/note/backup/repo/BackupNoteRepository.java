package com.note.backup.repo;

import com.note.pojo.Note;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
        name = "backup.db.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public interface BackupNoteRepository extends JpaRepository<Note, Integer> {
}
