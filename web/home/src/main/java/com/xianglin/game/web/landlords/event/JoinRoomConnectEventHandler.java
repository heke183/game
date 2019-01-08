package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.xianglin.game.biz.shard.PlayerService;
import com.xianglin.game.biz.shard.enums.RoomDirection;
import com.xianglin.game.biz.shard.exception.LandlordException;
import com.xianglin.game.biz.shard.exception.RoomException;
import com.xianglin.game.biz.shard.model.Player;
import com.xianglin.game.biz.shard.model.Room;
import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.mapper.RoomMapper;
import com.xianglin.game.common.dal.model.PlayerDO;
import com.xianglin.game.common.dal.model.RoomDO;
import com.xianglin.game.web.landlords.constant.RedisKey;
import com.xianglin.game.web.landlords.model.JoinRoomVo;
import com.xianglin.game.web.landlords.model.LandLordResponse;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

@EventHandler(eventType = EventType.JOIN_ROOM_CONNECT)
public class JoinRoomConnectEventHandler extends AbstractEventHandler implements DataListener<JoinRoomVo> {

    private static final Logger logger = LoggerFactory.getLogger(JoinRoomConnectEventHandler.class);

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private EventHandlerHelper eventHandlerHelper;

    @Resource
    private PlayersMapper playersMapper;

    @Resource
    private PlayerService playerService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void onData(SocketIOClient client, JoinRoomVo data, AckRequest ackSender) throws Exception {
        logger.info("{} 真正加入房间", client.getSessionId());

        // 同一客户度多次点击问题
        RLock lock = redissonClient.getLock(RedisKey.JOIN_ROOM_LOCK + data.getRoomUUID());
        if (!lock.tryLock()) {
            return;
        }
        try {
            PlayerDO playerDO = playersMapper.selectByUUID(data.getRoomUUID().toString());

            Room room;
            Player player;

            if (playerDO.getRoomUuid() != null) {
                player = PLAYER_MAP.get(UUID.fromString(playerDO.getRoomUuid()));
                if (player == null) {
                    throw new RoomException("加入房间失败");
                }

                Player playerOld = PLAYER_MAP.putIfAbsent(client.getSessionId(), player);
                // 不允许用一个房间连接的多次请求
                if (playerOld != null) {
                    return;
                }

                player.setUuid(data.getRoomUUID());
                player.setRoomUUID(client.getSessionId());
                player.setOffline(false);

                room = player.getRoom();

                joinRoom(client, room, player, true);
            } else {

                // 快速加入挑一个合适的房间
                if (data.getRoom() == null) {
                    room = selectSuitableRoom();
                } else {
                    room = Room.builder().room(data.getRoom()).position(data.getPosition()).build();
                }
                RoomDirection roomDirection = RoomDirection.parse(room.getPosition());

                Room oldRoom = ROOM_MAP.putIfAbsent(room.getRoom(), room);
                if (oldRoom != null) {
                    room = oldRoom;
                }

                player = Player.builder()
                        .partyId(playerDO.getPartyId())
                        .uuid(data.getRoomUUID())
                        .name(playerDO.getShowName())
                        .headImg(playerDO.getHeadImg())
                        .score(playerDO.getCredit())
                        .lastRoomAddress(data.getRoomAddress())
                        .money(playerDO.getGameCurrency()).build();

                // 设置房间位置相关信息
                player.setRoomDirection(roomDirection);
                player.setPosition(roomDirection.getCode());
                player.setRoom(room);
                player.setRoomUUID(client.getSessionId());

                // 缓存玩家
                Player playerOld = PLAYER_MAP.putIfAbsent(client.getSessionId(), player);
                // 不允许用一个房间连接的多次请求
                if (playerOld != null) {
                    return;
                }

                joinRoom(client, room, player, false);
            }

        } catch (Exception e) {
            client.sendEvent(EventType.JOIN_ROOM_CONNECT.getType(), LandLordResponse.ofFail("加入房间失败"));
            throw e;
        } finally {
            lock.unlock();
        }
    }

    private void joinRoom(SocketIOClient client, Room room, Player player, boolean reconnected) throws Exception {
        if (reconnected) {
            logger.info("玩家{} 重连加入房间 位置{}", player.getPartyId(), player.getRoomDirection());
            playerService.joinRoom(player, true);
        } else {
            // 避免多个玩家竞争问题
            if (!playerService.joinRoom(player, false)) {
                throw new RoomException(
                        String.format("玩家[%s]加入房间失败，位置[%s], 已经存在玩家", player.getPartyId(), player.getRoomDirection().name()));
            }
            // 建立与房间关系
            room.getPlayerMap().put(player.getRoomDirection(), player);

            // 通知其他人 进来了
            for (Map.Entry<RoomDirection, Player> entry : room.getPlayerMap().entrySet()) {
                Room clone = room.clone();
                clone.setPosition(entry.getValue().getPosition());
                client.getNamespace().getClient(entry.getValue().getRoomUUID()).sendEvent(EventType.ROOM_USERS.getType(), clone.tranToFront());
            }

            // 刷新大厅
            eventHandlerHelper.notifyHallUpdate(null);
        }

        // 响应加入成功
        client.sendEvent(EventType.JOIN_ROOM_CONNECT.getType(), LandLordResponse.ofSuccess());
    }

    private Room selectSuitableRoom() throws Exception {
        RoomDO roomDO = roomMapper.selectSuitableRoom();
        if (roomDO == null) {
            throw new LandlordException("房间没有剩余位置!");
        }
        Room room = Room.builder().build();
        room.setRoom(roomDO.getRoom());
        if (roomDO.getLeftPos() == null) {
            room.setPosition(RoomDirection.LEFT_POS.getCode());
        } else if (roomDO.getDownPos() == null) {
            room.setPosition(RoomDirection.DOWN_POS.getCode());
        } else {
            room.setPosition(RoomDirection.RIGHT_POS.getCode());
        }
        return room;
    }
}

