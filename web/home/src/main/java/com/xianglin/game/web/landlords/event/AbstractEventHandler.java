package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.xianglin.game.biz.shard.CreditService;
import com.xianglin.game.biz.shard.enums.RoomDirection;
import com.xianglin.game.biz.shard.enums.RoomStatusEnum;
import com.xianglin.game.biz.shard.model.*;
import com.xianglin.game.utils.constant.LandlordConstant;
import com.xianglin.game.web.landlords.LandlordsServer;
import com.xianglin.game.web.landlords.config.LandlordServerConfig;
import com.xianglin.game.web.landlords.exector.ExecutorFactory;
import com.xianglin.game.web.landlords.model.LandLordResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * 包含 叫地主 出牌 以及的超时后的 复用方法
 *
 * @author yefei
 */
public abstract class AbstractEventHandler implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(AbstractEventHandler.class);

    /**
     * 玩家进入桌子
     */
    static final ConcurrentMap<UUID/** room uuid */, Player> PLAYER_MAP = new ConcurrentHashMap<>();

    /**
     * 存在玩家的房间
     */
    public static final ConcurrentHashMap<Integer, Room> ROOM_MAP = new ConcurrentHashMap<>();

    @Resource(name = "landlordRoomServer")
    private LandlordsServer landlordsServer;

    @Resource
    private CreditService creditService;

    @Resource
    private EventHandlerHelper eventHandlerHelper;

    @Resource
    protected ExecutorFactory executorFactory;

    protected volatile ExecutorService executorService;

    @Override
    public void afterPropertiesSet() {
        executorService = executorFactory.createExecutorService();
    }

    /**
     * 出牌
     *
     * @param namespace
     * @param data
     * @param room
     * @param autoPlay  是否自动出牌
     */
    protected void play(SocketIONamespace namespace, RoomStatus data, Room room, Poker poker, boolean autoPlay) {
        Player curPlayer = data.getCurPlayer();
        List<Integer> cardArr = Arrays.asList(poker.getPokers());

        // 炸弹 倍数*2
        if (poker.getType() != null) {
            if (poker.getType().getType() == PokerType.FOUR_BOMB || poker.getType().getType() == PokerType.KING_BOMB) {
                notifyMultiple(namespace, room);
            }
        }

        // 删除出得牌
        if (curPlayer.getPokers().removeAll(cardArr) && !autoPlay) {
            // 通知出牌成功
            namespace.getClient(curPlayer.getRoomUUID()).sendEvent(EventType.CHECK_CARD.getType(), LandLordResponse.ofSuccess());
        }

        // 牌出完了 结算
        if (curPlayer.getPokers().size() == 0) {
            creditDeal(namespace, room, poker, curPlayer);
            return;
        }

        // 上两次的出的牌
        Map<String, Poker> prevPoker = room.getPrevPoker();
        // 记录出的牌
        prevPoker.put(curPlayer.getRoomDirection().getCode(), poker);

        notifyPlay(namespace, room, curPlayer, poker);
    }

    protected void notifyMultiple(SocketIONamespace namespace, Room room) {
        Credit credit = room.getCredit();
        credit.setMultiplePower(2);
        credit.setMultiple(credit.getMultiple() * credit.getMultiplePower());
        logger.info("room: {} 加倍: credit: {}", room.getRoom(), credit);

        for (Map.Entry<RoomDirection, Player> entry : room.getPlayerMap().entrySet()) {
            Player player = entry.getValue();
            try {
                if (!player.isOffline()) {
                    namespace.getClient(player.getRoomUUID()).sendEvent(EventType.MULTIPLE.getType(), credit.clone());
                }
            } catch (CloneNotSupportedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 积分结算
     *
     * @param namespace
     * @param room
     * @param poker
     * @param curPlayer
     */
    private void creditDeal(SocketIONamespace namespace, Room room, Poker poker, Player curPlayer) {
        Credit credit = room.getCredit();

        CreditResponse creditResponse = creditService.dealCredit(curPlayer, room, credit);

        // 最后一次通知出牌 过渡
        notifyPlay(namespace, room, curPlayer, poker);

        // 积分数据
        room.setGaming(false);
        eventHandlerHelper.clearRoom(room, false);

        Iterator<Map.Entry<RoomDirection, Player>> iterator = room.getPlayerMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next().getValue();
            player.setReady(false);
            player.setLandlords(false);

            if (!player.isOffline()) {
                namespace.getClient(player.getRoomUUID()).sendEvent(EventType.ROOM_CHANGE_STATUS.getType(), creditResponse);
            }
        }

    }

    /**
     * 通知出牌
     *
     * @param namespace
     * @param poker poker is null 第一次出牌
     */
    protected void notifyPlay(SocketIONamespace namespace,
                              Room room,
                              Player curPlayer,
                              Poker poker) {

        Player nextPlayer;
        if (poker == null) {
            nextPlayer = curPlayer;
        } else {
            nextPlayer = room.getPlayerMap().get(curPlayer.getRoomDirection().next());
        }

        // 判断是否一个轮回的第一次出牌
        boolean isFirst = false;
        Map<String, Poker> prevPoker = room.getPrevPoker();
        Poker poker0 = prevPoker.get(curPlayer.getRoomDirection().prev().getCode());
        if (poker == null || (prevPoker.size() == 3 && poker0.getPokers().length == 0 && poker.getPokers().length == 0)) {
            isFirst = true;
        }

        // 验证是否是最后一次出牌
        boolean isLast = false;
        if (curPlayer.getPokers().isEmpty()) {
            isLast = true;
        }

        // 判断是否出牌
        Boolean value = null;
        if (poker != null && poker.getPokers().length != 0) {
            value = true;
        } else if (poker != null && poker.getPokers().length == 0) {
            value = false;
        }

        for (Map.Entry<RoomDirection, Player> entry : room.getPlayerMap().entrySet()) {
            Player player = entry.getValue();

            Map<String, Poker> stringPokerMap = room.getPrevPoker();
            Map<String, List<Integer>> stringListMap = new HashMap<>();
            for (Map.Entry<String, Poker> pokerEntry : stringPokerMap.entrySet()) {
                stringListMap.put(pokerEntry.getKey(), Arrays.asList(pokerEntry.getValue().getPokers()));
            }

            RoomStatus roomStatus = RoomStatus.builder()
                    .name(RoomStatusEnum.CARD_NEXT.getCode())
                    .value(value)
                    .position(isLast ? null : nextPlayer.getRoomDirection().getCode())
                    .leftText(isFirst ? null : LandlordConstant.PLAY_LEFT_BUTTON)
                    .rightText(LandlordConstant.PLAY_RIGHT_BUTTON)
                    .time(nextPlayer.isOffline() ? 0 : LandlordConstant.PLAY_TIMEOUT / 1000)
                    .room(room)
                    .showCard(stringListMap)
                    .lastCardType(poker == null ? null : poker.getType())
                    .build();

            for (Map.Entry<RoomDirection, Player> entry_ : room.getPlayerMap().entrySet()) {
                Player player0 = entry_.getValue();
                roomStatus.getUserCardNum().put(player0.getRoomDirection().getCode(), player0.getPokers().size());
            }

            roomStatus.setCurPlayer(player);

            if (!player.isOffline()) {
                namespace.getClient(player.getRoomUUID()).sendEvent(EventType.ROOM_CHANGE_STATUS.getType(), roomStatus);
            }
            // 出牌的人 放入队列
            if (!isLast && player == nextPlayer) {
                if (player.isOffline()) {
                    roomStatus.delay(0);
                    roomStatus.setTime(0);
                } else {
                    roomStatus.delay(LandlordConstant.PLAY_TIMEOUT);
                }
                room.getPlayDelayQueue().put(roomStatus);
                logger.info("房间: {} , 轮到方位：{} 出牌，放入延迟队列，延迟到: {}", room.getRoom(), player.getRoomDirection(),
                        DateFormatUtils.format(roomStatus.getFuture(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()));
            }
        }
    }

    /**
     * 通知下一个叫地主
     *
     * @param namespace
     * @param data
     * @param room
     * @param autoRob   下一个是自动成为地主
     */
    protected void notifyNextRob(SocketIONamespace namespace,
                                 RoomStatus data,
                                 Room room,
                                 boolean autoRob) {

        Player player = data.getCurPlayer();

        if (autoRob) {
            data.setPosition(null);
            data.setTime(0);
        } else {
            Player nextPlayer = room.getRobQueue().peek();
            logger.info("{}, partyId:{} " + (data.getValue() ? "叫地主" : "不叫") + "，轮到下一个{}, partyId:{} 叫地主",
                    player.getRoomDirection(), player.getPartyId(), nextPlayer.getRoomDirection(), nextPlayer.getPartyId());
            data.setPosition(nextPlayer.getRoomDirection().getCode());
            data.setCurPlayer(nextPlayer);
            if (nextPlayer.isOffline()) {
                data.setTime(0);
                data.delay(0);
            } else {
                data.setTime(LandlordConstant.ROB_TIMEOUT / 1000);
                data.delay(LandlordConstant.ROB_TIMEOUT);
            }
        }

        data.setShowText(room.getShowTextMap());
        eventHandlerHelper.setButton(data, room);

        for (RoomDirection roomDirection : room.getPlayerMap().keySet()) {
            Player p = room.getPlayerMap().get(roomDirection);

            if (!p.isOffline()) {
                namespace.getClient(p.getRoomUUID()).sendEvent(EventType.ROOM_CHANGE_STATUS.getType(), data);
            }
        }

        if (!autoRob) {
            room.getRobDelayQueue().put(data);
        }
    }

    /**
     * @param namespace
     * @param data
     * @param room
     */
    protected void notifyNextRob(SocketIONamespace namespace,
                                 RoomStatus data,
                                 Room room) {
        notifyNextRob(namespace, data, room, false);
    }

    /**
     * 通知显示底牌
     *
     * @param namespace
     * @param landlord
     * @param room
     */
    protected void notifyShowAHand(SocketIONamespace namespace, Player landlord, Room room) {
        List<Integer> aHand = room.getAHand();
        landlord.getPokers().addAll(aHand);
        Collections.sort(landlord.getPokers(), (o1, o2) -> o2 - o1);

        // 转换给前台
        Map<String, Player> players = new HashMap<>();
        for (Map.Entry<RoomDirection, Player> entry : room.getPlayerMap().entrySet()) {
            players.put(entry.getKey().getCode(), entry.getValue());
        }

        logger.info("通知显示底牌：{}", players);
        for (String s : players.keySet()) {
            Player player = players.get(s);
            if (!player.isOffline()) {
                RoomStatus roomStatus = RoomStatus.builder()
                        .name(RoomStatusEnum.THREE_CARD.getCode())
                        .threeCard(aHand)
                        .players(players)
                        .build();

                namespace.getClient(player.getRoomUUID()).sendEvent(EventType.ROOM_CHANGE_STATUS.getType(), roomStatus);
            }
        }
    }

    /**
     * 叫地主超时 任务
     */
    class RobTimeOutTask implements Runnable {

        private final RoomStatus roomStatus;

        public RobTimeOutTask(RoomStatus roomStatus) {
            this.roomStatus = roomStatus;
        }

        @Override
        public void run() {
            SocketIOServer originServer = landlordsServer.getOriginServer();
            SocketIONamespace namespace = originServer.getNamespace(LandlordServerConfig.ROOM_NAME_SPACE);

            Player curPlayer = roomStatus.getCurPlayer();
            Room room = curPlayer.getRoom();
            // 剔除叫地主队列
            room.getRobQueue().poll();
            roomStatus.setValue(false);

            if (!room.isRob() && room.getRobQueue().size() == 0) {
                logger.info("房间 {} 没人叫地主,重新发牌", room.getRoom());
                // 没人叫地主，重新发牌，选地主
                eventHandlerHelper.showCards(namespace, room);
            } else {
                logger.info("{}, partyId:{} 叫地主超时", curPlayer.getRoomDirection(), curPlayer.getPartyId());
                // 通知下一个叫地主
                room.getShowTextMap().put(curPlayer.getRoomDirection().getCode(), LandlordConstant.ROB_LEFT_BUTTON);
                roomStatus.setValue(false);

                Queue<Player> robQueue = room.getRobQueue();
                // 就剩一个 现家没叫 并且已经叫过一次地主 那么他是地主
                if (robQueue.size() == 1 && robQueue.peek().getRobCount().get() == 1) {
                    Player landlord = robQueue.poll();
                    roomStatus.setCurPlayer(landlord);
                    if (!room.getRobDelayQueue().remove(roomStatus)) {
                        landlord.setLandlords(true);
                        notifyNextRob(namespace, roomStatus, room, true);
                        notifyShowAHand(namespace, landlord, room);
                        notifyPlay(namespace, room, landlord, null);
                    }
                } else {
                    notifyNextRob(namespace, roomStatus, room);
                }
            }
        }
    }

    /**
     * 出牌超时任务
     */
    class PlayTimeOutTask implements Runnable {

        private final RoomStatus roomStatus;

        public PlayTimeOutTask(RoomStatus roomStatus) {
            this.roomStatus = roomStatus;
        }

        @Override
        public void run() {
            SocketIOServer originServer = landlordsServer.getOriginServer();
            SocketIONamespace namespace = originServer.getNamespace(LandlordServerConfig.ROOM_NAME_SPACE);

            Player curPlayer = roomStatus.getCurPlayer();
            Room room = curPlayer.getRoom();
            Player nextPlayer = room.getPlayerMap().get(curPlayer.getRoomDirection().next());

            Map<String, Poker> map = room.getPrevPoker();

            // 上两家都不出
            Poker poker0 = map.get(curPlayer.getRoomDirection().prev().getCode());
            Poker poker1 = map.get(curPlayer.getRoomDirection().prev().prev().getCode());

            List<Integer> cardArr = new ArrayList<>(1);

            if (map.size() == 0 || (poker0.getPokers().length == 0 && poker1.getPokers().length == 0)) {
                logger.info("房间: {}, partyId: {}, 方位: {}, 超时，出一张最小的",
                        room.getRoom(), curPlayer.getPartyId(), curPlayer.getRoomDirection());

                // 第一个人超时 给他出一张最小的
                List<Integer> pokers = curPlayer.getPokers();
                cardArr.add(pokers.get(pokers.size() - 1));
                roomStatus.setCardArr(cardArr);
                roomStatus.setValue(true);
            } else {
                logger.info("房间: {}, partyId: {}, 方位: {}, 出牌超时",
                        room.getRoom(), curPlayer.getPartyId(), curPlayer.getRoomDirection());
                logger.info("轮到下一个 partyId: {}, 方位: {}, 出牌", nextPlayer.getPartyId(), nextPlayer.getRoomDirection());

                roomStatus.setCardArr(null);
                roomStatus.setValue(false);
            }

            play(namespace, roomStatus, room, new Poker(cardArr), true);
        }
    }
}
