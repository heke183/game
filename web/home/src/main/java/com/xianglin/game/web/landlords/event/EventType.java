package com.xianglin.game.web.landlords.event;

import com.xianglin.game.web.landlords.config.LandlordServerConfig;

public enum EventType {

    LOGIN("login", LandlordServerConfig.HALL_NAME_SPACE),
    USER_INFO("getUserInfo", LandlordServerConfig.HALL_NAME_SPACE, LandlordServerConfig.ROOM_NAME_SPACE),
    ROOM_LIST("getRoomList", LandlordServerConfig.HALL_NAME_SPACE),
    JOIN_ROOM("joinRoom", LandlordServerConfig.HALL_NAME_SPACE),
    JOIN_ROOM_CONNECT("joinRoomConnect", LandlordServerConfig.ROOM_NAME_SPACE),
    USER_ROOM_READY("userRoomReady", LandlordServerConfig.ROOM_NAME_SPACE),
    ROOM_USERS("getRoomUser", LandlordServerConfig.ROOM_NAME_SPACE),
    RANKING("getRanking", LandlordServerConfig.HALL_NAME_SPACE),
    START_GAME("startGame", LandlordServerConfig.ROOM_NAME_SPACE),
    SHOW_CARDS("showCards", LandlordServerConfig.ROOM_NAME_SPACE),
    ROOM_CHANGE_STATUS("roomChangeStatus", LandlordServerConfig.ROOM_NAME_SPACE),
    ROOM_STATUS_INFO("roomStatusInfo", LandlordServerConfig.ROOM_NAME_SPACE),
    CHECK_CARD("checkCard", LandlordServerConfig.ROOM_NAME_SPACE),
    MULTIPLE("multiple", LandlordServerConfig.ROOM_NAME_SPACE),
    SHOW_MESSAGE("showMessage", LandlordServerConfig.HALL_NAME_SPACE, LandlordServerConfig.ROOM_NAME_SPACE),
    STORE("store", LandlordServerConfig.HALL_NAME_SPACE)
    ;
    private String type;

    private Class clazz = byte[].class;

    private String namespace[];

    EventType(String type) {
        this.type = type;
    }

    EventType(String type, Class clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    EventType(String type, String ... namespace) {
        this.type = type;
        this.namespace = namespace;
    }

    public String getType() {
        return type;
    }

    public Class getClazz() {
        return clazz;
    }

    public String[] getNamespace() {
        return namespace;
    }

    public static EventType parseType(String type) {
        for (EventType value : EventType.values()) {
            if (value.type.equals(type))
                return value;
        }
        return null;
    }
}
