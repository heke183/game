package com.xianglin.game.web.landlords.event;

import com.alibaba.dubbo.config.annotation.Reference;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.xianglin.appserv.common.service.facade.app.PersonalService;
import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.game.biz.shard.PlayerService;
import com.xianglin.game.biz.shard.model.Player;
import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.model.PlayerDO;
import com.xianglin.game.web.landlords.model.LoginResponse;
import com.xianglin.game.web.landlords.model.MessageResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

@EventHandler(eventType = EventType.LOGIN)
public class LoginEventHandler implements DataListener<Player> {

    private static final Logger logger = LoggerFactory.getLogger(LoginEventHandler.class);

    @Reference
    private PersonalService personalService;

    @Resource
    private PlayerService playerService;

    @Resource
    private PlayersMapper playersMapper;

    @Override
    public void onData(SocketIOClient client, Player data, AckRequest ackSender) throws Exception {

        logger.info("{} 登录", client.getSessionId());

        Response<UserVo> userVoResponse = personalService.queryUser(data.getPartyId());
        if (userVoResponse.isSuccess()) {
            UserVo user = userVoResponse.getResult();

            PlayerDO playerDO = PlayerDO.builder()
                    .partyId(data.getPartyId())
                    .uuid(client.getSessionId().toString())
                    .showName(user.getShowName())
                    .headImg(user.getHeadImg()).build();

            playerService.updateOfLoginOrInsert(playerDO);
            LoginResponse loginResponse = LoginResponse.builder().login(true).msg("登录成功").build();

            checkReconnected(data, loginResponse);
            client.sendEvent(EventType.LOGIN.getType(), loginResponse);

            int i = playerService.dailyAddGameCurrency(playerDO.getPartyId());
            if (i > 0) {
                // 提示送金币弹框
                client.sendEvent(EventType.SHOW_MESSAGE.getType(), MessageResponse.ofSuccess("恭喜你，获得2000游戏币"));
            }
        } else {
            LoginResponse loginResponse = LoginResponse.builder().login(false).msg("登录失败").build();
            client.sendEvent(EventType.LOGIN.getType(), loginResponse);
        }
    }

    /**
     * 检查是否需要重连
     *
     * @param data
     * @param loginResponse
     */
    private void checkReconnected(Player data, LoginResponse loginResponse) {
        PlayerDO player = playersMapper.selectByPartyId(data.getPartyId());
        if (StringUtils.isNoneBlank(player.getRoomUuid())) {
            // reconnected
            loginResponse.setReconnected(true);
            loginResponse.setReconnectedAddress(player.getLastRoomAddress());

        }
    }
}
