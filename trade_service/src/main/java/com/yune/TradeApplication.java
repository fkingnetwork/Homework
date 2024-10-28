package com.yune;

import com.yune.client.CartClient;
import com.yune.client.UserClient;
import com.yune.client.bookCliet;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.yune.Mapper")
@EnableFeignClients(clients = {bookCliet.class, CartClient.class, UserClient.class})
public class TradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradeApplication.class,args);
    }
}