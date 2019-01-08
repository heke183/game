package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

@EventHandler(eventType = EventType.ROOM_LIST)
public class GetRoomListEventHandler extends AbstractEventHandler implements DataListener {

    private static final Logger logger = LoggerFactory.getLogger(GetRoomListEventHandler.class);

    @Resource
    private EventHandlerHelper eventHandlerHelper;

    @Override
    public void onData(SocketIOClient client, Object data, AckRequest ackSender) throws Exception {
        logger.info("{} 获取房间列表", client.getSessionId());
        eventHandlerHelper.notifyHallUpdate(client);
    }

}
