package com.yune.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data

public class CartDto {

    private int bookId;
    private int bookNum;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;


}
