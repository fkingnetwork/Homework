package com.yune.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_detail")
@Builder
public class OrderDetail {
    private int id;
    private int orderId;
    private int userId;
    private int bookId;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private double expiredFee;
    private int bookNum;
    private String bookName;
}
