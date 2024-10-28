package com.yune.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties("hm.auth")
public class AuthProperties {
    private List<String> excludePaths;
}
