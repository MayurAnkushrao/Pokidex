package com.example.pokedex_backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${cache.pokemon.max-size}")
    private long pokemonMaxSize;

    @Value("${cache.pokemon.ttl-seconds}")
    private long pokemonTtlSeconds;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("pokemon");
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .initialCapacity(50)
                .maximumSize(pokemonMaxSize)
                .expireAfterWrite(pokemonTtlSeconds, TimeUnit.SECONDS)
        );
        return cacheManager;
    }
}
