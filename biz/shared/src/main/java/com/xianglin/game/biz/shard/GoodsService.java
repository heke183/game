package com.xianglin.game.biz.shard;

import com.xianglin.game.biz.shard.exception.LandlordException;
import com.xianglin.game.biz.shard.model.Store;
import com.xianglin.game.common.dal.model.PlayerDO;

public interface GoodsService {

    void transaction(Store store, PlayerDO player) throws LandlordException;
}
