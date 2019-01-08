package com.xianglin.game.biz.shard.model;

import com.xianglin.game.biz.shard.enums.RoomStatusEnum;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.*;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@ToString(exclude = {"room"})
public class RoomStatus implements Delayed {

    private String name;

    private Boolean value;

    private String position;

    private Map<String, String> showText;

    private String leftText;

    private String rightText;

    private int time;

    /**
     * 底牌
     */
    private List<Integer> threeCard;

    /**
     * 每个玩家身份
     */
    private Map<String, Player> players;

    /**
     * 每个玩家的出的牌
     */
    private Map<String, List<Integer>> showCard;

    /**
     * 玩家出的牌（前端传过来）
     */
    private List<Integer> cardArr;

    /**
     * 最后的一次出得牌型
     */
    private PokerType lastCardType;

    private long future;

    /**
     * 当前操作的玩家
     */
    private Player curPlayer;

    /**
     * 重连之后玩家的信息
     */
    private Player reconnectedPlayer;

    /**
     * 每个玩家剩余牌数
     */
    @Builder.Default
    private Map<String, Integer> userCardNum = new HashMap<>();

    /**
     * 积分
     */
    @Builder.Default
    private Credit credit;

    /**
     * 房间
     */
    private Room room;

    public void delay(int millisecond) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MILLISECOND, millisecond);
        this.future = instance.getTimeInMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long sourceDuration = future - System.currentTimeMillis();
        return unit.convert(sourceDuration, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return 0;
    }

    /**
     * 同一用户 同一name 是相同的状态
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomStatus that = (RoomStatus) o;

        return Objects.equals(RoomStatusEnum.parse(name).getEqualsCode(), RoomStatusEnum.parse(that.name).getEqualsCode()) &&
                Objects.equals(curPlayer.getPartyId(), that.curPlayer.getPartyId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, curPlayer.getPartyId());
    }

    /**
     * 重新计算time
     */
    public void recalculateTime() {
        long time = (future - System.currentTimeMillis()) / 1000;
        if (time < 0) {
            this.time = 0;
        } else {
            this.time = (int) time;
        }
    }
}
