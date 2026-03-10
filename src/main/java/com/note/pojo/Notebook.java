package com.note.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import tools.jackson.databind.annotation.JsonSerialize;
import com.note.config.SyncIdGenerator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Notebook {
    @Id
    @GeneratedValue(generator = "sync-id-gen")
    @org.hibernate.annotations.GenericGenerator(
            name = "sync-id-gen",
            type = com.note.config.SyncIdGenerator.class
    )
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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    List<Notebook> sub_notebooks;

    public Notebook() {

    }
}
