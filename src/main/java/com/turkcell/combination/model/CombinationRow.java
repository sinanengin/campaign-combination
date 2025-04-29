package com.turkcell.combination.model;

import java.io.Serializable;

public class CombinationRow implements Serializable {

    private String campaignId;
    private String ncampaigncode;
    private Integer subscriptionPeriod;
    private String collectionType;
    private String baseNofr;

    private String crossCampaignCode1001;
    private String nofr1001_1;
    private String nofr1001_2;
    private String nofr1001_3;

    private String crossCampaignCode1002;
    private String nofr1002_1;
    private String nofr1002_2;
    private String nofr1002_3;

    private String crossCampaignCode1004;
    private String nofr1004_1;
    private String nofr1004_2;
    private String nofr1004_3;

    private String crossCampaignCode1006;
    private String nofr1006_1;
    private String nofr1006_2;
    private String nofr1006_3;

    private String crossCampaignCode1007;
    private String nofr1007_1;
    private String nofr1007_2;
    private String nofr1007_3;

    private String crossCampaignCode1008;
    private String nofr1008_1;
    private String nofr1008_2;
    private String nofr1008_3;

    private String crossCampaignCode1010;
    private String nofr1010_1;
    private String nofr1010_2;
    private String nofr1010_3;

    private String crossCampaignCode1011;
    private String nofr1011_1;
    private String nofr1011_2;
    private String nofr1011_3;

    private String crossCampaignCode1009;
    private String nofr1009_1;
    private String nofr1009_2;
    private String nofr1009_3;

    // === Standart Getters/Setters ===

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getNcampaigncode() { return ncampaigncode; }
    public void setNcampaigncode(String ncampaigncode) { this.ncampaigncode = ncampaigncode; }

    public Integer getSubscriptionPeriod() { return subscriptionPeriod; }
    public void setSubscriptionPeriod(Integer subscriptionPeriod) { this.subscriptionPeriod = subscriptionPeriod; }

    public String getCollectionType() { return collectionType; }
    public void setCollectionType(String collectionType) { this.collectionType = collectionType; }

    public String getBaseNofr() { return baseNofr; }
    public void setBaseNofr(String baseNofr) { this.baseNofr = baseNofr; }

    public void setCrossCampaignCode1001(String value) { this.crossCampaignCode1001 = value; }
    public void setNofr1001_1(String value) { this.nofr1001_1 = value; }
    public void setNofr1001_2(String value) { this.nofr1001_2 = value; }
    public void setNofr1001_3(String value) { this.nofr1001_3 = value; }

    public void setCrossCampaignCode1002(String value) { this.crossCampaignCode1002 = value; }
    public void setNofr1002_1(String value) { this.nofr1002_1 = value; }
    public void setNofr1002_2(String value) { this.nofr1002_2 = value; }
    public void setNofr1002_3(String value) { this.nofr1002_3 = value; }

    public void setCrossCampaignCode1004(String value) { this.crossCampaignCode1004 = value; }
    public void setNofr1004_1(String value) { this.nofr1004_1 = value; }
    public void setNofr1004_2(String value) { this.nofr1004_2 = value; }
    public void setNofr1004_3(String value) { this.nofr1004_3 = value; }

    public void setCrossCampaignCode1006(String value) { this.crossCampaignCode1006 = value; }
    public void setNofr1006_1(String value) { this.nofr1006_1 = value; }
    public void setNofr1006_2(String value) { this.nofr1006_2 = value; }
    public void setNofr1006_3(String value) { this.nofr1006_3 = value; }

    public void setCrossCampaignCode1007(String value) { this.crossCampaignCode1007 = value; }
    public void setNofr1007_1(String value) { this.nofr1007_1 = value; }
    public void setNofr1007_2(String value) { this.nofr1007_2 = value; }
    public void setNofr1007_3(String value) { this.nofr1007_3 = value; }

    public void setCrossCampaignCode1008(String value) { this.crossCampaignCode1008 = value; }
    public void setNofr1008_1(String value) { this.nofr1008_1 = value; }
    public void setNofr1008_2(String value) { this.nofr1008_2 = value; }
    public void setNofr1008_3(String value) { this.nofr1008_3 = value; }

    public void setCrossCampaignCode1010(String value) { this.crossCampaignCode1010 = value; }
    public void setNofr1010_1(String value) { this.nofr1010_1 = value; }
    public void setNofr1010_2(String value) { this.nofr1010_2 = value; }
    public void setNofr1010_3(String value) { this.nofr1010_3 = value; }

    public void setCrossCampaignCode1011(String value) { this.crossCampaignCode1011 = value; }
    public void setNofr1011_1(String value) { this.nofr1011_1 = value; }
    public void setNofr1011_2(String value) { this.nofr1011_2 = value; }
    public void setNofr1011_3(String value) { this.nofr1011_3 = value; }

    public void setCrossCampaignCode1009(String value) { this.crossCampaignCode1009 = value; }
    public void setNofr1009_1(String value) { this.nofr1009_1 = value; }
    public void setNofr1009_2(String value) { this.nofr1009_2 = value; }
    public void setNofr1009_3(String value) { this.nofr1009_3 = value; }

    // === Yardımcı Get ===

    public String getCrossCampaignCodeByCode(int code) {
        return switch (code) {
            case 1001 -> crossCampaignCode1001;
            case 1002 -> crossCampaignCode1002;
            case 1004 -> crossCampaignCode1004;
            case 1006 -> crossCampaignCode1006;
            case 1007 -> crossCampaignCode1007;
            case 1008 -> crossCampaignCode1008;
            case 1009 -> crossCampaignCode1009;
            case 1010 -> crossCampaignCode1010;
            case 1011 -> crossCampaignCode1011;
            default -> null;
        };
    }

    public String getNofrByCode(int code) {
        return getNofrByCodeAndIndex(code, 1);
    }

    public String getNofrByCodeAndIndex(int code, int index) {
        return switch (code) {
            case 1001 -> getNofrValue(nofr1001_1, nofr1001_2, nofr1001_3, index);
            case 1002 -> getNofrValue(nofr1002_1, nofr1002_2, nofr1002_3, index);
            case 1004 -> getNofrValue(nofr1004_1, nofr1004_2, nofr1004_3, index);
            case 1006 -> getNofrValue(nofr1006_1, nofr1006_2, nofr1006_3, index);
            case 1007 -> getNofrValue(nofr1007_1, nofr1007_2, nofr1007_3, index);
            case 1008 -> getNofrValue(nofr1008_1, nofr1008_2, nofr1008_3, index);
            case 1009 -> getNofrValue(nofr1009_1, nofr1009_2, nofr1009_3, index);
            case 1010 -> getNofrValue(nofr1010_1, nofr1010_2, nofr1010_3, index);
            case 1011 -> getNofrValue(nofr1011_1, nofr1011_2, nofr1011_3, index);
            default -> null;
        };
    }

    private String getNofrValue(String val1, String val2, String val3, int index) {
        return switch (index) {
            case 1 -> val1;
            case 2 -> val2;
            case 3 -> val3;
            default -> null;
        };
    }
}