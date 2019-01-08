package com.xianglin.game.biz.shard.exception;

/**
 * 牌型不匹配（比较大小的两副牌的牌型不一致，无法比较，炸弹除外）
 *
 * @author Yaen
 */
public class PokerTypeMissMatchException extends PokerException {

	private static final long serialVersionUID = -3414121205223361491L;

	public PokerTypeMissMatchException(String message) {
		super(message);
	}
}
