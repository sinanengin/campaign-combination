package com.turkcell.combination.service;

import com.turkcell.combination.model.CombinationRow;
import com.turkcell.combination.repository.CampaignCombinationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class CampaignCombinationCacheService {

    @Autowired
    private CampaignCombinationRepository repository;

    @PostConstruct
    public void initialLoad() {
        System.out.println("🚀 [CacheService] Initial cache loading started...");
        Instant start = Instant.now();

        repository.findMatchingOffers(); // cache'e yazıyor

        Instant end = Instant.now();
        System.out.println("✅ [CacheService] Initial cache loaded in " + Duration.between(start, end).toSeconds() + " seconds.");
    }

    @Scheduled(fixedRate = 120000) // 2 dakikada bir
    public void refreshIfNeeded() {
        System.out.println("🔄 [CacheService] Refresh check started...");

        boolean hasUpdates = checkForUpdates();
        if (hasUpdates) {
            System.out.println("📥 [CacheService] Updates detected! Reloading cache...");
            Instant start = Instant.now();

            repository.findMatchingOffers(); // Güncel veriyi tekrar cache'e al

            Instant end = Instant.now();
            System.out.println("✅ [CacheService] Cache refreshed in " + Duration.between(start, end).toSeconds() + " seconds.");
        } else {
            System.out.println("✅ [CacheService] No updates found. Cache is up-to-date.");
        }
    }

    private boolean checkForUpdates() {
        // 🔍 Oracle'a SELECT
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

        Integer count = repository.getJdbcTemplate().queryForObject(sql, Integer.class);

        return count != null && count > 0;
    }
}