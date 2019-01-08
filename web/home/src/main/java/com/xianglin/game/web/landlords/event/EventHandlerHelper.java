package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.xianglin.game.biz.shard.PlayerService;
import com.xianglin.game.biz.shard.enums.RoomDirection;
import com.xianglin.game.biz.shard.enums.RoomStatusEnum;
import com.xianglin.game.biz.shard.model.Player;
import com.xianglin.game.biz.shard.model.Poker;
import com.xianglin.game.biz.shard.model.Room;
import com.xianglin.game.biz.shard.model.RoomStatus;
import com.xianglin.game.common.dal.mapper.RoomMapper;
import com.xianglin.game.common.dal.model.RoomDO;
import com.xianglin.game.utils.constant.LandlordConstant;
import com.xianglin.game.web.landlords.LongPollingLandlordServer;
import com.xianglin.game.web.landlords.config.LandlordServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Component
public class EventHandlerHelper {

    private static final Logger logger = LoggerFactory.getLogger(EventHandlerHelper.class);

    @Resource(name = "landlordHallServer")
    private LongPollingLandlordServer landlordHallServer;

    @Resource(name = "landlordRoomServer")
    private LongPollingLandlordServer landlordRoomServer;

    @Resource
    private PlayerService playerService;

    @Resource
    private RoomMapper roomMapper;

    public boolean clearRoom(Room room, boolean forceClear) {
       return clearRoom(room, forceClear, false);
    }

    /**
     * 结算之后清除房间信息
     *
     * @param forceClear 强制清空整个房间信息
     * @param forceExit  强制退出离线玩家
     */
    public boolean clearRoom(Room room, boolean forceClear, boolean forceExit) {

        synchronized (room) {
            if (room.isGaming() && !forceClear && !forceExit) {
                logger.debug("房间: {} 正在游戏中，不能被清理", room.getRoom());
                return false;
            }
            try {
                Map<RoomDirection, Player> playerMap = room.getPlayerMap();
                int origin = playerMap.size();

                if (!CollectionUtils.isEmpty(playerMap)) {
                    SocketIONamespace namespace = landlordRoomServer.getOriginServer().getNamespace(LandlordServerConfig.ROOM_NAME_SPACE);

                    Iterator<Map.Entry<RoomDirection, Player>> iterator = room.getPlayerMap().entrySet().iterator();
                    while (iterator.hasNext()) {
                        Player player = iterator.next().getValue();

                        // 清除房间内玩家
                        if (forceClear || namespace.getClient(player.getRoomUUID()) == null) {
                            playerService.exitRoom(player, player.getRoomUUID().toString());
                            iterator.remove();
                            AbstractEventHandler.PLAYER_MAP.remove(player.getRoomUUID());
                        }
                    }

                    // 有人退出了 通知大厅和另外玩家
                    if (!forceClear && origin > playerMap.size()) {
                        for (Map.Entry<RoomDirection, Player> entry : room.getPlayerMap().entrySet()) {
                            Player player = entry.getValue();
                            Room roomClone = room.clone();
                            roomClone.setPosition(player.getPosition());
                            logger.info("清理房间后通知 player:{} room:{}", player, roomClone);
                            namespace.getClient(player.getRoomUUID()).sendEvent(EventType.ROOM_USERS.getType(), roomClone.tranToFront());
                        }
                        notifyHallUpdate(null);
                    }
                }
                room.clearRoomInfo();

                if (origin > playerMap.size()) {
                    return true;
                }
            } catch (Exception e) {
                logger.error("clear room error", e);
            }
        }
        return false;
    }

