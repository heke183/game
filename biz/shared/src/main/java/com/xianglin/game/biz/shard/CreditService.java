package com.xianglin.game.biz.shard;

import com.xianglin.game.biz.shard.model.*;
import com.xianglin.game.common.dal.model.PlayerDO;

import java.util.List;

/**
 * 积分， 倍数计算
 */
public interface CreditService {

    /**
     * 结算积分
     *
     * @param winner
     * @param room
     * @param credit
     */
    CreditResponse dealCredit(Player winner, Room room, Credit credit);

    /**
     * 排行榜数据
     *
     * @return
     */
    Ranking ranking(long partyId);
}
