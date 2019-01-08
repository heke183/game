package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.xianglin.game.biz.shard.model.Player;
import com.xianglin.game.biz.shard.model.Room;
import com.xianglin.game.biz.shard.model.RoomStatus;
import com.xianglin.game.web.landlords.model.MessageResponse;
import com.xianglin.game.web.landlords.model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取房间的状态信息
 * @author yefei
 */
@EventHandler(eventType = EventType.ROOM_STATUS_INFO)
public class RoomStatusInfoEventHandler implements DataListener {

    private static final Logger logger = LoggerFactory.getLogger(RoomStatusInfoEventHandler.class);

    @Override
    public void onData(SocketIOClient client, Object data, AckRequest ackSender) throws Exception {

        logger.info("获取 room的状态信息");

        Player player = AbstractEventHandler.PLAYER_MAP.get(client.getSessionId());
        Room room = player.getRoom();
        room.setPosition(player.getPosition());

        RoomStatus roomStatus = room.getRobDelayQueue().peek();
        if (roomStatus == null) {
            roomStatus = room.getPlayDelayQueue().peek();
            if (roomStatus == null) {
                client.sendEvent(EventType.SHOW_MESSAGE.getType(), MessageResponse.ofFail(MessageType.reconnected_error));
                logger.warn("用户：{} 重连加入房间失败", player.getPartyId());
                return;
            }
        }
        roomStatus.recalculateTime();
        roomStatus.setReconnectedPlayer(player);
        client.sendEvent(EventType.ROOM_STATUS_INFO.getType(), roomStatus);
    }
}
