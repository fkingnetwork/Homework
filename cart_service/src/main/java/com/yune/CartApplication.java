package com.yune;

import com.yune.client.TradeClient;
import com.yune.client.UserClient;
import com.yune.client.bookCliet;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.swing.*;
@SpringBootApplication
@MapperScan("com.yune.mapper")
@EnableFeignClients(clients = {bookCliet.class, UserClient.class, TradeClient.class})
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class,args);
    }
}