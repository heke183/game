package com.xianglin.game.biz.shard.exception;

/**
 * 牌型非法（给定的牌无法组成牌型）
 *
 * @author Yaen
 */
public class NoPokerTypeException extends PokerException {

	private static final long serialVersionUID = 3561609717309643964L;

	public NoPokerTypeException(String message) {
		super(message);
	}
}
