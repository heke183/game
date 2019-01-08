package com.xianglin.game.web.landlords.model;

import com.xianglin.game.web.landlords.config.LandlordServerConfig;

public enum NamespaceType {

    hall(LandlordServerConfig.HALL_NAME_SPACE),
    room(LandlordServerConfig.ROOM_NAME_SPACE);

    private String namespace;

    NamespaceType(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }
}
