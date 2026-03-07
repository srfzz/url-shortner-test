package com.shortner.url_shortner.services;

import com.shortner.url_shortner.entity.ClickEventEntity;
import com.shortner.url_shortner.entity.UrlMappingEntity;
import com.shortner.url_shortner.exceptions.ResourceNotFoundException;
import com.shortner.url_shortner.repository.ClickEventsRepository;
import com.shortner.url_shortner.repository.UrlMappingRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedirectService {
    private final UrlMappingRepository urlMappingRepository;
    private final ClickEventsRepository clickEventsRepository;

    public RedirectService(UrlMappingRepository urlMappingRepository, ClickEventsRepository clickEventsRepository) {
        this.urlMappingRepository = urlMappingRepository;
        this.clickEventsRepository = clickEventsRepository;
    }

    public String fetchUrlAndRedirect(String url, HttpServletRequest req, HttpServletResponse res)
    {
        UrlMappingEntity urlMapping=urlMappingRepository.findByShortUrl(url).orElseThrow(()-> new ResourceNotFoundException("Invalid url"));
        urlMapping.setClickCount(urlMapping.getClickCount()+1);
        urlMappingRepository.save(urlMapping);
        String ipAddress=req.getRemoteAddr();
        String agent=req.getHeader("User-Agent");
        ClickEventEntity clickEventEntity= ClickEventEntity.builder().urlMapping(urlMapping).ipAddress(ipAddress).userAgent(agent).build();
        clickEventsRepository.save(clickEventEntity);
        return urlMapping.getOriginalUrl();

    }
}
