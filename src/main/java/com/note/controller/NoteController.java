package com.note.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com.note.pojo.*;
import com.note.service.*;

@RestController
@RequestMapping("/notes")
public class NoteController
{
	@Autowired
	NoteService service;
	
	@GetMapping("getNoteBooks")
	public List<Notebook> listNoteBooks()
	{
		List<Notebook> res = Collections.EMPTY_LIST;
		res = service.listNoteBooksOnly();
		return res;
	}
	
	@GetMapping("getNote/{noteid}")
	public Note getNote(@PathVariable("noteid") Integer noteid)
	{
		return service.getNoteDetails(noteid);
	}
	
	@PostMapping("addNote")
	public Note addUpdateNote(@RequestBody Note note)
	{
		Note res = null;
		res = service.addUpdateNote(note);
		return res;
	}
	
	@PostMapping("addNotebook")
	public Notebook addNoteBook(@RequestBody Notebook book)
	{
		Notebook res = null;
		res = service.addNotebook(book);
		return res;
	}
	
	@GetMapping("deleteNote/{noteid}")
	public boolean deleteNote(@PathVariable("noteid") Integer note_id)
	{
		boolean res = false;
		res = service.deleteNote(note_id);
		return res;
	}
	
	@GetMapping("deleteNotebook/{notebookid}")
	public boolean deleteNotebook(@PathVariable("notebookid") Integer notebook_id)
	{
		boolean res = false;
		res = service.deleteNotebook(notebook_id);
		return res;
	}
	
	@GetMapping("search/{searchtxt}")
	public Set<Note> searchNotes(@PathVariable("searchtxt") String txt)
	{
		Set<Note> res = Collections.EMPTY_SET;
		res = service.searchNotes(txt);
		return res;
	}
}
