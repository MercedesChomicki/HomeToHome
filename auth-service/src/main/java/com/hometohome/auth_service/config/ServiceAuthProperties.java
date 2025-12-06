package com.hometohome.auth_service.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "auth.service")
public class ServiceAuthProperties {

    private Map<String, String> secrets = new HashMap<>();
    private Map<String, List<String>> scopes = new HashMap<>();
}