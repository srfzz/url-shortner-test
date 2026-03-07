package com.shortner.url_shortner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenUrlRequestDto {
    @NotBlank(message = "The field name must be  and cannot be empty")
    private String orignalUrl;

}