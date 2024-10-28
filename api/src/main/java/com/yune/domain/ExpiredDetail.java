package com.yune.domain;

import lombok.Data;

import java.time.Duration;

@Data
public class ExpiredDetail {
    private String bookName;
    private Duration expiredDays;
    private int orderId;
}
