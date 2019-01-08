package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.xianglin.game.biz.shard.model.RoomStatus;
import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.model.PlayerDO;
import com.xianglin.game.web.landlords.model.LandLordResponse;
import com.xianglin.game.biz.shard.model.Player;
import com.xianglin.game.biz.shard.model.Room;
import com.xianglin.game.biz.shard.enums.RoomDirection;
import com.xianglin.game.web.landlords.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import static com.xianglin.game.web.landlords.model.MessageType.game_currency_not_enough;

/**
 * 用户准备事件，三人准备之后直接发牌
 *
 * @author yefei
 */
@EventHandler(eventType = EventType.USER_ROOM_READY)
public class UserRoomReadyEventHandler extends AbstractEventHandler implements DataListener {

    private static final Logger logger = LoggerFactory.getLogger(UserRoomReadyEventHandler.class);

    @Resource
    private EventHandlerHelper eventHandlerHelper;

    @Resource
    private PlayersMapper playersMapper;

    @Override
    public void onData(SocketIOClient client, Object data, AckRequest ackSender) throws Exception {
        logger.info("用户：{} 准备", client.getSessionId());
        Player player = PLAYER_MAP.get(client.getSessionId());
        if (player.isReady()) {
            logger.warn("用户：{} 已经准备", client.getSessionId());
            return;
        }

        PlayerDO playerDO = playersMapper.selectByPartyId(player.getPartyId());

        if (playerDO.getGameCurrency() < 1000) {
            // 游戏币不足
            client.sendEvent(EventType.SHOW_MESSAGE.getType(), MessageResponse.ofFail(game_currency_not_enough));
            logger.info("用户：{} 金币不足, 剔出房间", playerDO.getPartyId());
            eventHandlerHelper.exitRoom(player, player.getRoomUUID().toString());
            return;
        }

        player.setReady(true);
        client.sendEvent(EventType.USER_ROOM_READY.getType(), LandLordResponse.ofSuccess());

        Room room = player.getRoom();

        boolean allReady = true;
        for (RoomDirection roomDirection : room.getPlayerMap().keySet()) {
            Player temp = room.getPlayerMap().get(roomDirection);
            if (!(allReady = allReady && temp.isReady()))
                break;
        }

        // 三人都准备了
        if (allReady && room.getPlayerMap().size() == 3) {

            synchronized (room) {
                if (room.getRobQueueTakeThread() == null) {
                    // 叫地主队列线程
                    Thread thread = new Thread(() -> {
                        while (true) {
                            try {
                                RoomStatus roomStatus = room.getRobDelayQueue().take();
                                executorService.submit(new RobTimeOutTask(roomStatus));
                            } catch (InterruptedException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    });
                    thread.setName("ROB_DELAY_QUEUE#" + room.getRoom());
                    thread.start();
                    room.setRobQueueTakeThread(thread);
                }

                if (room.getPlayQueueTakeThread() == null) {
                    // 出牌队列线程
                    Thread thread = new Thread(() -> {
                        while (true) {
                            try {
                                RoomStatus roomStatus = room.getPlayDelayQueue().take();
                                executorService.submit(new PlayTimeOutTask(roomStatus));
                            } catch (InterruptedException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    });
                    thread.setName("PLAY_DELAY_QUEUE#" + room.getRoom());
                    thread.start();
                    room.setPlayQueueTakeThread(thread);
                }
            }
            room.tranToFront();
            eventHandlerHelper.showCards(client.getNamespace(), room);
        } else {
            // 通知
            for (RoomDirection roomDirection : room.getPlayerMap().keySet()) {
                Player p = room.getPlayerMap().get(roomDirection);
                Room roomClone = room.clone();
                roomClone.setPosition(p.getPosition());
                logger.info("准备之后通知 player:{} room:{}", p, roomClone);
                client.getNamespace().getClient(p.getRoomUUID()).sendEvent(EventType.ROOM_USERS.getType(), roomClone.tranToFront());
            }
        }
    }
}
