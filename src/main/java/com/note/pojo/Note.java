package com.note.pojo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.*;

import lombok.Data;
import org.hibernate.annotations.*;
import org.hibernate.search.annotations.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;

@Entity
@Indexed
@Data
public class Note
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Field(termVector = TermVector.YES)
    int note_id;

    @NotBlank
    @Field
    @Basic
    String notename;

    @NotBlank
    @Basic
    @Type(type = "text")
    String jsonnotes;

    @Column(updatable = false)
    @CreationTimestamp
    @Basic
    Timestamp cdate;

    @UpdateTimestamp
    @Basic
    Timestamp udate;

    @NotEmpty
    @ElementCollection
    Set<String> keywords;

    @ManyToOne(optional = false)
    @JoinColumn(name = "notebook_id", nullable = false)
    @JsonBackReference
    Notebook notebook;

    @Transient
    //as above thing skips notebook object
    @JsonSerialize
    @JsonDeserialize
    Integer notebook_id;

    //@NotEmpty
    @Field
    @Basic
    @Type(type = "text")
    String plain_content;

    public Note()
    {

    }

    @Override
    public int hashCode()
    {
        return Objects.hash(note_id, notename, notebook_id);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Note)
            return note_id == (((Note) obj).note_id);
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
