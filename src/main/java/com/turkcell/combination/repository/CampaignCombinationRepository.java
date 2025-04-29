package com.turkcell.combination.repository;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public interface CampaignCombinationRepository {
    List<Map<String, Object>> findMatchingOffers();
    JdbcTemplate getJdbcTemplate();
}