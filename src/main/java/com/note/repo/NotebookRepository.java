package com.note.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.note.pojo.Note;
import com.note.pojo.Notebook;
import java.util.*;

public interface NotebookRepository extends JpaRepository<Notebook, Integer>
{
	public Notebook findByNotebookname(String name);
}
