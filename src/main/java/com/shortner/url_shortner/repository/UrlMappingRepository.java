package com.shortner.url_shortner.repository;

import com.shortner.url_shortner.entity.UrlMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMappingEntity,Long> {

}
