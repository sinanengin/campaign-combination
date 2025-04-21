package com.turkcell.combination.repository;

import java.util.List;
import java.util.Map;

public interface CampaignCombinationRepository {
    List<Map<String, Object>> findMatchingOffers();
}
