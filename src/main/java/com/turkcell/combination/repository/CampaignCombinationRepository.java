package com.turkcell.combination.repository;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public interface CampaignCombinationRepository {
    void populateFullCache();
    List<Map<String, Object>> findMatchingOffers(List<String> requestedKeys);
    JdbcTemplate getJdbcTemplate();
}