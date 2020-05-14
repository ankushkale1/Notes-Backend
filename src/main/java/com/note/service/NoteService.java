package com.note.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.databind.*;
import com.note.pojo.Note;
import com.note.pojo.Notebook;
import com.note.repo.NoteRepository;
import com.note.repo.NotebookRepository;

import io.github.biezhi.webp.WebpIO;

@Service
public class NoteService
{
	@Autowired
	NoteRepository note_repo;
	
	@Autowired
	SearchService search;
	
	@Autowired
	NotebookRepository notebook_repo;
	
	@Transactional(readOnly=true)
	public List<Notebook> listNoteBooksOnly()
	{
		List<Notebook> res = Collections.EMPTY_LIST;
		res = notebook_repo.getNotebooksOnly();
		
		for(Notebook notebook : res)
			for(Note note : notebook.getNotes())
				note.setJsonnotes("");
			
		return res;
	}
	
	@Transactional(readOnly=true)
	public Note getNoteDetails(Integer note_id)
	{
		Optional<Note> note = note_repo.findById(note_id);
		if(note.isPresent())
		{
			Notebook book = note.get().getNotebook(); //so that we get notebook id
			note.get().setNotebook_id(book.getNotebook_id());
			return note.get();
		}
		else
			return new Note();
	}
	
	public Note addUpdateNote(Note note)
	{
		Note res = null;
		res = note_repo.save(note);
		res.setNotebook_id(res.getNotebook().getNotebook_id());
		//convertImages(note);
		return res;
	}
	
	public Notebook addNotebook(Notebook note)
	{
		Notebook res = null;
		if(note.getParent() != null) //i.e parent passed
		{
			Optional<Notebook> parent = notebook_repo.findById(note.getParent().getNotebook_id());
			note.setParent(parent.isPresent() ? parent.get() : null);
		}
			
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
	
	@Transactional(readOnly=true)
	public Set<Note> searchNotes(String txt)
	{
		Set<Note> res = Collections.EMPTY_SET;
		res = search.fuzzySearch(txt);
		return res;
	}
}
