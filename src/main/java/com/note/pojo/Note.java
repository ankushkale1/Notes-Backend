package com.note.pojo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.*;

import org.hibernate.annotations.*;
import org.hibernate.search.annotations.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;

@Entity
@Indexed
//@Builder(toBuilder=true)
public class Note
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	//@Field(termVector = TermVector.YES)
	Integer note_id;
	
	@NotBlank
	@Field
	String notename;
	
	@NotBlank
	@Lob
	String jsonnotes;
	
	@Column(updatable = false)
	@CreationTimestamp
	LocalDateTime cdate;
	
	@UpdateTimestamp
	LocalDateTime udate;
	
	@NotEmpty
	@ElementCollection
	Set<String> keywords;
	
	@ManyToOne(optional=false)
    @JoinColumn(name="notebook_id", nullable=false)
	@JsonBackReference
	Notebook notebook;
	
	@Transient
	//as above thing skips notebook object
	@JsonSerialize
	@JsonDeserialize
	Integer notebook_id;
	
	//@NotEmpty
	@Lob
	@Field
	String plain_content;
	
	public Note()
	{
		
	}

	public String getPlain_content()
	{
		return plain_content;
	}

	public void setPlain_content(String plain_content)
	{
		this.plain_content = plain_content;
	}

	public Integer getNotebook_id()
	{
		return notebook_id;
	}

	public void setNotebook_id(Integer notebook_id)
	{
		this.notebook_id = notebook_id;
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

	@Override
	public int hashCode()
	{
		return Objects.hash(note_id,notename,notebook_id);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Note)
			return note_id.equals(((Note)obj).note_id);
		else
			return false;
	}

	@Override
	public String toString()
	{
		return "Note [note_id=" + note_id + ", notename=" + notename + ", jsonnotes=" + jsonnotes + ", cdate=" + cdate
				+ ", udate=" + udate + ", keywords=" + keywords + ", notebook=" + notebook + ", notebook_id="
				+ notebook_id + ", plain_content=" + plain_content + "]";
	}
	
}
