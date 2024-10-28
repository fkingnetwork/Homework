package com.yune.filter;

import cn.hutool.core.text.AntPathMatcher;
import com.yune.config.AuthProperties;
import com.yune.unit.JwtTool;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.time.Duration;
@RequiredArgsConstructor
@Component

public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(AuthGlobalFilter.class);

    private final JwtTool jwtTool;

    private final StringRedisTemplate redisTemplate;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final AuthProperties authProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        ServerHttpResponse response = exchange.getResponse();

        //判断是否需要拦截
        if (isExclude(path)){
            return chain.filter(exchange);
        }
        // 获取请求头中的 Authorization
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);

        if (token == null || jwtTool.parseToken(token) == null) {
            // Token不存在或无效时重定向到登录页面

            // 设置302状态码和重定向URL
            response.setStatusCode(HttpStatus.FOUND); // 302重定向
            response.getHeaders().setLocation(URI.create("/user"));
            return response.setComplete();
//            response.setStatusCode(HttpStatus.SEE_OTHER);
//            response.getHeaders().set("FAILTOAUTH","0");
//            return response.setComplete();
        }
        //解析token，获取用户id
        Long id = jwtTool.parseToken(token);
        log.info("userID:{}",id);
        //传递用户信息
        String userInfo = id.toString();
        ServerWebExchange ex = exchange.mutate()
                .request(b->b.header("user-info",userInfo))
                .build();


        //放行
        return chain.filter(exchange);
    }

    private boolean isExclude(String path) {
        for(String expath : authProperties.getExcludePaths() ){
            boolean match = antPathMatcher.match(expath, path);
            if (match){
                return true;
            }
        }
        return false;
    }


    @Override
    public int getOrder() {
        return -1;
    }
}
