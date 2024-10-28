package com.yune.Intercepter;

import com.yune.unit.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
//通过拦截器实现微服务之间传递userid
@Component
public class feignIntercepter implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long userId = UserContext.getUser();
        if (userId!=null){
            requestTemplate.header("user-info", String.valueOf(userId));
        }
    }
}
