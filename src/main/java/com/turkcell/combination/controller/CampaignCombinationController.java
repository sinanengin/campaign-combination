package com.turkcell.combination.controller;

import com.turkcell.combination.model.CampaignCombinationRequest;
import com.turkcell.combination.model.CampaignCombinationResponse;
import com.turkcell.combination.service.CampaignCombinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/combinations")
public class CampaignCombinationController {

    @Autowired
    private CampaignCombinationService campaignCombinationService;

    @PostMapping(consumes = "application/json")
    public CampaignCombinationResponse getValidCombinations(@RequestBody CampaignCombinationRequest request) {
        return campaignCombinationService.getValidCombinations(request);
    }
}