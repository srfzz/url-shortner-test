package com.shortner.url_shortner.controller;

import java.time.LocalDateTime;

import com.shortner.url_shortner.apiresponse.ApiReponse;
import com.shortner.url_shortner.dto.LoginRequestDto;
import com.shortner.url_shortner.dto.LoginResponseDto;
import com.shortner.url_shortner.dto.RegisterReponseDto;
import com.shortner.url_shortner.dto.RegisterRequestDto;
import com.shortner.url_shortner.services.AuthService;
import com.shortner.url_shortner.services.TokenBlacklistService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(AuthService authService, TokenBlacklistService tokenBlacklistService) {
        this.authService = authService;
        this.tokenBlacklistService = tokenBlacklistService;
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

    @PostMapping("/login")
    public ResponseEntity<ApiReponse<Object>> loginUser(@Valid @RequestBody LoginRequestDto loginRequest) {

        LoginResponseDto loginResponse = authService.loginUser(loginRequest);
        return ResponseEntity.ok(ApiReponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Login successful")
                .data(loginResponse)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiReponse<Object>> logoutUser(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            tokenBlacklistService.blacklistToken(token);
            SecurityContextHolder.clearContext();
        }

        return ResponseEntity.ok(
                ApiReponse.builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Logout successful")
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
