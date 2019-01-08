package com.xianglin.game.web.landlords.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class MessageResponse {

    private boolean success;

    private String msg;

    private MessageType type = MessageType.message;

    public MessageResponse(boolean success) {
        this.success = success;
    }

    public MessageResponse(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public MessageResponse(boolean success, String msg, MessageType type) {
        this.success = success;
        this.msg = msg;
        this.type = type;
    }

    public static MessageResponse ofSuccess(String msg) {
        return new MessageResponse(true, msg);
    }

    public static MessageResponse ofSuccess(MessageType type) {
        return new MessageResponse(true, type.getMsg(), type);
    }

    public static MessageResponse ofSuccess(String msg, MessageType type) {
        return new MessageResponse(true, msg, type);
    }

    public static MessageResponse ofFail(String msg) {
        return new MessageResponse(false, msg);
    }

    public static MessageResponse ofFail(MessageType type) {
        return new MessageResponse(false, type.getMsg(), type);
    }

    public static MessageResponse ofFail(String msg, MessageType type) {
        return new MessageResponse(false, msg, type);
    }

}
