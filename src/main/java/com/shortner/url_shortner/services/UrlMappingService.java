package com.shortner.url_shortner.services;

import com.shortner.url_shortner.dto.UrlMappingDto;
import com.shortner.url_shortner.entity.UrlMappingEntity;
import com.shortner.url_shortner.entity.UserEntity;

import com.shortner.url_shortner.repository.UrlMappingRepository;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UrlMappingService {
    private final UrlMappingRepository urlMappingRepository;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final long MIN_3_CHAR_OFFSET = 238328L;
    public UrlMappingService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }
    @Transactional
    public UrlMappingDto createShortenUrl(String originalUrl, UserEntity user) {
        UrlMappingEntity urlMapping=UrlMappingEntity.builder()
                .originalUrl(originalUrl)
                .clickCount(0)
                .user(user)
                .build();

        UrlMappingEntity savedEntity = urlMappingRepository.save(urlMapping);
        String shortUrl=generateShortUrl(savedEntity.getId());
        savedEntity.setShortUrl(shortUrl);
        savedEntity = urlMappingRepository.save(savedEntity);
          UrlMappingDto urlMappingDto= UrlMappingDto.builder()
                  .createdAt(savedEntity.getCreatedAt())
                  .id(savedEntity.getId())
                  .orignalUrl(savedEntity.getOriginalUrl())
                  .shortUrl(savedEntity.getShortUrl())
                  .clickCount(savedEntity.getClickCount())
                  .username(savedEntity.getUser().getUsername())
                  .build();

          return urlMappingDto;


    }

    private String generateShortUrl(Long id) {
        long offsetId=id+MIN_3_CHAR_OFFSET;
        StringBuilder shortUrl=new StringBuilder();
        while(offsetId > 0)
        {
            int remainder =(int)(offsetId%62);
            shortUrl.append(CHARACTERS.charAt(remainder));
            offsetId=offsetId/62;
        }
        return shortUrl.reverse().toString();


    }

}
