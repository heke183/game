package com.xianglin.game.common.dal.mapper;

import com.xianglin.game.common.dal.model.Order;
import tk.mybatis.mapper.common.BaseMapper;

public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 生成每天送游戏币的订单号码
     *
     * @return
     */
    int insertDailyAddGameCurrencyOrder(long partyId);

}
