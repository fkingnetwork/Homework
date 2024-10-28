package com.yune.Domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    private Long id;

    private String password;

    private String username;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private int status;

}
