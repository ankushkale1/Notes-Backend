package com.note.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

import com.note.pojo.Note;
import com.note.pojo.Notebook;
import com.note.repo.NoteRepository;
import com.note.repo.NotebookRepository;

@Service
public class NoteService
{
	@Autowired
	NoteRepository note_repo;
	
	@Autowired
	NotebookRepository notebook_repo;
	
	public List<Notebook> listNoteBooks()
	{
		List<Notebook> res = Collections.EMPTY_LIST;
		res = notebook_repo.findAll();
		return res;
	}
	
	public Set<Note> getNotesInNotebook(String notebook)
	{
		Set<Note> res = Collections.EMPTY_SET;
		res = notebook_repo.findByNotebookname(notebook).getNotes();
		return res;
	}
	
	public Note addUpdateNote(Note note)
	{
		Note res = null;
		res = note_repo.save(note);
		return res;
	}
	
	public Notebook addNotebook(Notebook note)
	{
		Notebook res = null;
		res = notebook_repo.save(note);
		return res;
	}
	
	public boolean deleteNote(Integer note_id)
	{
		boolean res = true;
		note_repo.deleteById(note_id);
		return res;
	}
	
	public boolean deleteNotebook(Integer notebook_id)
	{
		boolean res = true;
		notebook_repo.deleteById(notebook_id);
		return res;
	}
	
	public List<Note> searchNotes(String txt)
	{
		List<Note> res = Collections.EMPTY_LIST;
		
		return res;
	}
}
