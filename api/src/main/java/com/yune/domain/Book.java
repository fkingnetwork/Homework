package com.yune.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("Books")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bookId;         // book_id
    private String title;       // title
    private String author;      // author
    private String genre;       // genre
    private Date publishDate;   // publish_date
    private String isbn;        // isbn
    private int stock;          // stock
    private Date createdAt;     // created_at
}
