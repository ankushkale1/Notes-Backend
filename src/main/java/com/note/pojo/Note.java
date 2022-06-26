package com.note.pojo;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
@Data
public class Note
{
    @Id
    String note_id;

    @NotBlank
    String notename;

    @NotBlank
    String jsonnotes;

    @CreatedDate
    Timestamp cdate;

    Timestamp udate;

    @NotEmpty
    Set<String> keywords;

    @DBRef
    @JsonBackReference
    Notebook notebook;

    @Transient
    //as above thing skips notebook object
    @JsonSerialize
    @JsonDeserialize
    String notebook_id;

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
            return note_id.equals(((Note) obj).note_id);
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
