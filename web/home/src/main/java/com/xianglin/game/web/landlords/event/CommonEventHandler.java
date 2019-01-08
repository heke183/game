package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.xianglin.game.biz.shard.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.OnMessage;

@Component
public class CommonEventHandler extends AbstractEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommonEventHandler.class);

    @Resource
    private EventHandlerHelper eventHandlerHelper;

    @OnConnect
    public void onConnect(SocketIOClient client) {
        logger.info("client: {} connect", client.getSessionId().toString());
    }

    @OnMessage
    public void onMessage(SocketIOClient client) {
        logger.info("client: {} onMessage", client.getSessionId().toString());
    }

    /**
     * onDisconnect 不可靠, 需要扫描 namespace
     *
     * @param client
     * @throws Exception
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) throws Exception {
        logger.info("client: {} disconnect", client.getSessionId().toString());
        Player player = PLAYER_MAP.get(client.getSessionId());
        if (player != null) {
            logger.info("用户：{}, partyId：{} 断开房间的连接", client.getSessionId(), player.getPartyId());
            if (player.getRoom() == null) {
                return;
            }
            eventHandlerHelper.exitRoom(player, client.getSessionId().toString());
        } else {
            logger.info("用户：{}, 断开大厅的连接", client.getSessionId());
        }
    }
}