    /**
     * 房间数据初始化
     *
     * @param room
     */
    private Player roomInit(Room room) {

        if (!room.getAHand().isEmpty()) {
            room.getAHand().clear();
        }

        // 选一个 叫地主
        Map<RoomDirection, Player> playerMap = room.getPlayerMap();
        Player landlords = playerMap.get(RoomDirection.random());

        Queue<Player> queue = room.getRobQueue();

        if (!queue.isEmpty()) {
            queue.clear();
        }

        queue.add(landlords);
        Player nextPlayer0 = room.getPlayerMap().get(landlords.getRoomDirection().next());
        Player nextPlayer1 = room.getPlayerMap().get(landlords.getRoomDirection().next().next());
        queue.add(nextPlayer0);
        queue.add(nextPlayer1);

        landlords.getRobCount().set(0);
        nextPlayer0.getRobCount().set(0);
        nextPlayer1.getRobCount().set(0);

        room.setGaming(true);
        return landlords;
    }

    /**
     * 选出三张底盘，其他的给三个玩家
     *
     * @param room
     * @return
     */
    private List<Integer> getAHand(Room room) {

        Map<RoomDirection, Player> playerMap = room.getPlayerMap();
        List<Integer> shuffle = Poker.shuffle();

        // 3个玩家发牌，模拟常规发牌顺序，谁第一个发牌就不要太追究了
        List<Integer> deck1 = new ArrayList<>(17);
        List<Integer> deck2 = new ArrayList<>(17);
        List<Integer> deck3 = new ArrayList<>(17);
        for (int i = 0; i < 17; i++) {
            deck1.add(shuffle.get(i * 3));
            deck2.add(shuffle.get(i * 3 + 1));
            deck3.add(shuffle.get(i * 3 + 2));
        }
        // 排序
        Collections.sort(deck1, (o1, o2) -> o2 - o1);
        Collections.sort(deck2, (o1, o2) -> o2 - o1);
        Collections.sort(deck3, (o1, o2) -> o2 - o1);

        Player player1 = playerMap.get(RoomDirection.LEFT_POS);
        Player player2 = playerMap.get(RoomDirection.DOWN_POS);
        Player player3 = playerMap.get(RoomDirection.RIGHT_POS);

        player1.setPokers(deck1);
        player2.setPokers(deck2);
        player3.setPokers(deck3);

        // 最后3张底牌
        List<Integer> aHand = new ArrayList<>();
        aHand.add(shuffle.get(51));
        aHand.add(shuffle.get(52));
        aHand.add(shuffle.get(53));
        Collections.sort(aHand, (o1, o2) -> o2 - o1);

        logger.debug("底牌：{}", aHand);
        return aHand;
    }

    /**
     * 发牌
     *
     * @param namespace
     * @param room
     */
    public void showCards(SocketIONamespace namespace, Room room) {

        // 如果有玩家离线，直接剔除
        if (clearRoom(room, false, true)) {
            return;
        }

        Player landlords = roomInit(room);

        // 发盘和抽出底牌
        List<Integer> aHand = getAHand(room);

        // 保存底牌x
        room.getAHand().addAll(aHand);

        // 通知叫地主
        logger.info("通知：{}, partyId:{} 叫地主", landlords.getUuid(), landlords.getPartyId());

        RoomStatus roomStatus = RoomStatus.builder()
                .name(RoomStatusEnum.ROB.getCode())
                .leftText(LandlordConstant.ROB_LEFT_BUTTON)
                .rightText(LandlordConstant.ROB_RIGHT_BUTTON)
                .position(landlords.getRoomDirection().getCode())
                .showText(room.getShowTextMap())
                .time(LandlordConstant.ROB_TIMEOUT / 1000)
                .credit(room.getCredit())
                .curPlayer(landlords)
                .room(room)
                .build();

        roomStatus.delay(LandlordConstant.ROB_TIMEOUT);

        room.getRobDelayQueue().put(roomStatus);

        // 发牌响应给客户度
        for (Map.Entry<RoomDirection, Player> entry : room.getPlayerMap().entrySet()) {
            Player player = entry.getValue();

            if (!player.isOffline()) {
                List<Integer> cards = player.getPokers();
                logger.info("给玩家：{} 发牌：{}", player, cards);
                namespace.getClient(player.getRoomUUID()).sendEvent(EventType.SHOW_CARDS.getType(), cards);

                logger.info("通知玩家：{} 房间信息(谁叫地主) ：{}", player, roomStatus);
                namespace.getClient(player.getRoomUUID()).sendEvent(EventType.ROOM_CHANGE_STATUS.getType(), roomStatus);
            }
        }
    }

