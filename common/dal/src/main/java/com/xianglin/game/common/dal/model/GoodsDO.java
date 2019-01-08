package com.xianglin.game.common.dal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "game_landlord_goods")
public class GoodsDO {

    @Id
    private Integer id;

    private String goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 类型
     */
    private String goodsType;

    /**
     * 剩余数量
     */
    private Integer amount;

    /**
     * 价值
     */
    private Integer goodsValue;

    /**
     * 图片
     */
    private String goodsImg;

    /**
     * 价格 （xx金币）
     */
    private Integer price;

    private String isDeleted;

    private String creator;

    private String updater;

    private Date createDate;

    private Date updateDate;

}
