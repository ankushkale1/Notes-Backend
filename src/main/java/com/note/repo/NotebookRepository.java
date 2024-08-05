package com.note.repo;

import com.note.pojo.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Integer> {
    Notebook findByNotebookname(String name);

    @Query(value = "select * from notebook", nativeQuery = true)
    List<Notebook> getNotebooksOnly();
}
