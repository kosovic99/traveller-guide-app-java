package com.travellerguide.traveller_guide_api.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "countries",
                "countryBySlug",
                "citiesByCountrySlug",
                "cityBySlug",
                "countryCities",
                "attractionsByCitySlug",
                "attractionByCityAndSlug",
                "searchAll",
                "prerenderRoutes",
                "categoriesBootstrap"
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(50)
                .maximumSize(500)
                .expireAfterWrite(Duration.ofMinutes(30)));

        return cacheManager;
    }
}
