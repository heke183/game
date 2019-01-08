package com.xianglin.game.biz.shard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xianglin.game.biz.shard.enums.RoomDirection;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;

@Data
@Builder
@EqualsAndHashCode(of = {"room"})
@JsonIgnoreProperties({"rob", "aHand", "robDelayQueue",
        "playDelayQueue", "prevPoker", "robQueue", "showTextMap", "credit", "robQueueTakeThread", "playQueueTakeThread"})
public class Room implements Cloneable {

    /**
     * 底牌
     */
    private final List<Integer> aHand = new ArrayList<>();

    /**
     * 叫地主 倒计时 超时队列
     */
    private final DelayQueue<RoomStatus> robDelayQueue = new DelayQueue<>();

    /**
     * 出牌 倒计时 超时队列
     */
    private final DelayQueue<RoomStatus> playDelayQueue = new DelayQueue<>();

    /**
     * 记录上家的出牌
     * list == null 表示还没出过牌
     * list.size() == 0 表示不出
     */
    private final Map<String, Poker> prevPoker = new ConcurrentHashMap<>();

    /**
     * 叫地主的队列
     */
    private final Queue<Player> robQueue = new ConcurrentLinkedQueue<>();

    /**
     * 上家的 showText
     */
    private final Map<String, String> showTextMap = new ConcurrentHashMap<>();

    /**
     * 房间积分
     */
    private final Credit credit = Credit.builder().build();

    private Thread robQueueTakeThread;

    private Thread playQueueTakeThread;

    private Integer room;

    private String position;

    // ------ 给前端使用
    private Player leftPos;

    private Player rightPos;

    private Player downPos;
    // ------

    @Builder.Default
    private Map<RoomDirection, Player> playerMap = new HashMap<>();

    /**
     * 是否有人叫了地主
     */
    private volatile boolean rob;

    /**
     * 是否在游戏中
     */
    private volatile boolean gaming;

    /**
     * 转换给前端需要的类型
     */
    public Room tranToFront() {
        leftPos = playerMap.get(RoomDirection.LEFT_POS);
        if (leftPos != null) {
            leftPos.setPosition(leftPos.getRoomDirection().getCode());
        }
        rightPos = playerMap.get(RoomDirection.RIGHT_POS);
        if (rightPos != null) {
            rightPos.setPosition(rightPos.getRoomDirection().getCode());
        }
        downPos = playerMap.get(RoomDirection.DOWN_POS);
        if (downPos != null) {
            downPos.setPosition(downPos.getRoomDirection().getCode());
        }
        return this;
    }

    @Override
    public Room clone() throws CloneNotSupportedException {
        Room clone = (Room) super.clone();
        return clone;
    }

    public void clearRoomInfo() {
        aHand.clear();
        robDelayQueue.clear();
        playDelayQueue.clear();
        prevPoker.clear();
        robQueue.clear();
        showTextMap.clear();
        credit.init();
        rob = false;
        gaming = false;
    }
}
