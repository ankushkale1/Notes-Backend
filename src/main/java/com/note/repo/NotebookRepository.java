package com.note.repo;

import com.note.pojo.Note;
import com.note.pojo.Notebook;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface NotebookRepository extends MongoRepository<Notebook, String>
{
    public Notebook findByNotebookname(String name);

//    @Query(value = "select * from notebook", nativeQuery = true)
//    public List<Notebook> getNotebooksOnly();
}
