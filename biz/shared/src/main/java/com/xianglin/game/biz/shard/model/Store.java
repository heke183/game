package com.xianglin.game.biz.shard.model;

import com.xianglin.game.common.dal.model.GoodsDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Store {

    private List<GoodsDO> goods;

    private String goodsId;

    private StoreStatus storeStatus;
}

