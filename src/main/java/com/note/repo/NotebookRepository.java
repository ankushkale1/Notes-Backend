package com.note.repo;

import com.note.pojo.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Integer> {
    Notebook findByNotebookname(String name);

    @NativeQuery("select * from notebook")
    List<Notebook> getNotebooksOnly();
}
