package com.xianglin.game.biz.shard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xianglin.game.biz.shard.enums.RoomDirection;
import lombok.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Builder
@JsonIgnoreProperties({"robCount", "room"})
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"room"})
public class Player {

    private long partyId;

    private UUID uuid;

    private UUID roomUUID;

    private String headImg;

    private String name;

    /**
     * 游戏币
     */
    private int money;

    /**
     * 积分
     */
    private int score;

    /**
     * 积分级别
     */
    private String creditLevel;

    /**
     * 创建的房间id
     */
    private Integer createdRoom;

    /**
     * 所在房间
     */
    private Room room;

    /**
     * 是否是地主
     */
    private volatile boolean landlords;

    /**
     * 房间位置
     */
    private RoomDirection roomDirection;

    /**
     * 房间位置 前端需要
     */
    private String position;

    /**
     * 手中的牌
     */
    private List<Integer> pokers;

    /**
     * 身份 landlord farmer
     */
    private String identity;

    /**
     * 是否准备
     */
    private volatile boolean ready;

    /**
     * 是否离线/逃跑
     */
    private volatile boolean offline;

    /**
     * 叫地主的次数
     */
    @Builder.Default
    private AtomicInteger robCount = new AtomicInteger(0);

    /**
     * 连接的房间地址
     */
    private String lastRoomAddress;

}
