package com.xianglin.game.web.landlords.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class JoinRoomVo {

    /**
     * 加入房间时，大厅连接的uuid 名字取的有问题
     */
    private UUID roomUUID;

    /**
     * 房间服务器地址
     */
    private String roomAddress;

    /**
     * 房间ID
     */
    private Integer room;

    /**
     * 加入的位置
     */
    private String position;
}
