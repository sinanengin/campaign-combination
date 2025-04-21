package com.turkcell.combination.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CombinationResponse {
    private String attr;
    private List<CombinationComponent> components;
}
