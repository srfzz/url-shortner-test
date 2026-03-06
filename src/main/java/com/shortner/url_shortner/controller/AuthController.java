package com.shortner.url_shortner.controller;

import java.time.LocalDateTime;

import com.shortner.url_shortner.apiresponse.ApiReponse;
import com.shortner.url_shortner.dto.RegisterReponseDto;
import com.shortner.url_shortner.dto.RegisterRequestDto;
import com.shortner.url_shortner.services.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiReponse<Object>> registerUser(@Valid @RequestBody RegisterRequestDto registerRequest) {
        RegisterReponseDto registerResponse = authService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiReponse.builder()
                .success(true)
                .statusCode(HttpStatus.CREATED.value())
                .message("User registered successfully")
                .data(registerResponse)
                .timestamp(LocalDateTime.now())
                .build());
    }

}
