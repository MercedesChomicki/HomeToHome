package com.hometohome.pet_service.client;

import com.hometohome.pet_service.config.FeignConfig;
import com.hometohome.pet_service.dto.response.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        value = "user-service",
        url = "${user-service.url}",
        configuration = FeignConfig.class
)
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable UUID id);
}