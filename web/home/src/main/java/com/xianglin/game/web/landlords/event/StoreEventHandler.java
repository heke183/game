package com.xianglin.game.web.landlords.event;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.xianglin.game.biz.shard.GoodsService;
import com.xianglin.game.biz.shard.exception.LandlordException;
import com.xianglin.game.biz.shard.model.Store;
import com.xianglin.game.common.dal.mapper.GoodsMapper;
import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.model.GoodsDO;
import com.xianglin.game.common.dal.model.GoodsType;
import com.xianglin.game.common.dal.model.PlayerDO;
import com.xianglin.game.web.landlords.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

import static com.xianglin.game.web.landlords.model.MessageType.buy_game_currency;

/**
 * 商店相关事件处理
 * @author yefei
 */
@EventHandler(eventType = EventType.STORE)
public class StoreEventHandler implements DataListener<Store> {

    private static final Logger logger = LoggerFactory.getLogger(StoreEventHandler.class);

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private GoodsService goodsService;

    @Resource
    private PlayersMapper playersMapper;

    @Override
    public void onData(SocketIOClient client, Store data, AckRequest ackSender) throws Exception {
        if (data.getStoreStatus() == null) {
            throw new LandlordException("商店事件，参数异常");
        }
        PlayerDO playerDO = playersMapper.selectByUUID(client.getSessionId().toString());

        switch (data.getStoreStatus()) {
            case LIST:
                GoodsDO goodsDO = GoodsDO.builder().isDeleted("0").goodsType(GoodsType.VIRTUAL_TRAN.name()).build();
                List<GoodsDO> goods = goodsMapper.select(goodsDO);
                client.sendEvent(EventType.STORE.getType(), goods);
                break;
            case BUY:
                logger.info("用户: {} 购买物品：{}", playerDO.getPartyId(), data.getGoodsId());
                try {
                    goodsService.transaction(data, playerDO);
                } catch (LandlordException e) {
                    client.sendEvent(EventType.SHOW_MESSAGE.getType(), MessageResponse.ofFail(e.getMessage(), buy_game_currency));
                    break;
                }
                client.sendEvent(EventType.SHOW_MESSAGE.getType(), MessageResponse.ofSuccess(buy_game_currency));
                break;
        }
    }
}
