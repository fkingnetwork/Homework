package com.yune.domain;

import com.baomidou.mybatisplus.annotation.TableName;
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
}
