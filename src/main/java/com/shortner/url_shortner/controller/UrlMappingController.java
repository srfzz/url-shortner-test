package com.shortner.url_shortner.controller;

import java.security.Principal;
import java.util.HashMap;

import com.shortner.url_shortner.apiresponse.ApiReponse;
import com.shortner.url_shortner.dto.ShortenUrlRequestDto;
import com.shortner.url_shortner.dto.UrlMappingDto;
import com.shortner.url_shortner.entity.UserEntity;
import com.shortner.url_shortner.services.UrlMappingService;
import com.shortner.url_shortner.services.UserDetailsImpl;
import com.shortner.url_shortner.services.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/urls")
public class UrlMappingController {

    private final UrlMappingService urlMappingService;
    private final UserService userService;

    public UrlMappingController(UrlMappingService urlMappingService, UserService userService) {
        this.urlMappingService = urlMappingService;
        this.userService = userService;
    }

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiReponse<Object>> shortenUrl(@Valid @RequestBody ShortenUrlRequestDto shortenUrlRequestDto ,
                                                          Authentication authentication) {
        log.info("principal data:{}", authentication.getPrincipal());
        String originalUrl = shortenUrlRequestDto.getOrignalUrl();
        log.info("original url:{}", originalUrl);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserEntity user = userService.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UrlMappingDto urlMappingDto = urlMappingService.createShortenUrl(originalUrl, user);
        return ResponseEntity.ok(ApiReponse.builder()
                .success(true)
                .statusCode(200)
                .message("URL shortened successfully")
                .data(urlMappingDto)
                .build());

    }

}
