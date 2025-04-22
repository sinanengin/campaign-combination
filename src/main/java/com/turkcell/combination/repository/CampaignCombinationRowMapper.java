package com.turkcell.combination.repository;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CampaignCombinationRowMapper implements RowMapper<Map<String, Object>> {

    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Object> row = new HashMap<>();

        row.put("CAMPAIGN_ID", rs.getObject("CAMPAIGN_ID"));
        row.put("NCAMPAIGNCODE", rs.getObject("NCAMPAIGNCODE"));
        row.put("SUBSCRIPTION_PERIOD", rs.getObject("SUBSCRIPTION_PERIOD"));
        row.put("COLLECTION_TYPE", rs.getObject("COLLECTION_TYPE"));
        row.put("BASE_NOFR", rs.getObject("BASE_NOFR"));

        String[] codes = { "1001", "1002", "1004", "1006", "1007", "1008", "1009", "1010", "1011" };

        for (String code : codes) {
            row.put("CROSS_CAMPAIGNCODE_" + code, rs.getObject("CROSS_CAMPAIGNCODE_" + code));
            for (int i = 1; i <= 3; i++) {
                row.put("NOFR_" + code + "_" + i, rs.getObject("NOFR_" + code + "_" + i));
            }
        }

        return row;
    }
}