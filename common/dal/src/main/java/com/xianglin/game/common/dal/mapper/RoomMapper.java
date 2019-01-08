package com.xianglin.game.common.dal.mapper;

import com.xianglin.game.common.dal.model.RoomDO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

public interface RoomMapper extends BaseMapper<RoomDO> {

    /**
     * 是否已经加入房间
     *
     * @param uuid
     * @return
     */
    boolean isJoined(String uuid);

    /**
     * 加入房间
     *
     * @param room
     * @param position
     * @param uuid
     * @return
     */
    int join(@Param("room") int room,
             @Param("position") String position,
             @Param("uuid") String uuid,
             @Param("reconnected") boolean reconnected);

    /**
     * 退出房间
     *
     * @param room
     * @param position
     * @return
     */
    int exit(@Param("room") int room,
             @Param("position") String position);

    /**
     * 查询所有房间和房间上的玩家
     *
     * @return
     */
    List<RoomDO> selectRooms();

    /**
     * 挑一个房间进入
     *
     * @return
     */
    RoomDO selectSuitableRoom();

    /**
     * 清空房间里边的玩家
     *
     * @return
     */
    int clearAllRoom();
}
