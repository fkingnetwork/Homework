package com.yune;

import com.alibaba.spring.beans.factory.annotation.EnableConfigurationBeanBinding;
import com.yune.client.TradeClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableConfigurationProperties
@SpringBootApplication
@MapperScan("com.yune.Mapper")
@EnableFeignClients(clients = {TradeClient.class})
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}