package com.xianglin.game.web.landlords.model;

public enum MessageType {

    error("服务器异常"),

    /**
     * 消息
     */
    message("操作成功"),

    /**
     * 购买游戏币
     */
    buy_game_currency("兑换成功"),

    /**
     * 游戏币不足
     */
    game_currency_not_enough("游戏币不足"),

    /**
     * 与大厅失去连接
     */
    offline_hall("与大厅失去连接"),

    /**
     * reconnected error
     */
    reconnected_error("重连加入房间失败")
    ;

    private String msg;

    public String getMsg() {
        return msg;
    }

    MessageType() {
    }

    MessageType(String msg) {
        this.msg = msg;
    }
}
