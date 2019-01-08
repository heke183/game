package com.xianglin.game.web.landlords;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ExceptionListenerAdapter;
import com.xianglin.game.biz.shard.exception.PokerException;
import com.xianglin.game.biz.shard.model.Room;
import com.xianglin.game.biz.shard.model.RoomStatus;
import com.xianglin.game.web.landlords.event.EventHandlerHelper;
import com.xianglin.game.web.landlords.event.EventType;
import com.xianglin.game.web.landlords.model.LandLordResponse;
import com.xianglin.game.web.landlords.model.MessageResponse;
import com.xianglin.game.web.landlords.model.MessageType;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ExceptionResolve extends ExceptionListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionResolve.class);

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void onEventException(Exception e, List<Object> data, SocketIOClient client) {
        if (e instanceof PokerException) {
            client.sendEvent(EventType.CHECK_CARD.getType(), LandLordResponse.ofFail("所选牌不符合出牌规则"));
            return;
        } else {
            logger.error("未知错误", e);
            client.sendEvent(EventType.SHOW_MESSAGE.getType(), MessageResponse.ofFail(MessageType.error));
        }

        if (!CollectionUtils.isEmpty(data)) {
            Object datum = data.get(0);
            EventHandlerHelper eventHandlerHelper = applicationContext.getBean(EventHandlerHelper.class);

            if (datum instanceof RoomStatus) {
                RoomStatus roomStatus = (RoomStatus) datum;
                // 未知异常，解散掉房间
                Room room = roomStatus.getRoom();
                if (room != null) {
                    logger.error("房间内未知错误，解散房间", e);
                    eventHandlerHelper.clearRoom(room, true);
                }
            } else {
                // TODO
            }
        }

    }

    @Override
    public void onDisconnectException(Exception e, SocketIOClient client) {
        logger.error("", e);
    }

    @Override
    public void onConnectException(Exception e, SocketIOClient client) {
        logger.error("", e);
    }

    @Override
    public void onMessageException(Exception e, String data, SocketIOClient client) {
        logger.error("", e);
    }

    @Override
    public void onJsonException(Exception e, Object data, SocketIOClient client) {
        logger.error("", e);
    }

    @Override
    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        return super.exceptionCaught(ctx, e);
    }
}
