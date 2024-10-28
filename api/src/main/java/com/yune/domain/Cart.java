package com.yune.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@TableName("cart")
@Data
@Builder
public class Cart {
    private int id;
    private int bookId;
    private String bookName;
    private int bookNum;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private double expiredFee;
    private int userId;

    @JsonCreator
    public Cart(@JsonProperty("id") int id,
                @JsonProperty("bookId") int bookId,
                @JsonProperty("bookName") String bookName,
                @JsonProperty("bookNum") int bookNum,
                @JsonProperty("borrowDate") LocalDateTime borrowDate,
                @JsonProperty("returnDate") LocalDateTime returnDate,
                @JsonProperty("expiredFee") double expiredFee,
                @JsonProperty("userId") int userId) {
        this.id = id;
        this.bookId = bookId;
        this.bookNum = bookNum;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.expiredFee = expiredFee;
        this.userId = userId;
        this.bookName=bookName;
    }


}
