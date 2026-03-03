package com.note.backup.repo;

import com.note.pojo.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupNotebookRepository extends JpaRepository<Notebook, Integer> {
}
