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
@Table(name = "game_landlord_order")
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    private Integer id;

    private Long partyId;

    private String orderId;

    private Date orderDate;

    private Date endDate;

    private String orderStatus;

    private String goodsId;

    private Integer spendGoldCoins;

    private Integer goodsValue;

    @Builder.Default
    private String isDeleted = "0";

    private String creator;

    private String updater;

    private Date createDate;

    private Date updateDate;
}
