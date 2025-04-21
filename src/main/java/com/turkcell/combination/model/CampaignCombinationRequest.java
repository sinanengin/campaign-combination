package com.turkcell.combination.model;

import lombok.Data;
import java.util.List;

@Data
public class CampaignCombinationRequest {
    private List<CampaignOfferRequest> campaignOffers;
}
