package com.note.repo;

import com.note.pojo.NoteInfo;
import com.note.pojo.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Integer> {
    Notebook findByNotebookname(String name);

    @Query("SELECT n FROM Notebook n")
    List<Notebook> findAllWithSubNotebooks();

    @Query("SELECT new com.note.pojo.NoteInfo(n.note_id, n.notename) FROM Note n WHERE n.notebook.notebook_id = :notebookId")
    List<NoteInfo> findNoteInfoByNotebookId(Integer notebookId);
}