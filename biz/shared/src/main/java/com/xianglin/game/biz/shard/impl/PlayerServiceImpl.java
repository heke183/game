package com.xianglin.game.biz.shard.impl;

import com.xianglin.game.biz.shard.PlayerService;
import com.xianglin.game.biz.shard.model.Player;
import com.xianglin.game.common.dal.mapper.OrderMapper;
import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.mapper.RoomMapper;
import com.xianglin.game.common.dal.model.CreditLevelEnum;
import com.xianglin.game.common.dal.model.PlayerDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

    @Autowired
    private PlayersMapper playersMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Override
    @Transactional
    public int dailyAddGameCurrency(long partyId) {

        int i = playersMapper.dailyAddGameCurrency(partyId);
        int result = 0;
        if (i == 1) {
            result = orderMapper.insertDailyAddGameCurrencyOrder(partyId);
            logger.info("用户：{} 第一次登录，赠送一次游戏币", partyId);
        }
        return result;
    }

    @Override
    @Transactional
    public void updateOfLoginOrInsert(PlayerDO playerDO) {
        int update = playersMapper.updateOfLogin(playerDO);
        if (update == 0) {
            playerDO.setCreditLevel(CreditLevelEnum.CreditLevel1.getName());
            playerDO.setCreator(playerDO.getShowName());
            playerDO.setUpdater(playerDO.getShowName());
            playerDO.setCreateDate(new Date());
            playerDO.setUpdateDate(new Date());
            playersMapper.insertSelective(playerDO);

            logger.info("用户：{} 第一次进入斗地主", playerDO.getPartyId());
        }
    }

    @Override
    @Transactional
    public boolean joinRoom(Player player, boolean reconnected) {
        int i = playersMapper.updateOfJoinRoom(
                player.getPartyId(),
                player.getRoomUUID().toString(),
                player.getLastRoomAddress(),
                reconnected);
        if (i == 1) {
            int join = roomMapper.join(
                    player.getRoom().getRoom(),
                    player.getRoomDirection().name(),
                    player.getRoomUUID().toString(),
                    reconnected);

            if (join == 0) {
                playersMapper.updateOfExitRoom(player.getPartyId(), player.getRoomUUID().toString());
            }
            return join == 1;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean exitRoom(Player player, String sessionId) {
        playersMapper.updateOfExitRoom(player.getPartyId(), sessionId);
        int join = roomMapper.exit(
                player.getRoom().getRoom(),
                player.getRoomDirection().name());
        return join == 1;
    }
}
