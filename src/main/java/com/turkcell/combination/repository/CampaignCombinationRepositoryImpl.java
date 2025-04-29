package com.turkcell.combination.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
public class CampaignCombinationRepositoryImpl implements CampaignCombinationRepository {

    private static final Logger logger = LoggerFactory.getLogger(CampaignCombinationRepositoryImpl.class);

    private static final String REDIS_KEY_PREFIX = "campaign_combination_batch:";
    private static final String REDIS_INDEX_KEY = "campaign_combination_index";
    private static final int BATCH_SIZE = 10000;
    private static final long CACHE_TTL_MINUTES = 30;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void initCacheOnStartup() {
        logger.info("🛠 Başlangıçta veriler Redis'e kaydediliyor...");
        findMatchingOffers();
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    @Override
    public List<Map<String, Object>> findMatchingOffers() {
        long startTime = System.currentTimeMillis(); // ⭐ Başlangıç zamanı

        // 1. Redis'te veri var mı?
        List<Object> cachedKeys = redisTemplate.opsForList().range(REDIS_INDEX_KEY, 0, -1);
        if (cachedKeys != null && !cachedKeys.isEmpty()) {
            System.out.println("➡️ Redis üzerinden parçalı veri getiriliyor...");
            List<Map<String, Object>> all = new ArrayList<>();
            for (Object key : cachedKeys) {
                List<Map<String, Object>> part = (List<Map<String, Object>>) redisTemplate.opsForValue().get(key.toString());
                if (part != null) {
                    all.addAll(part);
                }
            }

            long endTime = System.currentTimeMillis();
            System.out.println("✅ Redis'ten veri okuma tamamlandı (" + (endTime - startTime) / 1000.0 + " saniye)");
            return all;
        }

        // 2. Oracle’dan batch batch veri çek
        System.out.println("⬇️ Oracle SELECT ile batch veri çekiliyor...");
        List<String> batchKeys = Collections.synchronizedList(new ArrayList<>());
        List<Map<String, Object>> fullList = Collections.synchronizedList(new ArrayList<>());
        int offset = 0;
        int totalInserted = 0;

        while (true) {
            String sql = "SELECT * FROM CPCM.COC_FULL_LIST OFFSET " + offset + " ROWS FETCH NEXT " + BATCH_SIZE + " ROWS ONLY";
            List<Map<String, Object>> batch = jdbcTemplate.query(sql, new CampaignCombinationRowMapper());

            if (batch.isEmpty()) break;

            fullList.addAll(batch);
            final int batchIndex = offset / BATCH_SIZE;
            String redisKey = REDIS_KEY_PREFIX + batchIndex;
            batchKeys.add(redisKey);

            redisTemplate.opsForValue().set(redisKey, batch, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            offset += BATCH_SIZE;
            totalInserted += batch.size();

            System.out.println("✅ Batch #" + batchIndex + " Redis'e yazıldı (" + batch.size() + " kayıt)");
        }

        redisTemplate.opsForList().rightPushAll(REDIS_INDEX_KEY, batchKeys.toArray());
        redisTemplate.expire(REDIS_INDEX_KEY, CACHE_TTL_MINUTES, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis(); // ⭐ Bitiş zamanı
        System.out.println("✔️ Tüm veri Redis'e parçalı olarak yazıldı. Toplam kayıt: " + totalInserted);
        System.out.println("⏱️ Cacheleme işlemi toplam " + (endTime - startTime) / 1000.0 + " saniye sürdü.");

        return fullList;
    }
}

