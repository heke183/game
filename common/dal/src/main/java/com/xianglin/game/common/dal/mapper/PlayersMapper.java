package com.xianglin.game.common.dal.mapper;

import com.xianglin.game.common.dal.model.PlayerDO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

public interface PlayersMapper extends BaseMapper<PlayerDO> {

    /**
     * 更新头像 昵称等App的基础信息
     *
     * @param playerDO
     * @return
     */
    int updateOfLogin(PlayerDO playerDO);

    /**
     * 更新积分 游戏币等
     *
     * @param playerDO
     * @return
     */
    int updateCredit(PlayerDO playerDO);

    /**
     * 更新游戏币
     *
     * @param playerDO
     * @return
     */
    int updateGameCurrency(PlayerDO playerDO);

    /**
     * 每天送一次游戏币
     *
     * @return
     */
    int dailyAddGameCurrency(long partyId);

    /**
     * 根据大厅uuid查询用户信息
     *
     * @param uuid
     * @return
     */
    PlayerDO selectByUUID(String uuid);

    /**
     *
     * 根据大厅partyId查询用户信息
     *
     * @param partyId
     * @return
     */
    PlayerDO selectByPartyId(long partyId);

    /**
     *
     * @return
     */
    int updateOfJoinRoom(@Param("partyId") long partyId,
                         @Param("roomUuid") String roomUuid,
                         @Param("lastRoomAddress") String lastRoomAddress,
                         @Param("reconnected") boolean reconnected);

    /**
     *
     * @return
     */
    int updateOfExitRoom(@Param("partyId") long partyId,
                         @Param("roomUuid") String roomUuid
                         );


    /**
     *
     * @return
     */
    int clearAllRoomUUID();

}
