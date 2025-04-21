package com.turkcell.combination.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
public class CampaignCombinationRepositoryImpl implements CampaignCombinationRepository {

    private static final String REDIS_KEY = "campaign_combinations";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<Map<String, Object>> findMatchingOffers() {
        // 1. Redis kontrol
        Object cached = redisTemplate.opsForValue().get(REDIS_KEY);
        // Burası bekletiyor burayı düzenle !!
        if (cached instanceof List<?>) {
            System.out.println("➡️ Redis üzerinden veri getiriliyor...");
            return (List<Map<String, Object>>) cached;
        }

        // 2. DB'den veriyi çek
        System.out.println("⬇️ Oracle SELECT ile veri çekiliyor...");

        String sql = "SELECT * FROM CPCM.COC_FULL_LIST";
        List<Map<String, Object>> result = jdbcTemplate.query(sql, new CampaignCombinationRowMapper());

        System.out.println("✔️ Cache'e yazılacak kayıt sayısı: " + result.size());

        // 3. Redis’e cachele - /*30 dakika*/
        redisTemplate.opsForValue().set(REDIS_KEY, result/*, 30, TimeUnit.MINUTES*/);
        System.out.println("✅ Veri Redis’e cachelendi.");
        return result;
    }
}
