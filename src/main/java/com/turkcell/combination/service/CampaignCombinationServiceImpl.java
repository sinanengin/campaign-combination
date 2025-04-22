package com.turkcell.combination.service;

import com.turkcell.combination.model.*;
import com.turkcell.combination.repository.CampaignCombinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CampaignCombinationServiceImpl implements CampaignCombinationService {

    @Autowired
    private CampaignCombinationRepository repository;

    @Override
    public CampaignCombinationResponse getValidCombinations(CampaignCombinationRequest request) {
        List<Map<String, Object>> allCombinations = repository.findMatchingOffers();
        List<CombinationResponse> responseList = new ArrayList<>();

        for (Map<String, Object> combination : allCombinations) {
            if (!isMatchingCombination(combination, request.getCampaignOffers())) {
                continue;
            }

            String attr = combination.get("COLLECTION_TYPE") + ":" + combination.get("SUBSCRIPTION_PERIOD");
            List<CombinationComponent> components = new ArrayList<>();

            // Tarife (1006)
            Object cross1006 = combination.get("CROSS_CAMPAIGNCODE_1006");
            Object nofr1006_1 = combination.get("NOFR_1006_1");
            if (cross1006 != null && nofr1006_1 != null) {
                components.add(new CombinationComponent(cross1006 + ":" + nofr1006_1));
            }

            // Ana teklif + yan teklifler (excluding 1006)
            StringBuilder offerBuilder = new StringBuilder();
            offerBuilder.append(combination.get("NCAMPAIGNCODE")).append(":").append(combination.get("BASE_NOFR"));

            for (int i = 1001; i <= 1011; i++) {
                if (i == 1006) continue; // already added
                String key = "NOFR_" + i + "_1";
                Object value = combination.get(key);
                if (value != null) {
                    offerBuilder.append(":").append(value);
                }
            }

            components.add(new CombinationComponent(offerBuilder.toString()));
            responseList.add(new CombinationResponse(attr, components));
        }

        return new CampaignCombinationResponse(responseList);
    }

    private boolean isMatchingCombination(Map<String, Object> combination, List<CampaignOfferRequest> requestOffers) {
        Set<String> availablePairs = new HashSet<>();
        availablePairs.add(combination.get("NCAMPAIGNCODE") + ":" + combination.get("BASE_NOFR"));

        for (int i = 1001; i <= 1011; i++) {
            for (int j = 1; j <= 3; j++) {
                Object campaign = combination.get("CROSS_CAMPAIGNCODE_" + i);
                Object offer = combination.get("NOFR_" + i + "_" + j);
                if (campaign != null && offer != null) {
                    availablePairs.add(campaign + ":" + offer);
                }
            }
        }

        for (CampaignOfferRequest req : requestOffers) {
            String expected = req.getNcampaign() + ":" + req.getNofr();
            if (!availablePairs.contains(expected)) {
                return false;
            }
        }

        return true;
    }
}