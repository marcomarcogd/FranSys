package com.fransys.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Jwt jwt = new Jwt();
    private String uploadDir = "uploads";

    @Data
    public static class Jwt {
        private String issuer;
        private String secret;
        private long accessTokenMinutes;
    }
}
