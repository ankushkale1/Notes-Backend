package com.note.pojo;

/**
 * A lightweight DTO representing basic info about a Note, excluding heavy content.
 */
public class NoteInfo {
    private Integer note_id;
    private String notename;

    public NoteInfo(Integer note_id, String notename) {
        this.note_id = note_id;
        this.notename = notename;
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
}