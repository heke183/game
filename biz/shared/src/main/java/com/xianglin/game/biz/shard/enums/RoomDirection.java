package com.xianglin.game.biz.shard.enums;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 房间三个位置
 *
 */
public enum RoomDirection {

    LEFT_POS("leftPos") {
        @Override
        public RoomDirection next() {
            return DOWN_POS;
        }

        @Override
        public RoomDirection prev() {
            return RIGHT_POS;
        }
    },
    RIGHT_POS("rightPos") {
        @Override
        public RoomDirection next() {
            return LEFT_POS;
        }

        @Override
        public RoomDirection prev() {
            return DOWN_POS;
        }
    },
    DOWN_POS("downPos") {
        @Override
        public RoomDirection next() {
            return RIGHT_POS;
        }

        @Override
        public RoomDirection prev() {
            return LEFT_POS;
        }
    },
    ;

    private String code;

    public abstract RoomDirection next();

    public abstract RoomDirection prev();

    /**
     * 随机挑一个方位
     *
     * @return
     */
    public static RoomDirection random() {
        return RoomDirection.values()[ThreadLocalRandom.current().nextInt(3)];
    }

    public String getCode() {
        return code;
    }

    RoomDirection(String code) {
        this.code = code;
    }

    public static RoomDirection parse(String code) {

        for (RoomDirection value : RoomDirection.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
