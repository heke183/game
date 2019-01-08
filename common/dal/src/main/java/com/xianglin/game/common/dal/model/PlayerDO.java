package com.xianglin.game.common.dal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Builder
@Table(name = "game_landlord_players")
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDO {

    @Id
    private Long id;

    private String uuid;

    private String roomUuid;

    private Long partyId;

    private String lastRoomAddress;

    private Date lastLoginDate;

    private String showName;

    private String headImg;

    private int gameCurrency;

    private int credit;

    private String creditLevel;

    private int totalGames;

    private int winGames;

    private int failGames;

    private int rumAway;

    private int ranking;

    @Builder.Default
    private String isDeleted = "0";

    private String creator;

    private String updater;

    private Date createDate;

    private Date updateDate;

    /**
     * 是否是本人
     */
    private boolean self;

    /**
     * 倍数
     */
    private int multiple;

    /**
     * 是否是地主
     */
    private boolean landlord;

    public void setCredit(int credit) {
        CreditLevelEnum creditLevelEnum = CreditLevelEnum.selectCreditLevel(credit + this.credit);
        if (creditLevelEnum != null)
            this.creditLevel = creditLevelEnum.getName();

        this.credit = credit;
    }
}
