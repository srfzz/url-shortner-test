package com.shortner.url_shortner.repository;

import com.shortner.url_shortner.entity.ClickEventEntity;
import com.shortner.url_shortner.entity.UrlMappingEntity;
import com.shortner.url_shortner.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMappingEntity,Long> {

    Optional<UrlMappingEntity> findById(Long id);


    Optional<UrlMappingEntity> findByShortUrl(String shortUrl);


    List<UrlMappingEntity> findByUser(Optional<UserEntity> user);


    List<UrlMappingEntity> user(UserEntity user);

    ClickEventEntity findByClickEvents(UrlMappingEntity urlMappingEntity);
}
