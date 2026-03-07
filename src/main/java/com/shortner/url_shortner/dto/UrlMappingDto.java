package com.shortner.url_shortner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlMappingDto {
    private Long id;
    private String orignalUrl;
    private String shortUrl;
    private Integer clickCount;
    private  String username;
    private LocalDateTime createdAt;

}
