package com.note.pojo;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Notebook
{
	@Id
	@GeneratedValue
	Integer notebook_id;
	
	@NotBlank
	String notebookname;
	
	@CreationTimestamp
	LocalDateTime cdate;
	
	@UpdateTimestamp
	LocalDateTime udate;
	
	@OneToMany(mappedBy="notebook",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JsonManagedReference
	Set<Note> notes;
	
	public Notebook()
	{
		
	}

	public Integer getNotebook_id()
	{
		return notebook_id;
	}

	public void setNotebook_id(Integer notebook_id)
	{
		this.notebook_id = notebook_id;
	}

	public String getNotebookname()
	{
		return notebookname;
	}

	public void setNotebookname(String notebookname)
	{
		this.notebookname = notebookname;
	}

	public LocalDateTime getCdate()
	{
		return cdate;
	}

	public void setCdate(LocalDateTime cdate)
	{
		this.cdate = cdate;
	}

	public LocalDateTime getUdate()
	{
		return udate;
	}

	public void setUdate(LocalDateTime udate)
	{
		this.udate = udate;
	}

	public Set<Note> getNotes()
	{
		return notes;
	}

	public void setNotes(Set<Note> notes)
	{
		this.notes = notes;
	}
}
