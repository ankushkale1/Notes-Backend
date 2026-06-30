package com.note.repo;

import com.note.pojo.Notebook;
import com.note.pojo.NotebookInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Integer> {
    Notebook findByNotebookname(String name);

    @Query("SELECT new com.note.pojo.NotebookInfo(n.notebook_id, n.notebookname, n.cdate, n.udate) FROM Notebook n")
    List<NotebookInfo> findNotebooksOnly();
}