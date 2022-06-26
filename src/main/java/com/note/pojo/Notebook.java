package com.note.pojo;

import javax.validation.constraints.*;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Document
public class Notebook
{
    @Id
    String notebook_id;

    String notebookname;

    @CreatedDate
    Date cdate;

    @DBRef
    @JsonManagedReference
    Set<Note> notes;

    @DocumentReference
    @JsonBackReference
    Notebook parent = null;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @DocumentReference
    List<Notebook> sub_notebooks;

    public Notebook()
    {

    }
}
