package com.note.pojo;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Article
{
    @Id
    String aid;

    String url;

    String data;

    @CreatedDate
    LocalDateTime cdate;
}
