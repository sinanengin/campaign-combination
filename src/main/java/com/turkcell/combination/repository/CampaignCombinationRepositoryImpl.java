package com.turkcell.combination.repository;

import com.turkcell.combination.repository.CampaignCombinationRowMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
public class CampaignCombinationRepositoryImpl implements CampaignCombinationRepository {

    private static final Logger logger = LoggerFactory.getLogger(CampaignCombinationRepositoryImpl.class);
    private static final String INDEX_KEY = "campaign_combination_keys";
    private static final long TTL_MINUTES = 30;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public void populateFullCache() {
        logger.info("📦 [Redis] Oracle'dan tüm kombinasyonlar çekiliyor...");
        List<Map<String, Object>> rows = jdbcTemplate.query("SELECT * FROM CPCM.COC_FULL_LIST", new CampaignCombinationRowMapper());

        Set<String> indexSet = new HashSet<>();

        int count = 0;
        for (Map<String, Object> row : rows) {
            Set<String> keys = extractKeysFromRow(row);

            for (String key : keys) {
                redisTemplate.opsForList().rightPush(key, row);
                indexSet.add(key);
            }

            count++;
            if (count % 10000 == 0) {
                logger.info("➡ {} kayıt işlendi...", count);
            }
        }

        redisTemplate.opsForValue().set(INDEX_KEY, new ArrayList<>(indexSet), TTL_MINUTES, TimeUnit.MINUTES);
        logger.info("✅ [Redis] {} kayıt cache'e yazıldı. {} benzersiz key oluşturuldu.", count, indexSet.size());
    }




    @Override
    public List<Map<String, Object>> findMatchingOffers(List<String> requestedKeys) {
        long start = System.currentTimeMillis();

        Set<Map<String, Object>> resultSet = new HashSet<>();
        for (String key : requestedKeys) {
            List<Object> cachedList = redisTemplate.opsForList().range(key, 0, -1);
            if (cachedList != null) {
                for (Object item : cachedList) {
                    if (item instanceof Map<?, ?> row) {
                        resultSet.add((Map<String, Object>) row);
                    }
                }
            }
        }

        logger.info("🔍 Redis'ten {} kayıt alındı. Süre: {} ms", resultSet.size(), System.currentTimeMillis() - start);


        String updateDateSql = """
    UPDATE CPCM.UTIL_PARAMETERS 
    SET VALUE = TO_CHAR((
        SELECT MAX(V.START_VALIDITY_DATE)
        FROM CPCM.VERSION V
        JOIN CPCM.LNK_ENTITY_VERSION LNK ON LNK.VERSIONID = V.VERSIONID
        WHERE V.VERSION_STATUS = 1
          AND LNK.ENTITY_TYPE_ID IN (4,20,35,9,21,25,26)
    ), 'dd/mm/yyyy hh24:MI:SS')
    WHERE NAME = 'LAST_COMBINATION_DATE'
""";

        jdbcTemplate.update(updateDateSql);

        return new ArrayList<>(resultSet);
    }

    private Set<String> extractKeysFromRow(Map<String, Object> row) {
        Set<String> keys = new HashSet<>();

        Object baseCampaign = row.get("NCAMPAIGNCODE");
        Object baseNofr = row.get("BASE_NOFR");
        if (baseCampaign != null && baseNofr != null) {
            keys.add(baseCampaign + ":" + baseNofr);
        }

        for (int code : List.of(1001, 1002, 1004, 1006, 1007, 1008, 1009, 1010, 1011)) {
            Object crossCampaign = row.get("CROSS_CAMPAIGNCODE_" + code);
            for (int i = 1; i <= 3; i++) {
                Object nofr = row.get("NOFR_" + code + "_" + i);
                if (crossCampaign != null && nofr != null) {
                    keys.add(crossCampaign + ":" + nofr);
                }
            }
        }

        return keys;
    }
}