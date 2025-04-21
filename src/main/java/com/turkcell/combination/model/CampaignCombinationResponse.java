package com.turkcell.combination.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CampaignCombinationResponse {
    private List<CombinationResponse> combinations;
}
