package com.yune.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
@Builder
@Data
@TableName("`order`")
public class Order {

    private int id;
    private int userId;
    private int status;
    private LocalDateTime createTime;

}
