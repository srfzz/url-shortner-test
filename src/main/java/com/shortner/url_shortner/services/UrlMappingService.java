package com.shortner.url_shortner.services;

import com.shortner.url_shortner.dto.ClickEventDto;
import com.shortner.url_shortner.dto.UrlMappingDto;
import com.shortner.url_shortner.entity.ClickEventEntity;
import com.shortner.url_shortner.entity.UrlMappingEntity;
import com.shortner.url_shortner.entity.UserEntity;

import com.shortner.url_shortner.exceptions.ResourceNotFoundException;
import com.shortner.url_shortner.repository.ClickEventsRepository;
import com.shortner.url_shortner.repository.UrlMappingRepository;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UrlMappingService {
    private final UrlMappingRepository urlMappingRepository;
    private final ClickEventsRepository clickEventsRepository;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final long MIN_3_CHAR_OFFSET = 238328L;
    public UrlMappingService(UrlMappingRepository urlMappingRepository,ClickEventsRepository clickEventsRepository) {
        this.urlMappingRepository = urlMappingRepository;
        this.clickEventsRepository = clickEventsRepository;
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

    public List<UrlMappingDto> getAllUrls(Optional<UserEntity> user) {

        List<UrlMappingDto> urlMappingDtos= urlMappingRepository.findByUser(user).stream().map(m->{return UrlMappingDto.builder().orignalUrl(m.getOriginalUrl()).id(m.getId()).createdAt(m.getCreatedAt()).shortUrl(m.getShortUrl()).clickCount(m.getClickCount()).username(m.getUser().getUsername()).build();}).collect(Collectors.toList());
        log.info("urlMappingDtos:"+urlMappingDtos);
        System.out.println(urlMappingDtos);
    return urlMappingDtos;

    }

    public List<ClickEventDto> getClikEventByDate(String shortUrl, LocalDateTime startDate,LocalDateTime endDate) {
    UrlMappingEntity urlMappingEntity  =urlMappingRepository.findByShortUrl(shortUrl).orElseThrow(()->new ResourceNotFoundException("Url Mapping Not Found"));
    List <ClickEventEntity> clicks=clickEventsRepository.findByUrlMappingAndCreatedAtBetween(urlMappingEntity,startDate,endDate);
        Map<LocalDate, Long> countsByDate = clicks.stream()
                .collect(Collectors.groupingBy(
                        click -> click.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));
        return countsByDate.entrySet().stream()
                .map(entry -> ClickEventDto.builder()
                        .clickDate(entry.getKey().atStartOfDay())
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }


    public Map<LocalDate,Long> getTotalClicksByUserAndDate(UserEntity user, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<UrlMappingEntity> urlMappingEntityList=urlMappingRepository.findByUser(Optional.ofNullable(user));
        if(urlMappingEntityList.isEmpty())
        {
            throw new ResourceNotFoundException("no Urls Found");
        }
        return clickEventsRepository.findByUrlMappingInAndCreatedAtBetween(urlMappingEntityList,startDateTime,endDateTime)
                .stream().collect(Collectors.groupingBy(
                        click -> click.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));
//       return totalCliks.entrySet().stream()
//               .map(entry->{
//           return ClickEventDto.builder()
//                   .clickDate(entry.getKey().atStartOfDay())
//                   .count(entry.getValue()).build();
//        }).toList();


    }
}
