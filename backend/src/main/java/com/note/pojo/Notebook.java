package com.note.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NamedEntityGraph(
    name = "Notebook.withSubNotebooks",
    attributeNodes = @NamedAttributeNode("sub_notebooks")
)
public class Notebook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer notebook_id;

    @NotBlank
    String notebookname;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime cdate;

    @UpdateTimestamp
    LocalDateTime udate;

    @Transient
    private List<NoteInfo> notes;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_notebook_id")
    @JsonBackReference
    Notebook parent = null;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    List<Notebook> sub_notebooks;

    public Notebook() {

    }
}