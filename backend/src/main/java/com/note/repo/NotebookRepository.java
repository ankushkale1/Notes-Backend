package com.note.repo;

import com.note.pojo.NoteInfo;
import com.note.pojo.Notebook;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Integer> {
    Notebook findByNotebookname(String name);

    @EntityGraph(value = "Notebook.withSubNotebooks")
    List<Notebook> findAll();

    @Query("SELECT new com.note.pojo.NoteInfo(n.note_id, n.notename, n.notebook.notebook_id) FROM Note n")
    List<NoteInfo> findAllNoteInfo();
}