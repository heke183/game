package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.xianglin.game.biz.shard.enums.RoomStatusEnum;
import com.xianglin.game.biz.shard.exception.LandlordException;
import com.xianglin.game.biz.shard.exception.NoPokerTypeException;
import com.xianglin.game.biz.shard.exception.PokerException;
import com.xianglin.game.biz.shard.exception.PokerNotBiggerThanException;
import com.xianglin.game.biz.shard.model.*;
import com.xianglin.game.utils.constant.LandlordConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * 房间状态改变
 * 涉及状态
 *
 * @author yefei
 * @see RoomStatusEnum
 */
@EventHandler(eventType = EventType.ROOM_CHANGE_STATUS)
public class RoomChangeStatusEventHandler extends AbstractEventHandler implements DataListener<RoomStatus> {

    private static final Logger logger = LoggerFactory.getLogger(RoomChangeStatusEventHandler.class);

    @Resource
    private EventHandlerHelper eventHandlerHelper;

    @Override
    public void onData(SocketIOClient client, RoomStatus data, AckRequest ackSender) throws Exception {

        Player player = PLAYER_MAP.get(client.getSessionId());
        Room room = player.getRoom();

        data.setRoom(room);
        data.setCurPlayer(player);
        RoomStatusEnum roomStatusEnum = RoomStatusEnum.parse(data.getName());

        Map<String, String> map = room.getShowTextMap();

        // 解析房间的状态（叫地主，抢地主，出牌）
        switch (roomStatusEnum) {
            case ROB:
                if (!room.getRobDelayQueue().remove(data)) {
                    // 超时
                    logger.warn("- 房间:{} 玩家:{} 位置:{} 叫地主超时，拒绝操作！", room.getRoom(), roomStatusEnum, player.getPartyId());
                    return;
                }
                // showTex，是否有人叫了地主 叫次数
                if (!data.getValue()) {
                    map.put(player.getRoomDirection().getCode(), LandlordConstant.ROB_LEFT_BUTTON);
                } else {
                    room.setRob(true);
                    player.getRobCount().incrementAndGet();
                    map.put(player.getRoomDirection().getCode(), LandlordConstant.ROB_RIGHT_BUTTON);
                }
                rob(client, data, room);
                break;
            case ROB_NEXT:
                if (!room.getRobDelayQueue().remove(data)) {
                    // 超时
                    logger.warn("- 房间:{} 玩家:{} 位置:{} 抢地主超时，拒绝操作！", room.getRoom(), roomStatusEnum, player.getPartyId());
                    return;
                }
                // showText，是否有人叫了地主 叫次数
                if (!data.getValue()) {
                    map.put(player.getRoomDirection().getCode(), LandlordConstant.ROB_NEXT_LEFT_BUTTON);
                } else {
                    notifyMultiple(client.getNamespace(), room);

                    room.setRob(true);
                    player.getRobCount().incrementAndGet();
                    map.put(player.getRoomDirection().getCode(), LandlordConstant.ROB_NEXT_RIGHT_BUTTON);
                }
                rob(client, data, room);
                break;
            case CARD_NEXT:
                Poker poker = checkCard(data, room);
                // 是否能出牌（是否超时）
                if (!room.getPlayDelayQueue().remove(data)) {
                    // 超时
                    logger.warn("- 房间:{} 玩家:{} 位置:{} 出牌超时，拒绝操作！", room.getRoom(), roomStatusEnum, player.getPartyId());
                    return;
                }
                play(client.getNamespace(), data, room, poker, false);
                break;
        }
    }

    private void rob(SocketIOClient client, RoomStatus data, Room room) throws LandlordException {
        Player player = data.getCurPlayer();
        Queue<Player> robQueue = room.getRobQueue();
        if (robQueue.poll() != player) {
            throw new LandlordException("叫地主状态异常");
        }

        if (player.getRobCount().get() == 2) {
            player.setLandlords(true);
            // 叫了两次地主，他肯定是地主
            notifyNextRob(client.getNamespace(), data, room, true);
            notifyShowAHand(client.getNamespace(), player, room);
            notifyPlay(client.getNamespace(), room, player, null);
        } else {
            if (robQueue.size() == 0) {
                if (data.getValue()) {
                    // 最后一个是地主
                    player.setLandlords(true);
                    notifyNextRob(client.getNamespace(), data, room, true);
                    notifyShowAHand(client.getNamespace(), player, room);
                    notifyPlay(client.getNamespace(), room, player, null);
                } else {
                    // 重新发牌
                    eventHandlerHelper.showCards(client.getNamespace(), room);
                }
            } else if (robQueue.size() == 1
                    && robQueue.peek().getRobCount().get() == 1
                    && !data.getValue()) {
                // 就剩一个 现家没叫 并且已经叫过一次地主 那么他是地主

                Player landlord = robQueue.poll();
                data.setCurPlayer(landlord);

                if (!room.getRobDelayQueue().remove(data)) {
                    landlord.setLandlords(true);
                    notifyNextRob(client.getNamespace(), data, room, true);
                    notifyShowAHand(client.getNamespace(), landlord, room);
                    notifyPlay(client.getNamespace(), room, landlord, null);
                } else {
                    throw new LandlordException("移除超时队列异常");
                }
            } else {
                // 通知下一个叫地主
                notifyNextRob(client.getNamespace(), data, room);

                if (data.getValue()) {
                    robQueue.add(player);
                }
            }
        }
    }

    private Poker checkCard(RoomStatus data, Room room) throws LandlordException {
        Player curPlayer = data.getCurPlayer();

        // 上两次的出的牌
        Map<String, Poker> prevPoker = room.getPrevPoker();

        List<Integer> cardArr = data.getCardArr() == null ? new ArrayList<>() : data.getCardArr();
        Poker cur = new Poker(cardArr);

        if (data.getValue()) {
            if (CollectionUtils.isEmpty(cardArr)) {
                throw new PokerException("请选择要出的牌");
            }

            Poker poker0 = prevPoker.get(curPlayer.getRoomDirection().prev().getCode());
            Poker poker1 = prevPoker.get(curPlayer.getRoomDirection().prev().prev().getCode());

            /*
             * 第一次出牌或者上两家不出 不需要比较大小
             */
            if (prevPoker.size() == 0 || (poker0.getPokers().length == 0 && poker1.getPokers().length == 0)) {
                // 第一次出牌需要判断牌型
                if (cur.getType().getType() == PokerType.NO_TYPE) {
                    // 不允许出牌 error
                    throw new NoPokerTypeException("牌型不符合规则");
                }
            } else {
                Poker prev;
                if (poker0.getPokers().length == 0) {
                    prev = poker1;
                } else {
                    prev = poker0;
                }
                logger.info("上一次牌: {}，当前牌: {}", prev, cur);
                boolean biggerThan = cur.biggerThan(prev);
                if (!biggerThan) {
                    // 不允许出牌 error
                    throw new PokerNotBiggerThanException("所选牌不符合出牌规则");
                }
            }
        }
        return cur;
    }
}
