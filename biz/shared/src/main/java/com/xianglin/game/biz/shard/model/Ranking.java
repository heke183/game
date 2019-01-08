package com.xianglin.game.biz.shard.model;

import com.xianglin.game.common.dal.model.PlayerDO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Ranking {

    /**
     * 本人在周排行榜单中的位置
     */
    private PlayerDO weekRankingSelf;

    /**
     * 本人在总排行榜单中的位置
     */
    private PlayerDO totalRankingSelf;

    /**
     * 周排行
     */
    private List<PlayerDO> weekRanking;

    /*
     * 总排行
     */
    private List<PlayerDO> totalRanking;
}
