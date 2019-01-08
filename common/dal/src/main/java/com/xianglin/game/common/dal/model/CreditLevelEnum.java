package com.xianglin.game.common.dal.model;

public enum CreditLevelEnum {

    CreditLevel1("包身工", null, 100),
    CreditLevel2("短工", 100, 200),
    CreditLevel3("长工", 200, 400),
    CreditLevel4("佃户", 400, 600),
    CreditLevel5("贫农", 600, 800),
    CreditLevel6("渔夫", 800, 100),
    CreditLevel7("猎人", 1000, 2000),
    CreditLevel8("中农", 2000, 4000),
    CreditLevel9("富农", 4000, 6000),
    CreditLevel10("掌柜", 6000, 10000),
    CreditLevel11("商人", 10000, 20000),
    CreditLevel12("衙役", 20000, 50000),
    CreditLevel13("小财主", 50000, 100000),
    CreditLevel14("大财主", 100000, 200000),
    CreditLevel15("小地主", 200000, 500000),
    CreditLevel16("大地主", 500000, 1000000),
    CreditLevel17("知县", 1000000, 2000000),
    CreditLevel18("通判", 2000000, 5000000),
    CreditLevel19("知府", 5000000, 10000000),
    CreditLevel20("总督", 10000000, 20000000),
    CreditLevel21("巡抚", 20000000, 50000000),
    CreditLevel22("丞相", 50000000, 100000000),
    CreditLevel23("帝王", 100000000, null);

    private String name;

    private Integer minScore;

    private Integer maxScore;

    CreditLevelEnum(String name, Integer minScore, Integer maxScore) {
        this.name = name;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public String getName() {
        return name;
    }

    public static CreditLevelEnum selectCreditLevel(Integer credit) {
        if (credit == null)
            return null;
        for (CreditLevelEnum value : CreditLevelEnum.values()) {
            if (value.minScore == null) {
                if (value.maxScore > credit) {
                    return value;
                }
                continue;
            }

            if (value.maxScore == null) {
                if (value.minScore <= credit) {
                    return value;
                }
                continue;
            }

            if (value.minScore <= credit && value.maxScore > credit) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CreditLevelEnum{" +
                "name='" + name + '\'' +
                ", minScore=" + minScore +
                ", maxScore=" + maxScore +
                '}';
    }
}
