package com.note.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.note.pojo.Note;
import com.note.pojo.Notebook;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Integer>
{
    public Notebook findByNotebookname(String name);

    @Query(value = "select * from notebook", nativeQuery = true)
    public List<Notebook> getNotebooksOnly();
}
