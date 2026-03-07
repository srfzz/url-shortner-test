package com.shortner.url_shortner.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.shortner.url_shortner.apiresponse.ApiReponse;
import com.shortner.url_shortner.dto.ClickEventDto;
import com.shortner.url_shortner.dto.ShortenUrlRequestDto;
import com.shortner.url_shortner.dto.UrlMappingDto;
import com.shortner.url_shortner.entity.UrlMappingEntity;
import com.shortner.url_shortner.entity.UserEntity;
import com.shortner.url_shortner.exceptions.ResourceNotFoundException;
import com.shortner.url_shortner.services.UrlMappingService;
import com.shortner.url_shortner.services.UserDetailsImpl;
import com.shortner.url_shortner.services.UserService;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import javax.swing.text.html.Option;

@Slf4j
@RestController
@RequestMapping("/api/urls")
@PreAuthorize("hasRole('USER')")
public class UrlMappingController {

    private final UrlMappingService urlMappingService;
    private final UserService userService;

    public UrlMappingController(UrlMappingService urlMappingService, UserService userService) {
        this.urlMappingService = urlMappingService;
        this.userService = userService;
    }

    @PostMapping("/shorten")

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

    @GetMapping("/myurls")
    public ResponseEntity<ApiReponse<Object>> fetchAllUrls(Authentication authentication)
    {
      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      log.info("principal data:{}", authentication.getPrincipal());
        Optional<UserEntity> user = userService.findById(userDetails.getId());
      List<UrlMappingDto> urlMappingDto=urlMappingService.getAllUrls(user);
      log.info("urlMappingDto:"+urlMappingDto);
      return ResponseEntity.ok().body(ApiReponse.builder().message("fetched succesfully").statusCode(HttpStatus.OK.value()).data(urlMappingDto).success(true).timestamp(LocalDateTime.now()).build());
    }

    @GetMapping("/analytics/{shortUr}")
    public ResponseEntity<ApiReponse<Object>> getAnalytics(@PathVariable String shortUrl, @RequestParam("startDate") String startDate,@RequestParam("endDate") String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);
     List<ClickEventDto> clickEventDtos  =urlMappingService.getClikEventByDate(shortUrl,startDateTime,endDateTime);
     return ResponseEntity.ok().body(ApiReponse.builder().success(true).statusCode(HttpStatus.OK.value()).message("Successfull").data(clickEventDtos).timestamp(LocalDateTime.now()).build());
    }
    @GetMapping("/analytics/totalClicks")
    public ResponseEntity<ApiReponse<Object>> getUrlTotalClicks(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);
        UserEntity user = userService.findById(userDetails.getId())

                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Map<LocalDate,Long> totalClicks=urlMappingService.getTotalClicksByUserAndDate(user,startDateTime,endDateTime);
        return ResponseEntity.ok().body(ApiReponse.builder().success(true).statusCode(HttpStatus.OK.value()).message("Successfull").data(totalClicks).timestamp(LocalDateTime.now()).build());


    }


}
