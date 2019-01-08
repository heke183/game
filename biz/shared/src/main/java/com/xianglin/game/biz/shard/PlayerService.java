package com.xianglin.game.biz.shard;

import com.xianglin.game.biz.shard.model.Player;
import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.model.PlayerDO;

public interface PlayerService {

    /**
     * @see PlayersMapper#dailyAddGameCurrency(long partyId)
     *
     * @param partyId
     * @return
     */
    int dailyAddGameCurrency(long partyId);

    /**
     * 首次进入斗地主插入数据，或者后来更新头像等
     *
     * @param playerDO
     */
    void updateOfLoginOrInsert(PlayerDO playerDO);

    /**
     * 加入房间
     *
     * @return
     */
    boolean joinRoom(Player player, boolean reconnected);

    /**
     * 退出房间
     *
     * @return
     */
    boolean exitRoom(Player player, String sessionId);
}
