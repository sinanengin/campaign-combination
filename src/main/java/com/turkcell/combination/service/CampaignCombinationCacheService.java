package com.turkcell.combination.service;

import com.turkcell.combination.repository.CampaignCombinationRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class CampaignCombinationCacheService {

    private static final Logger logger = LoggerFactory.getLogger(CampaignCombinationCacheService.class);

    @Autowired
    private CampaignCombinationRepository repository;

    @PostConstruct
    public void initializeCache() {
        Instant start = Instant.now();
        logger.info("🚀 [CacheService] Initial cache load started...");

        try {
            repository.populateFullCache();
            logger.info("✅ [CacheService] Initial cache loaded in {} seconds.",
                    Duration.between(start, Instant.now()).toSeconds());
        } catch (Exception e) {
            logger.error("❌ [CacheService] Initial cache loading failed.", e);
        }
    }

    @Scheduled(fixedRate = 120_000) // every 2 minutes
    public void checkForUpdatesAndRefresh() {
        Instant start = Instant.now();
        logger.info("🔄 [CacheService] Checking for updates...");

        try {
            if (hasCombinationUpdates()) {
                logger.info("📥 [CacheService] Updates found. Refreshing cache...");
                repository.populateFullCache();
                logger.info("✅ [CacheService] Cache refreshed in {} seconds.",
                        Duration.between(start, Instant.now()).toSeconds());
            } else {
                logger.info("✅ [CacheService] No updates found. Cache is up-to-date.");
            }
        } catch (Exception e) {
            logger.error("❌ [CacheService] Cache refresh failed.", e);
        }
    }

    private boolean hasCombinationUpdates() {
        String sql = """
            SELECT COUNT(*) 
            FROM CPCM.VERSION V
            JOIN CPCM.LNK_ENTITY_VERSION LNK ON LNK.VERSIONID = V.VERSIONID
            WHERE V.VERSION_STATUS = 1
              AND LNK.ENTITY_TYPE_ID IN (4,20,35,9,21,25,26)
              AND V.START_VALIDITY_DATE > (
                  SELECT TO_DATE(VALUE, 'dd/mm/yyyy hh24:MI:SS') 
                  FROM CPCM.UTIL_PARAMETERS 
                  WHERE NAME = 'LAST_COMBINATION_DATE'
              )
              AND V.START_VALIDITY_DATE < SYSDATE
        """;

        try {
            Integer count = repository.getJdbcTemplate().queryForObject(sql, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("❌ [CacheService] Update check failed.", e);
            return false;
        }
    }
}