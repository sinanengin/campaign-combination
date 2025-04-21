package com.turkcell.combination.model;

import lombok.Data;

import java.util.List;

@Data
public class CombinationRequest {
    private List<CampaignOfferRequest> campaignOffers;
}
