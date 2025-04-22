package com.turkcell.combination.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Repository
public class CampaignCombinationRepositoryImpl implements CampaignCombinationRepository {

    private static final String REDIS_KEY_PREFIX = "campaign_combination_batch:";
    private static final String REDIS_INDEX_KEY = "campaign_combination_index";
    private static final int BATCH_SIZE = 10000;
    private static final long CACHE_TTL_MINUTES = 30;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<Map<String, Object>> findMatchingOffers() {
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
            return all;
        }

        // 2. Oracle’dan batch batch veri çek
        System.out.println("⬇️ Oracle SELECT ile batch veri çekiliyor...");
        List<String> batchKeys = Collections.synchronizedList(new ArrayList<>());
        List<Map<String, Object>> fullList = Collections.synchronizedList(new ArrayList<>());
        int offset = 0;

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

            System.out.println("✅ Batch #" + batchIndex + " Redis'e yazıldı (" + batch.size() + " kayıt)");
        }

        // 3. Index key'i yaz
        redisTemplate.opsForList().rightPushAll(REDIS_INDEX_KEY, batchKeys.toArray());
        redisTemplate.expire(REDIS_INDEX_KEY, CACHE_TTL_MINUTES, TimeUnit.MINUTES);

        System.out.println("✔️ Tüm veri parçalı olarak Redis'e yazıldı. Toplam kayıt: " + fullList.size());
        return fullList;
    }
}