package com.note.pojo;

import java.time.LocalDateTime;
import java.util.Set;

import javax.annotation.Generated;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Note
{
	@Id
	@GeneratedValue
	Integer note_id;
	
	@NotBlank
	String notename;
	
	@NotBlank
	@Lob
	String jsonnotes;
	
	@CreationTimestamp
	LocalDateTime cdate;
	
	@UpdateTimestamp
	LocalDateTime udate;
	
	@NotEmpty
	@ElementCollection(targetClass=String.class)
	Set<String> keywords;
	
	@ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="notebook_id", nullable=false)
	@JsonBackReference
	Notebook notebook;
	
	public Note()
	{
		
	}

	public Notebook getNotebook()
	{
		return notebook;
	}

	public void setNotebook(Notebook notebook)
	{
		this.notebook = notebook;
	}

	public Integer getNote_id()
	{
		return note_id;
	}

	public void setNote_id(Integer note_id)
	{
		this.note_id = note_id;
	}

	public String getNotename()
	{
		return notename;
	}

	public void setNotename(String notename)
	{
		this.notename = notename;
	}

	public String getJsonnotes()
	{
		return jsonnotes;
	}

	public void setJsonnotes(String jsonnotes)
	{
		this.jsonnotes = jsonnotes;
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

	public Set<String> getKeywords()
	{
		return keywords;
	}

	public void setKeywords(Set<String> keywords)
	{
		this.keywords = keywords;
	}
}
