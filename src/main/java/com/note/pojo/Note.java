package com.note.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Entity
//@Indexed
@Data
public class Note {
    @Id
//    @GeneratedValue(generator = "sync-id-gen")
//    @org.hibernate.annotations.GenericGenerator(
//            name = "sync-id-gen",
//            type = com.note.config.SyncIdGenerator.class
//    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int note_id;

    @NotBlank
//    @Field
    @Basic
    String notename;

    @NotBlank
    @Basic
    @Column(columnDefinition = "LONGTEXT")
    String jsonnotes;

    @Column(updatable = false)
    @CreationTimestamp
    @Basic
    Timestamp cdate;

    @UpdateTimestamp
    @Basic
    Timestamp udate;

    //@NotEmpty
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
//    @Field
    @Basic
    @Column(columnDefinition = "LONGTEXT")
    String plain_content;

    public Note() {

    }

    @Override
    public int hashCode() {
        return Objects.hash(note_id, notename, notebook_id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Note note)
            return note_id == (note.note_id);
        else
            return false;
    }

    @Override
    public String toString() {
        return "Note [note_id=" + note_id + ", notename=" + notename + ", jsonnotes=" + jsonnotes + ", cdate=" + cdate
                + ", udate=" + udate + ", keywords=" + keywords + ", notebook=" + notebook + ", notebook_id="
                + notebook_id + ", plain_content=" + plain_content + "]";
    }

}
