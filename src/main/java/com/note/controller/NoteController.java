package com.note.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<List<Notebook>> listNoteBooks()
	{
		List<Notebook> res = Collections.EMPTY_LIST;
		res = service.listNoteBooksOnly();
		return ResponseEntity.ok(res);
	}
	
	@GetMapping("getNote/{noteid}")
	public ResponseEntity<Note> getNote(@PathVariable("noteid") Integer noteid)
	{
		return ResponseEntity.ok(service.getNoteDetails(noteid));
	}
	
	@PostMapping("addNote")
	public ResponseEntity<Note> addUpdateNote(@RequestBody Note note)
	{
		Note res = null;
		res = service.addUpdateNote(note);
		return ResponseEntity.ok(res);
	}
	
	@PostMapping("addNotebook")
	public ResponseEntity<Notebook> addNoteBook(@RequestBody Notebook book)
	{
		Notebook res = null;
		res = service.addNotebook(book);
		return ResponseEntity.ok(res);
	}
	
	@GetMapping("deleteNote/{noteid}")
	public ResponseEntity<Boolean> deleteNote(@PathVariable("noteid") Integer note_id)
	{
		boolean res = false;
		res = service.deleteNote(note_id);
		return ResponseEntity.ok(res);
	}
	
	@GetMapping("deleteNotebook/{notebookid}")
	public ResponseEntity<Boolean> deleteNotebook(@PathVariable("notebookid") Integer notebook_id)
	{
		boolean res = false;
		res = service.deleteNotebook(notebook_id);
		return ResponseEntity.ok(res);
	}
	
	@GetMapping("search/{searchtxt}")
	public ResponseEntity<Set<Note>> searchNotes(@PathVariable("searchtxt") String txt)
	{
		Set<Note> res = Collections.EMPTY_SET;
		res = service.searchNotes(txt);
		return ResponseEntity.ok(res);
	}
}
