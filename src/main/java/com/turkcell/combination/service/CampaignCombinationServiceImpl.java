package com.turkcell.combination.service;

import com.turkcell.combination.model.*;
import com.turkcell.combination.repository.CampaignCombinationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CampaignCombinationServiceImpl implements CampaignCombinationService {

    private static final Logger logger = LoggerFactory.getLogger(CampaignCombinationServiceImpl.class);

    private static final List<Integer> OFFER_CODES = Arrays.asList(1001, 1002, 1004, 1006, 1007, 1008, 1009, 1010, 1011);

    @Autowired
    private CampaignCombinationRepository repository;

    @Override
    public CampaignCombinationResponse getValidCombinations(CampaignCombinationRequest request) {
        if (request.getCampaignOffers() == null || request.getCampaignOffers().isEmpty()) {
            logger.warn("⚠️ Boş request alındı.");
            return new CampaignCombinationResponse(Collections.emptyList());
        }

        logger.info("📥 Gelen request: {} kampanya-teklif çifti", request.getCampaignOffers().size());

        Set<String> requestedKeys = buildRequestedKeySet(request.getCampaignOffers());
        List<Map<String, Object>> candidateRows = repository.findMatchingOffers(new ArrayList<>(requestedKeys));

        logger.info("🔍 Redis üzerinden {} satır alındı", candidateRows.size());

        List<CombinationResponse> result = new ArrayList<>();
        for (Map<String, Object> row : candidateRows) {
            if (!containsAllRequestedPairs(row, requestedKeys)) continue;

            String attr = row.get("COLLECTION_TYPE") + ":" + row.get("SUBSCRIPTION_PERIOD");
            List<CombinationComponent> components = new ArrayList<>();

            // 1006 (tarife) bileşeni
            addTariffComponent(row, components);

            // Ana ve yan teklifleri bileşene ekle
            components.add(new CombinationComponent(buildOfferString(row)));

            result.add(new CombinationResponse(attr, components));
        }

        logger.info("🚀 Response olarak toplam {} kombinasyon döndü", result.size());
        return new CampaignCombinationResponse(result);
    }

    private Set<String> buildRequestedKeySet(List<CampaignOfferRequest> offers) {
        Set<String> keys = new HashSet<>();
        for (CampaignOfferRequest offer : offers) {
            keys.add(offer.getNcampaign() + ":" + offer.getNofr());
        }
        return keys;
    }

    private boolean containsAllRequestedPairs(Map<String, Object> row, Set<String> requestedKeys) {
        Set<String> rowKeys = new HashSet<>();

        Object baseCamp = row.get("NCAMPAIGNCODE");
        Object baseNofr = row.get("BASE_NOFR");
        if (baseCamp != null && baseNofr != null) {
            rowKeys.add(baseCamp + ":" + baseNofr);
        }

        for (int code : OFFER_CODES) {
            Object camp = row.get("CROSS_CAMPAIGNCODE_" + code);
            Object nofr = row.get("NOFR_" + code + "_1");
            if (camp != null && nofr != null) {
                rowKeys.add(camp + ":" + nofr);
            }
        }

        return rowKeys.containsAll(requestedKeys);
    }

    private void addTariffComponent(Map<String, Object> row, List<CombinationComponent> components) {
        Object camp = row.get("CROSS_CAMPAIGNCODE_1006");
        Object nofr = row.get("NOFR_1006_1");
        if (camp != null && nofr != null) {
            components.add(new CombinationComponent(camp + ":" + nofr));
        }
    }

    private String buildOfferString(Map<String, Object> row) {
        StringBuilder builder = new StringBuilder();
        builder.append(row.get("NCAMPAIGNCODE")).append(":").append(row.get("BASE_NOFR"));

        for (int code : OFFER_CODES) {
            if (code == 1006) continue; // Tarife zaten ayrı eklendi
            Object nofr = row.get("NOFR_" + code + "_1");
            if (nofr != null) {
                builder.append(":").append(nofr);
            }
        }
        return builder.toString();
    }
}