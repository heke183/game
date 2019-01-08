package com.xianglin.game.biz.shard.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xianglin.cif.common.service.facade.GoldcoinService;
import com.xianglin.cif.common.service.facade.model.Response;
import com.xianglin.cif.common.service.facade.vo.GoldcoinAccountVo;
import com.xianglin.cif.common.service.facade.vo.GoldcoinRecordVo;
import com.xianglin.game.biz.shard.GoodsService;
import com.xianglin.game.biz.shard.exception.LandlordException;
import com.xianglin.game.biz.shard.model.Store;
import com.xianglin.game.common.dal.mapper.GoodsMapper;
import com.xianglin.game.common.dal.mapper.OrderMapper;
import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.model.GoodsDO;
import com.xianglin.game.common.dal.model.GoodsType;
import com.xianglin.game.common.dal.model.Order;
import com.xianglin.game.common.dal.model.PlayerDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Service
public class GoodsServiceImpl implements GoodsService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsServiceImpl.class);

    private static final long GOLD_SYS_ACCOUNT = 10000L;

    @Reference
    private GoldcoinService goldcoinService;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private PlayersMapper playersMapper;

    @Override
    @Transactional
    public void transaction(Store store, PlayerDO player) throws LandlordException {
        GoodsDO goods = GoodsDO.builder().isDeleted("0").goodsId(store.getGoodsId()).build();
        GoodsDO goodsDO = goodsMapper.selectOne(goods);


        switch (GoodsType.parse(goodsDO.getGoodsType())) {
            case VIRTUAL_TRAN:
                dealVirtualTran(store, player, goodsDO);
                break;
            default:
                throw new UnsupportedOperationException("unsupported goods type" + goodsDO.getGoodsType());
        }
    }

    /**
     * 金币和游戏币虚拟交易
     *
     * @param store
     * @param player
     * @param goodsDO
     * @throws LandlordException
     */
    private void dealVirtualTran(Store store, PlayerDO player, GoodsDO goodsDO) throws LandlordException {

        Response<GoldcoinAccountVo> response = goldcoinService.queryAccount(player.getPartyId());
        if (response.getResult().getAmount() < goodsDO.getPrice()) {
            throw new LandlordException("兑换失败，金币不足");
        }

        GoldcoinRecordVo goldcoinRecordVo = new GoldcoinRecordVo();
        goldcoinRecordVo.setSystem("game");
        goldcoinRecordVo.setAmount(-goodsDO.getPrice());
        goldcoinRecordVo.setFronPartyId(GOLD_SYS_ACCOUNT);
        goldcoinRecordVo.setType("landlord");
        goldcoinRecordVo.setRemark("斗地主商城购买");
        goldcoinRecordVo.setToPartyId(player.getPartyId());
        String requestId = UUID.randomUUID().toString().replaceAll("-","");
        goldcoinRecordVo.setRequestId(requestId);

        Response<GoldcoinRecordVo> doRecordResponse = goldcoinService.doRecord(goldcoinRecordVo);
        if (doRecordResponse.getCode() == 1000) {
            logger.info("扣除金币成功");
            // 生成订单
            Order order = Order.builder()
                    .partyId(player.getPartyId())
                    .orderId(requestId)
                    .orderDate(new Date())
                    .endDate(new Date())
                    .goodsId(store.getGoodsId())
                    .spendGoldCoins(goodsDO.getPrice())
                    .goodsValue(goodsDO.getGoodsValue())
                    .creator(player.getShowName())
                    .updater(player.getShowName())
                    .createDate(new Date())
                    .updateDate(new Date()).build();
            orderMapper.insertSelective(order);

            // 给用户加游戏币
            player.setGameCurrency(goodsDO.getGoodsValue());
            playersMapper.updateGameCurrency(player);

        } else {
            throw new LandlordException("兑换失败");
        }
    }
}
