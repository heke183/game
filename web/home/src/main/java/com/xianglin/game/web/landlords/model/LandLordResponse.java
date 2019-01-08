package com.xianglin.game.web.landlords.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class LandLordResponse {

    private boolean success;

    private String msg;

    private String address;

    /**
     * 这个应该是 hallUUID
     */
    private UUID roomUUID;

    public LandLordResponse(boolean success) {
        this.success = success;
    }

    public LandLordResponse(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public static LandLordResponse ofSuccess() {
        return new LandLordResponse(true);
    }

    public static LandLordResponse ofSuccess(String msg) {
        return new LandLordResponse(true, msg);
    }

    public static LandLordResponse ofFail() {
        return new LandLordResponse(false);
    }

    public static LandLordResponse ofFail(String msg) {
        return new LandLordResponse(false, msg);
    }
}
