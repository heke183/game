package com.xianglin.game.web.landlords;

import com.xianglin.game.web.landlords.event.EventType;
import com.xianglin.game.web.landlords.model.NamespaceType;

public interface LandlordsServer {

    void start();

    <T> void addEventListener(EventType eventType, T t);

    void shutdown();

    <T> T getOriginServer();

    NamespaceType nameSpaceType();
}
