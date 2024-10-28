package com.yune.unit;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserContext {

    public static final ThreadLocal<Long> tl = new ThreadLocal<>();

    public static void setUser(Long userId){
        tl.set(userId);
    }
    public static Long getUser(){
        return tl.get();
    }
    public static void rmUser(){
        //去掉请求头中的Authorizaiton，去掉ThreadLocal中的TOken
        tl.remove();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            HttpServletResponse response = requestAttributes.getResponse();

            // 移除请求头中的 Authorization
            if (response != null) {
                response.setHeader("Authorization", null);
    }
}}}
