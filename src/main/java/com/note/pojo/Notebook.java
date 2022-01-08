package com.note.pojo;

import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.Data;
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
@Data
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

    @OneToMany(mappedBy = "notebook", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    Set<Note> notes;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    //@JsonIgnore
    @JoinColumn(name = "parent_notebook_id")
    @JsonBackReference
    Notebook parent = null;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    List<Notebook> sub_notebooks;

    public Notebook()
    {

    }
}
