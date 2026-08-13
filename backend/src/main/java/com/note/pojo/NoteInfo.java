package com.note.pojo;

/**
 * A lightweight DTO representing basic info about a Note, excluding heavy content.
 */
public class NoteInfo {
    private Integer note_id;
    private String notename;
    private Integer notebook_id;

    public NoteInfo(Integer note_id, String notename, Integer notebook_id) {
        this.note_id = note_id;
        this.notename = notename;
        this.notebook_id = notebook_id;
    }

    // Getters and Setters
    public Integer getNote_id() {
        return note_id;
    }

    public void setNote_id(Integer note_id) {
        this.note_id = note_id;
    }

    public String getNotename() {
        return notename;
    }

    public void setNotename(String notename) {
        this.notename = notename;
    }

    public Integer getNotebook_id() {
        return notebook_id;
    }

    public void setNotebook_id(Integer notebook_id) {
        this.notebook_id = notebook_id;
    }
}