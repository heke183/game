package com.xianglin.game.web.landlords.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private boolean login;

    private String msg;

    /**
     * 是否需要重连
     */
    private boolean reconnected;

    /**
     * 重连地址
     */
    private String reconnectedAddress;

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
