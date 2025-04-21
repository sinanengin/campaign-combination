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
            if (!matchesRequest(combination, request.getCampaignOffers())) {
                continue;
            }

            String attr = combination.get("COLLECTION_TYPE") + ":" + combination.get("SUBSCRIPTION_PERIOD");
            List<CombinationComponent> components = new ArrayList<>();

            // 1. cmp → Tarife
            if (combination.get("CROSS_CAMPAIGNCODE_1006") != null && combination.get("NOFR_1006_1") != null) {
                components.add(new CombinationComponent(
                        combination.get("CROSS_CAMPAIGNCODE_1006") + ":" + combination.get("NOFR_1006_1")
                ));
            }

            // 2. cmp → Ana teklif + yan teklifler
            StringBuilder builder = new StringBuilder();
            builder.append(combination.get("NCAMPAIGNCODE")).append(":").append(combination.get("BASE_NOFR"));
            for (int i = 1001; i <= 1011; i++) {
                if (i == 1006) continue;
                String key = "NOFR_" + i + "_1";
                Object nofr = combination.get(key);
                if (nofr != null) {
                    builder.append(":").append(nofr);
                }
            }

            components.add(new CombinationComponent(builder.toString()));
            responseList.add(new CombinationResponse(attr, components));
        }

        return new CampaignCombinationResponse(responseList);
    }

    private boolean matchesRequest(Map<String, Object> combination, List<CampaignOfferRequest> requestList) {
        Set<String> combinationSet = new HashSet<>();
        combinationSet.add(combination.get("NCAMPAIGNCODE") + ":" + combination.get("BASE_NOFR"));

        for (int i = 1001; i <= 1011; i++) {
            for (int j = 1; j <= 3; j++) {
                Object ccode = combination.get("CROSS_CAMPAIGNCODE_" + i);
                Object nofr = combination.get("NOFR_" + i + "_" + j);
                if (ccode != null && nofr != null) {
                    combinationSet.add(ccode + ":" + nofr);
                }
            }
        }

        for (CampaignOfferRequest offer : requestList) {
            String pair = offer.getNcampaign() + ":" + offer.getNofr();
            if (!combinationSet.contains(pair)) {
                return false;
            }
        }

        return true;
    }
}
