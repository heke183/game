package com.xianglin.game.web.landlords.spring.support;

import com.corundumstudio.socketio.listener.DataListener;
import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.mapper.RoomMapper;
import com.xianglin.game.web.landlords.LandlordsServer;
import com.xianglin.game.web.landlords.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LandlordServerRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(LandlordServerRunner.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private PlayersMapper playersMapper;

    @Override
    public void run(String... args) throws Exception {
        Map<String, DataListener> beansOfType = applicationContext.getBeansOfType(DataListener.class);

        Map<String, LandlordsServer> beans = applicationContext.getBeansOfType(LandlordsServer.class);
        for (Map.Entry<String, LandlordsServer> entry : beans.entrySet()) {
            LandlordsServer landlordsServer = entry.getValue();

            for (Map.Entry<String, DataListener> entrySet : beansOfType.entrySet()) {
                landlordsServer.addEventListener(EventType.parseType(entrySet.getKey()), entrySet.getValue());
            }
        }

        int i = roomMapper.clearAllRoom();
        logger.info("CommandLineRunner 清理了 {} 个房间", i);

        playersMapper.clearAllRoomUUID();
    }
}
