package com.yune.config;

import com.yune.Mapper.UserMapper;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class UserStatusAspect {
    @Autowired
    private UserMapper userMapper;


    // 切入点，拦截 UserMapper 中的 changeStatus 方法
    @Before("execution(* com.yune.Controller.UserController.changeStatus(..)) && args(id, status)")
    public void beforeChangeStatus(Long id, int status) {
        // 更新用户的更新时间
        userMapper.updateTime(id, LocalDateTime.now());
    }


}
