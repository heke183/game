package com.xianglin.game.common.dal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@Table(name = "game_landlord_room")
@AllArgsConstructor
@NoArgsConstructor
public class RoomDO {

    @Id
    private int room;

    private String leftPos;

    private String downPos;

    private String rightPos;

}
