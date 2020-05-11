package com.note.pojo;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
	
	@Column(updatable = false)
	@CreationTimestamp
	LocalDateTime cdate;
	
	@UpdateTimestamp
	LocalDateTime udate;
	
	@OneToMany(mappedBy="notebook",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JsonManagedReference
	Set<Note> notes;
	
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne
	//@JsonIgnore
	@JoinColumn(name="parent_notebook_id")
	@JsonBackReference
	Notebook parent = null;
	
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
	@OneToMany(mappedBy = "parent",fetch = FetchType.EAGER)
	List<Notebook> sub_notebooks;
	
	public Notebook()
	{
		
	}

	public Notebook getParent()
	{
		return parent;
	}

	public void setParent(Notebook parent)
	{
		this.parent = parent;
	}

	public List<Notebook> getSub_notebooks()
	{
		return sub_notebooks;
	}

	public void setSub_notebooks(List<Notebook> sub_notebooks)
	{
		this.sub_notebooks = sub_notebooks;
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