    protected void exitRoom(Player player, String sessionId) throws Exception {

        Room room = player.getRoom();
        SocketIOServer originServer = landlordRoomServer.getOriginServer();
        SocketIONamespace namespace = originServer.getNamespace(LandlordServerConfig.ROOM_NAME_SPACE);

        if (room.isGaming()) {
            logger.info("用户：{}，方位：{} 强制退出房间: {}", player.getPartyId(), player.getRoomDirection(), room.getRoom());
            player.setOffline(true);
        } else {
            logger.info("用户：{}，方位：{} 退出房间: {}", player.getPartyId(), player.getRoomDirection(), room.getRoom());
            // 退出房间
            if (playerService.exitRoom(player, sessionId)) {

                player.setReady(false);
                // 清除人
                Map<RoomDirection, Player> playerMap = room.getPlayerMap();
                if (playerMap.remove(player.getRoomDirection(), player)) {
                    AbstractEventHandler.PLAYER_MAP.remove(player.getRoomUUID(), player);
                    // 通知其他人
                    for (Map.Entry<RoomDirection, Player> entry : playerMap.entrySet()) {
                        Player value = entry.getValue();
                        if (player != value) {
                            if (room.isGaming()) {
                                // 强制退出 变成未准备
                                value.setReady(false);
                            }
                            Room clone = room.clone();
                            clone.setPosition(value.getPosition());
                            logger.info("退出房间后 player:{} room:{}", player, clone);
                            namespace.getClient(value.getRoomUUID()).sendEvent(EventType.ROOM_USERS.getType(), clone.tranToFront());
                        }
                    }
                    notifyHallUpdate(null);
                }
            }
        }
    }

    public void setButton(RoomStatus roomStatus, Room room) {
        if (room.isRob()) {
            roomStatus.setLeftText(LandlordConstant.ROB_NEXT_LEFT_BUTTON);
            roomStatus.setRightText(LandlordConstant.ROB_NEXT_RIGHT_BUTTON);
        } else {
            roomStatus.setLeftText(LandlordConstant.ROB_LEFT_BUTTON);
            roomStatus.setRightText(LandlordConstant.ROB_RIGHT_BUTTON);
        }
    }

    /**
     * todo cache？
     *
     * @param client
     */
    public void notifyHallUpdate(SocketIOClient client) {
        List<RoomDO> roomDOList = roomMapper.selectRooms();
        List<Room> rooms = new ArrayList<>();
        for (RoomDO room : roomDOList) {
            Room roomVO = Room.builder().room(room.getRoom()).build();

            if (room.getLeftPos() != null) {
                Player player = AbstractEventHandler.PLAYER_MAP.get(UUID.fromString(room.getLeftPos()));
                roomVO.setLeftPos(player);
            }

            if (room.getDownPos() != null) {
                Player player = AbstractEventHandler.PLAYER_MAP.get(UUID.fromString(room.getDownPos()));
                roomVO.setDownPos(player);
            }

            if (room.getRightPos() != null) {
                Player player = AbstractEventHandler.PLAYER_MAP.get(UUID.fromString(room.getRightPos()));
                roomVO.setRightPos(player);
            }
            rooms.add(roomVO);
        }
        if (client == null) {
            SocketIOServer originServer = landlordHallServer.getOriginServer();
            SocketIONamespace namespace = originServer.getNamespace(LandlordServerConfig.HALL_NAME_SPACE);
            namespace.getBroadcastOperations().sendEvent(EventType.ROOM_LIST.getType(), rooms);
        } else {
            client.sendEvent(EventType.ROOM_LIST.getType(), rooms);
        }
    }
}
