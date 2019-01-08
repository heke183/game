package com.xianglin.game.common.dal.mapper;

import com.xianglin.game.common.dal.model.PlayRecord;
import com.xianglin.game.common.dal.model.PlayerDO;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

public interface PlayRecordMapper extends BaseMapper<PlayRecord> {

    /**
     * 查询自己在周榜单中的排名
     *
     * @param partyId
     * @return
     */
    PlayerDO weekRankingSelf(long partyId);

    /**
     * 查询自己在总榜单中的排名
     *
     * @param partyId
     * @return
     */
    PlayerDO totalRankingSelf(long partyId);

    /**
     * 周排行 取前30
     *
     * @return
     */
    List<PlayerDO> weekRanking(long partyId);

    /**
     * 总排行 取前50
     *
     * @return
     */
    List<PlayerDO> totalRanking(long partyId);

    /**
     * 插入记录
     *
     * @param playRecord
     * @return
     */
    int insertRecord(PlayRecord playRecord);
}
