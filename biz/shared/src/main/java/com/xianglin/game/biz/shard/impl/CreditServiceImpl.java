package com.xianglin.game.biz.shard.impl;

import com.xianglin.game.biz.shard.CreditService;
import com.xianglin.game.biz.shard.enums.PlayResult;
import com.xianglin.game.biz.shard.enums.RoomDirection;
import com.xianglin.game.biz.shard.model.*;
import com.xianglin.game.common.dal.mapper.PlayRecordMapper;
import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.model.PlayRecord;
import com.xianglin.game.common.dal.model.PlayerDO;
import com.xianglin.game.utils.constant.LandlordConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CreditServiceImpl implements CreditService {

    private static final Logger logger = LoggerFactory.getLogger(CreditServiceImpl.class);

    @Autowired
    private PlayersMapper playersMapper;

    @Autowired
    private PlayRecordMapper playRecordMapper;

    @Override
    @Transactional
    public CreditResponse dealCredit(Player winner, Room room, Credit credit) {

        logger.info("room: {} 计算积分，游戏币", room.getRoom());
        // 失败玩家扣除的游戏币
        Player nextPlay0 = room.getPlayerMap().get(winner.getRoomDirection().next());
        Player nextPlay1 = room.getPlayerMap().get(winner.getRoomDirection().next().next());

        PlayerDO playerDOWinner = playersMapper.selectByPartyId(winner.getPartyId());
        PlayerDO playerDONextPlay0 = playersMapper.selectByPartyId(nextPlay0.getPartyId());
        PlayerDO playerDONextPlay1 = playersMapper.selectByPartyId(nextPlay1.getPartyId());

        int loserGameCurrency;
        if (winner.isLandlords()) {
            playerDOWinner.setLandlord(true);
            playerDOWinner.setCredit(credit.getBaseScore() * credit.getMultiple() * 2);
            playerDONextPlay0.setCredit(-credit.getBaseScore() * credit.getMultiple());
            playerDONextPlay1.setCredit(-credit.getBaseScore() * credit.getMultiple());

            // 计算 nextPlay0 应该扣除的游戏币
            int gameCurrency = credit.getBaseScore() * credit.getMultiple() * credit.getN() + credit.getA();
            if (gameCurrency > playerDONextPlay0.getGameCurrency()) {
                loserGameCurrency = playerDONextPlay0.getGameCurrency();
            } else {
                loserGameCurrency = gameCurrency;
            }
            playerDONextPlay0.setGameCurrency(-loserGameCurrency);

            // 计算 nextPlay1 应该扣除的游戏币
            int loserGameCurrency_;
            gameCurrency = credit.getBaseScore() * credit.getMultiple() * credit.getN() + credit.getA();
            if (gameCurrency > playerDONextPlay1.getGameCurrency()) {
                loserGameCurrency_ = playerDONextPlay1.getGameCurrency();
            } else {
                loserGameCurrency_ = gameCurrency;
            }
            playerDONextPlay1.setGameCurrency(-loserGameCurrency_);

            // 地主能拿到的游戏币
            loserGameCurrency = loserGameCurrency_ + loserGameCurrency;
            if (loserGameCurrency > playerDOWinner.getGameCurrency()) {
                playerDOWinner.setGameCurrency(playerDOWinner.getGameCurrency());
            } else {
                playerDOWinner.setGameCurrency(loserGameCurrency);
            }
        } else {
            playerDOWinner.setCredit(credit.getBaseScore() * credit.getMultiple());
            if (nextPlay0.isLandlords()) {
                playerDONextPlay0.setLandlord(true);
                playerDONextPlay0.setCredit(-credit.getBaseScore() * credit.getMultiple() * 2);
                playerDONextPlay1.setCredit(credit.getBaseScore() * credit.getMultiple());

                // 计算地主 nextPlay0 应该扣除的游戏币
                int gameCurrency = credit.getBaseScore() * credit.getMultiple() * 2 + credit.getA();
                if (gameCurrency > playerDONextPlay0.getGameCurrency()) {
                    loserGameCurrency = playerDONextPlay0.getGameCurrency();
                } else {
                    loserGameCurrency = gameCurrency;
                }
                playerDONextPlay0.setGameCurrency(-loserGameCurrency);

                // nextPlay1 也是 winner
                if (loserGameCurrency / 2 > playerDONextPlay1.getGameCurrency()) {
                    playerDONextPlay1.setGameCurrency(playerDOWinner.getGameCurrency());
                } else {
                    playerDONextPlay1.setGameCurrency(loserGameCurrency / 2);
                }

                if (loserGameCurrency / 2 > playerDOWinner.getGameCurrency()) {
                    playerDOWinner.setGameCurrency(playerDOWinner.getGameCurrency());
                } else {
                    playerDOWinner.setGameCurrency(loserGameCurrency / 2);
                }

            } else {
                playerDONextPlay1.setLandlord(true);
                playerDONextPlay0.setCredit(credit.getBaseScore() * credit.getMultiple());
                playerDONextPlay1.setCredit(-credit.getBaseScore() * credit.getMultiple() * 2);

                int gameCurrency = credit.getBaseScore() * credit.getMultiple() * 2 + credit.getA();
                if (gameCurrency > playerDONextPlay1.getGameCurrency()) {
                    loserGameCurrency = playerDONextPlay1.getGameCurrency();
                } else {
                    loserGameCurrency = gameCurrency;
                }
                playerDONextPlay1.setGameCurrency(-loserGameCurrency);

                // nextPlay0 也是 winner
                if (loserGameCurrency / 2 > playerDONextPlay0.getGameCurrency()) {
                    playerDONextPlay0.setGameCurrency(playerDOWinner.getGameCurrency());
                } else {
                    playerDONextPlay0.setGameCurrency(loserGameCurrency / 2);
                }

                if (loserGameCurrency / 2 > playerDOWinner.getGameCurrency()) {
                    playerDOWinner.setGameCurrency(playerDOWinner.getGameCurrency());
                } else {
                    playerDOWinner.setGameCurrency(loserGameCurrency / 2);
                }
            }
        }

        insertRecord(playerDOWinner, playerDOWinner, credit);
        insertRecord(playerDONextPlay0, playerDONextPlay0, credit);
        insertRecord(playerDONextPlay1, playerDONextPlay1, credit);

        CreditResponse creditResponse = CreditResponse.builder().build();
        creditResponse.setWin(winner.isLandlords() ? LandlordConstant.LANDLORD : LandlordConstant.FARMER);
        creditResponse.setWinPosition(winner.getPosition());

        // 添加每家玩家剩余的牌
        for (Map.Entry<RoomDirection, Player> entry : room.getPlayerMap().entrySet()) {
            Player player = entry.getValue();
            creditResponse.getRestCard().put(player.getRoomDirection().getCode(), player.getPokers());
        }
        // 积分 游戏币
        creditResponse.getScoreInfo().add(playerDOWinner);
        creditResponse.getScoreInfo().add(playerDONextPlay0);
        creditResponse.getScoreInfo().add(playerDONextPlay1);

        // 刷新缓存中的游戏币
        winner.setMoney(winner.getMoney() + playerDOWinner.getGameCurrency());
        winner.setScore(winner.getScore() + playerDOWinner.getCredit());

        nextPlay0.setMoney(nextPlay0.getMoney() + playerDONextPlay0.getGameCurrency());
        nextPlay0.setScore(nextPlay0.getScore() + playerDONextPlay0.getCredit());

        nextPlay1.setMoney(nextPlay1.getMoney() + playerDONextPlay1.getGameCurrency());
        nextPlay1.setScore(nextPlay1.getScore() + playerDONextPlay1.getCredit());

        return creditResponse;
    }

    private void insertRecord(PlayerDO playerDO, PlayerDO winner, Credit credit) {
        playerDO.setMultiple(credit.getMultiple());

        PlayRecord playRecord = PlayRecord.builder()
                .partyId(playerDO.getPartyId())
                .baseScore(credit.getBaseScore())
                .multiple(credit.getMultiple())
                .credit(playerDO.getCredit())
                .gameCurrency(playerDO.getGameCurrency())
                .isLandlord(winner == playerDO ? LandlordConstant.LANDLORD : LandlordConstant.FARMER)
                .result(playerDO.getCredit() > 0 ? PlayResult.WIN.name() : PlayResult.FAIL.name())
                .creator(playerDO.getShowName())
                .updater(playerDO.getShowName())
                .build();
        playRecordMapper.insertRecord(playRecord);

        // update credit
        PlayerDO player = PlayerDO.builder()
                .partyId(playerDO.getPartyId())
                .credit(playerDO.getCredit())
                .creditLevel(playerDO.getCreditLevel())
                .gameCurrency(playerDO.getGameCurrency())
                .winGames(playerDO.getCredit() > 0 ? 1 : 0)
                .failGames(playerDO.getCredit() > 0 ? 0 : 1)
                .build();

        playersMapper.updateCredit(player);
    }

    @Override
    public Ranking ranking(long partyId) {
        List<PlayerDO> weekRanking = playRecordMapper.weekRanking(partyId);
        List<PlayerDO> totalRanking = playRecordMapper.totalRanking(partyId);

        PlayerDO weekRankingSelf = playRecordMapper.weekRankingSelf(partyId);
        PlayerDO totalRankingSelf = playRecordMapper.totalRankingSelf(partyId);


        Ranking ranking = Ranking.builder()
                .weekRanking(weekRanking)
                .totalRanking(totalRanking)
                .weekRankingSelf(weekRankingSelf)
                .totalRankingSelf(totalRankingSelf)
                .build();

        return ranking;
    }


}
