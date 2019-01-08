package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.xianglin.game.biz.shard.CreditService;
import com.xianglin.game.biz.shard.model.Ranking;
import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.model.PlayerDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@EventHandler(eventType = EventType.RANKING)
public class RankingEventHandler implements DataListener {

    private static final Logger logger = LoggerFactory.getLogger(RankingEventHandler.class);

    @Autowired
    private PlayersMapper playersMapper;

    @Autowired
    private CreditService creditService;

    @Override
    public void onData(SocketIOClient client, Object data, AckRequest ackSender) throws Exception {

        PlayerDO playerDO = playersMapper.selectByUUID(client.getSessionId().toString());
        logger.info("用户: {} 获取排行榜信息", playerDO.getPartyId());
        Ranking ranking = creditService.ranking(playerDO.getPartyId());

        client.sendEvent(EventType.RANKING.getType(), ranking);
    }
}
