package com.turkcell.combination.service;

import com.turkcell.combination.model.CampaignCombinationRequest;
import com.turkcell.combination.model.CampaignCombinationResponse;

public interface CampaignCombinationService {
    CampaignCombinationResponse getValidCombinations(CampaignCombinationRequest request);
}
