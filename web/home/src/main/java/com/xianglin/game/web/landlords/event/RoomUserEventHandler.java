package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.xianglin.game.biz.shard.model.Player;
import com.xianglin.game.biz.shard.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 房间内用户数据（不含房间状态）
 *
 */
@EventHandler(eventType = EventType.ROOM_USERS)
public class RoomUserEventHandler extends AbstractEventHandler implements DataListener {

    private static final Logger logger = LoggerFactory.getLogger(RoomUserEventHandler.class);

    @Override
    public void onData(SocketIOClient client, Object data, AckRequest ackSender) throws Exception {
        logger.info("同步房间内 用户信息");

        Player player = PLAYER_MAP.get(client.getSessionId());
        Room room = player.getRoom();
        room = room.clone();
        room.setPosition(player.getRoomDirection().getCode());

        client.getNamespace().getClient(client.getSessionId()).sendEvent(EventType.ROOM_USERS.getType(), room.tranToFront());
    }
}
