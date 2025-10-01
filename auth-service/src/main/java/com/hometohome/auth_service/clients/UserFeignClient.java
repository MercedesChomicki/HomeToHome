package com.hometohome.auth_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hometohome.auth_service.config.FeignConfig;
import com.hometohome.auth_service.dto.UserRequestDto;
import com.hometohome.auth_service.dto.UserResponseDto;

@FeignClient(name = "user-service", path = "/users", configuration = FeignConfig.class)
public interface UserFeignClient {
    @PostMapping
    UserResponseDto createUser(@RequestBody UserRequestDto user);

    @GetMapping("/by-email")
    UserResponseDto findByEmail(@RequestParam("email") String email);
}