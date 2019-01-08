package com.xianglin.game.common.dal.model;

public enum GoodsType {

    /**
     * 虚拟物品
     */
    VIRTUAL_GOODS,

    /**
     * 虚拟交易
     */
    VIRTUAL_TRAN,

    ;

    public static GoodsType parse(String goodsType) {
        for (GoodsType value : GoodsType.values()) {
            if (value.name().equals(goodsType)){
                return value;
            }
        }
        return null;
    }
}
