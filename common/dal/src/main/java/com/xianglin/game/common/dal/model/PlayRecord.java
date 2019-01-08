package com.xianglin.game.common.dal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "game_landlord_play_record")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayRecord {

    @Id
    private Long id;

    private Long partyId;

    private Integer baseScore;

    private Integer multiple;

    private Integer credit;

    private Integer gameCurrency;

    private String isLandlord;

    private String result;

    private Date recordDate;

    @Builder.Default
    private String isDeleted = "0";

    private String creator;

    private String updater;

    private Date createDate;

    private Date updateDate;

}
