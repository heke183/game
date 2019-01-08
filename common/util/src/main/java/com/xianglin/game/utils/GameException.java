package com.xianglin.game.utils;


/**
 * open系统异常
 */
public class GameException extends RuntimeException{

    /**
     * 返回码
     */
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public GameException(String message) {
        super(message);
    }

    public GameException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public GameException(ResultEnum result) {
        super(result.getMessage());
        this.code = result.getCode();
    }
}
