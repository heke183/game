package com.xianglin;

import com.xianglin.game.utils.constant.LandlordConstant;
import com.xianglin.game.biz.shard.model.RoomStatus;
import com.xianglin.game.biz.shard.enums.RoomStatusEnum;

import java.util.concurrent.DelayQueue;

public class DelayedQueueTest {

    public static void main(String[] args) throws Exception {

        DelayQueue<RoomStatus> queue = new DelayQueue();

        RoomStatus roomStatus = RoomStatus.builder()
                .name(RoomStatusEnum.ROB_NEXT.getCode())
                .leftText(LandlordConstant.ROB_LEFT_BUTTON)
                .rightText(LandlordConstant.ROB_RIGHT_BUTTON)
                .position("leftPos")
                .time(LandlordConstant.ROB_TIMEOUT / 1000)
                .build();

        roomStatus.delay(-1);

        queue.add(roomStatus);

        RoomStatus take = queue.take();
        System.out.println("xx" + take);

    }
}
