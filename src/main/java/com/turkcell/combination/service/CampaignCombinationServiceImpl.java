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

    @Autowired
    private CampaignCombinationRepository repository;

    @Override
    public CampaignCombinationResponse getValidCombinations(CampaignCombinationRequest request) {
        // Boş request kontrolü
        if (request.getCampaignOffers() == null || request.getCampaignOffers().isEmpty()) {
            logger.warn("⚠️ Gelen request boş! Hiç kampanya/teklif içermiyor.");
            return new CampaignCombinationResponse(Collections.emptyList());
        }

        logger.info("📥 Gelen request {} kampanya-teklif içeriyor.", request.getCampaignOffers().size());

        List<Map<String, Object>> allCombinations = repository.findMatchingOffers();
        List<CombinationResponse> responseList = new ArrayList<>();

        Set<String> requestedPairs = buildRequestSet(request.getCampaignOffers());

        logger.info("🔍 Toplam {} kombinasyon satırı incelenecek.", allCombinations.size());

        for (Map<String, Object> combination : allCombinations) {
            if (!isMatchingCombination(combination, requestedPairs)) {
                continue;
            }

            // Eşleşen kombinasyonu oluştur
            String attr = combination.get("COLLECTION_TYPE") + ":" + combination.get("SUBSCRIPTION_PERIOD");
            List<CombinationComponent> components = new ArrayList<>();

            // Tarife bilgisi (1006 kodlu)
            Object cross1006 = combination.get("CROSS_CAMPAIGNCODE_1006");
            Object nofr1006_1 = combination.get("NOFR_1006_1");
            if (cross1006 != null && nofr1006_1 != null) {
                components.add(new CombinationComponent(cross1006 + ":" + nofr1006_1));
            }

            // Ana teklif + yan teklifler
            StringBuilder offerBuilder = new StringBuilder();
            offerBuilder.append(combination.get("NCAMPAIGNCODE")).append(":").append(combination.get("BASE_NOFR"));

            for (int code : Arrays.asList(1001, 1002, 1004, 1007, 1008, 1009, 1010, 1011)) {
                String nofrKey = "NOFR_" + code + "_1";
                Object nofr = combination.get(nofrKey);
                if (nofr != null) {
                    offerBuilder.append(":").append(nofr);
                }
            }

            components.add(new CombinationComponent(offerBuilder.toString()));
            responseList.add(new CombinationResponse(attr, components));

            logger.debug("✅ Eşleşen kombinasyon eklendi: {}", attr);
        }

        logger.info("🚀 Toplam {} kombinasyon bulundu ve döndü.", responseList.size());
        return new CampaignCombinationResponse(responseList);
    }

    // İstekten gelen campaign-offer listesi Set haline getiriliyor
    private Set<String> buildRequestSet(List<CampaignOfferRequest> offerRequests) {
        Set<String> requestedPairs = new HashSet<>();
        for (CampaignOfferRequest req : offerRequests) {
            requestedPairs.add(req.getNcampaign() + ":" + req.getNofr());
        }
        return requestedPairs;
    }

    // Kombinasyonun istenen kombinasyonlarla eşleşip eşleşmediği kontrolü
    private boolean isMatchingCombination(Map<String, Object> combination, Set<String> requestedPairs) {
        Set<String> availablePairs = new HashSet<>();

        Object ncampaign = combination.get("NCAMPAIGNCODE");
        Object baseNofr = combination.get("BASE_NOFR");
        if (ncampaign != null && baseNofr != null) {
            availablePairs.add(ncampaign + ":" + baseNofr);
        }

        for (int code : Arrays.asList(1001, 1002, 1004, 1006, 1007, 1008, 1009, 1010, 1011)) {
            Object campaign = combination.get("CROSS_CAMPAIGNCODE_" + code);
            for (int i = 1; i <= 3; i++) {
                Object nofr = combination.get("NOFR_" + code + "_" + i);
                if (campaign != null && nofr != null) {
                    availablePairs.add(campaign + ":" + nofr);
                }
            }
        }

        for (String requestedPair : requestedPairs) {
            if (!availablePairs.contains(requestedPair)) {
                return false;
            }
        }

        return true;
    }
}