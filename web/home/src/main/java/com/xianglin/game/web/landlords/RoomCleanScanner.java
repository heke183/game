package com.xianglin.game.web.landlords;

import com.xianglin.game.biz.shard.model.Room;
import com.xianglin.game.web.landlords.event.AbstractEventHandler;
import com.xianglin.game.web.landlords.event.EventHandlerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class RoomCleanScanner implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RoomCleanScanner.class);

    @Resource
    private EventHandlerHelper eventHandlerHelper;

    @Override
    public void afterPropertiesSet() throws Exception {

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            logger.debug("---- RoomCleanScanner begin clean room ------");
            for (Map.Entry<Integer, Room> entry : AbstractEventHandler.ROOM_MAP.entrySet()) {
                eventHandlerHelper.clearRoom(entry.getValue(), false);
            }
        }, 1, 10, TimeUnit.SECONDS);

        logger.info("---- RoomCleanScanner start");
    }
}
