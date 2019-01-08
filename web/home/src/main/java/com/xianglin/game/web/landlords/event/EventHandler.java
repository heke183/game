package com.xianglin.game.web.landlords.event;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    EventType eventType();
}
