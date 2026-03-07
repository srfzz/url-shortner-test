package com.shortner.url_shortner.repository;

import com.shortner.url_shortner.entity.ClickEventEntity;
import com.shortner.url_shortner.entity.UrlMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClickEventsRepository extends JpaRepository<ClickEventEntity, Long> {

    List<ClickEventEntity> findByUrlMappingAndCreatedAtBetween(UrlMappingEntity urlMapping, LocalDateTime startDate, LocalDateTime enddate);
    List<ClickEventEntity> findByUrlMappingInAndCreatedAtBetween(List<UrlMappingEntity> urlMapping, LocalDateTime startDate, LocalDateTime enddate);

}

