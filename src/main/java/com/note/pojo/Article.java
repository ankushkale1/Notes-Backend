package com.note.pojo;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
public class Article
{
    @Id
    @GeneratedValue
    Integer aid;

    String url;

    String data;

    @CreationTimestamp
    LocalDateTime cdate;

    @UpdateTimestamp
    LocalDateTime udate;
}
