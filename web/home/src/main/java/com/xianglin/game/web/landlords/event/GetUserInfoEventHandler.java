package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.xianglin.game.biz.shard.model.Player;
import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.model.PlayerDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

@EventHandler(eventType = EventType.USER_INFO)
public class GetUserInfoEventHandler extends AbstractEventHandler implements DataListener<Player> {

    private static final Logger logger = LoggerFactory.getLogger(GetUserInfoEventHandler.class);

    @Resource
    private PlayersMapper playersMapper;

    @Override
    public void onData(SocketIOClient client, Player data, AckRequest ackSender) throws Exception {
        logger.info("{} 获取用户信息", client.getSessionId());

        Player player = PLAYER_MAP.get(client.getSessionId());

        // player is null 是通过大厅的连接发过来的请求
        if (player == null) {
            PlayerDO playerDO = playersMapper.selectByUUID(client.getSessionId().toString());
            player = Player.builder()
                    .uuid(client.getSessionId())
                    .headImg(playerDO.getHeadImg())
                    .name(playerDO.getShowName())
                    .money(playerDO.getGameCurrency())
                    .score(playerDO.getCredit())
                    .creditLevel(playerDO.getCreditLevel())
                    .build();
        }

        client.sendEvent(EventType.USER_INFO.getType(), player);
    }
}
