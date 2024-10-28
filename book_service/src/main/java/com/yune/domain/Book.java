package com.yune.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("Books")
public class Book {
    @TableId(value = "book_id", type = IdType.AUTO)  // 映射主键并指定字段名称
    private int bookId;         // book_id
    private String title;       // title
    private String author;      // author
    private String genre;       // genre
    private Date publishDate;   // publish_date
    private String isbn;        // isbn
    private int stock;          // stock
    private Date createdAt;     // created_at
}
