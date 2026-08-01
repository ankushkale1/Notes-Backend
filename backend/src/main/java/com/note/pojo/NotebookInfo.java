package com.note.pojo;

import java.time.LocalDateTime;

/**
 * A DTO for fetching only essential notebook data, excluding the heavy notes list.
 */
public class NotebookInfo {
    private Integer notebook_id;
    private String notebookname;
    private LocalDateTime cdate;
    private LocalDateTime udate;

    public NotebookInfo(Integer notebook_id, String notebookname, LocalDateTime cdate, LocalDateTime udate) {
        this.notebook_id = notebook_id;
        this.notebookname = notebookname;
        this.cdate = cdate;
        this.udate = udate;
    }

    // Standard Getters & Setters
    public Integer getNotebook_id() { return notebook_id; }
    public void setNotebook_id(Integer notebook_id) { this.notebook_id = notebook_id; }
    public String getNotebookname() { return notebookname; }
    public void setNotebookname(String notebookname) { this.notebookname = notebookname; }
    public LocalDateTime getCdate() { return cdate; }
    public void setCdate(LocalDateTime cdate) { this.cdate = cdate; }
    public LocalDateTime getUdate() { return udate; }
    public void setUdate(LocalDateTime udate) { this.udate = udate; }
}