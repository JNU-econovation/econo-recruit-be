package com.econovation.recruit.api.config;

import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(List.of(
                "boardsByColumnsIds",           // BoardService::getBoardByColumnsIds
                "columnsByNavigationId",        // ColumnService::getByNavigationId
                "boardCardsByNavigationId",     // CardService::getByNavigationId
                "recordsByPage"                 // RecordService::execute
        ));
        return cacheManager;
    }

}
