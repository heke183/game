package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.model.PlayerDO;
import com.xianglin.game.web.landlords.load.balance.LoadBalancer;
import com.xianglin.game.web.landlords.model.LandLordResponse;
import com.xianglin.game.web.landlords.model.MessageResponse;
import com.xianglin.game.web.landlords.model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

@EventHandler(eventType = EventType.JOIN_ROOM)
public class JoinRoomEventHandler implements DataListener {

    private static final Logger logger = LoggerFactory.getLogger(JoinRoomEventHandler.class);

    @Resource
    private LoadBalancer loadBalancer;

    @Resource
    private PlayersMapper playersMapper;

    @Override
    public void onData(SocketIOClient client, Object data, AckRequest ackSender) throws Exception {
        logger.info("{} 加入房间 获取连接", client.getSessionId());

        PlayerDO playerDO = playersMapper.selectByUUID(client.getSessionId().toString());

        if (playerDO == null) {
            client.sendEvent(EventType.SHOW_MESSAGE.getType(), MessageResponse.ofFail(MessageType.offline_hall));
            return;
        }

        if (playerDO.getGameCurrency() < 1000) {
            // 游戏币不足
            client.sendEvent(EventType.JOIN_ROOM.getType(), LandLordResponse.ofFail("游戏币不足"));
        } else {
            LandLordResponse landLordResponse = LandLordResponse.ofSuccess();
            String address = loadBalancer.select().getAddress();
            landLordResponse.setAddress(address);
            landLordResponse.setRoomUUID(client.getSessionId());

            client.sendEvent(EventType.JOIN_ROOM.getType(), landLordResponse);
        }
    }
}
