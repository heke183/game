package com.xianglin.game.biz.shard.enums;

public enum RoomStatusEnum {

    /**
     * 叫地主
     */
    ROB("rob", "rob"),

    /**
     * 抢地主
     */
    ROB_NEXT("robNext", "rob"),

    /**
     * 显示底牌
     */
    THREE_CARD("threeCard"),

    /**
     * 出牌
     */
    CARD_NEXT("cardNext", "cardNext"),

    /**
     * 积分结算
     */
    CREDIT_DEAL("totalScore")
    ;

    private String code;

    private String equalsCode;

    public String getEqualsCode() {
        return equalsCode;
    }

    public String getCode() {
        return code;
    }

    RoomStatusEnum(String code) {
        this.code = code;
    }

    RoomStatusEnum(String code, String equalsCode) {
        this.code = code;
        this.equalsCode = equalsCode;
    }

    public static RoomStatusEnum parse(String code) {

        for (RoomStatusEnum value : RoomStatusEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
