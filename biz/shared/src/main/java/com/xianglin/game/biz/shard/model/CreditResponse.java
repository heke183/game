package com.xianglin.game.biz.shard.model;

import com.xianglin.game.biz.shard.enums.RoomStatusEnum;
import com.xianglin.game.common.dal.model.PlayerDO;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class CreditResponse {

    @Builder.Default
    private String name = RoomStatusEnum.CREDIT_DEAL.getCode();

    /**
     * 赢的一方
     */
    private String win;

    /**
     *
     */
    private String winPosition;

    /**
     * 玩家最后剩余的牌
     */
    @Builder.Default
    private Map<String, List<Integer>> restCard = new HashMap<>();

    /**
     * 三个玩家结算的信息
     */
    @Builder.Default
    private List<PlayerDO> scoreInfo = new ArrayList<>();
}
