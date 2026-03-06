package com.shortner.url_shortner.apiresponse;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiReponse<T> {
    private Boolean success;
    private Integer statusCode;
    private String message;
    private T data;
    private LocalDateTime timestamp;

}
