package com.xianglin.game.biz.shard.model;

import lombok.Getter;
import lombok.Setter;

/**
 * poker type.
 *
 * @author Yaen
 */
public class PokerType {

	/** no type 不成牌型 */
	public static final int NO_TYPE = 0;
	/** single 单张 */
	public static final int SINGLE = 1;
	/** pair 对子 */
	public static final int PAIR = 2;
	/** triple 三张 */
	public static final int TRIPLE = 3;
	/** triple with single 三带一 */
	public static final int TRIPLE_WITH_SINGLE = 4;
	/** triple with pair 三带对 */
	public static final int TRIPLE_WITH_PAIR = 5;
	/** straight single 顺子 */
	public static final int STRAIGHT_SINGLE = 6;
	/** straight pair 连对 */
	public static final int STRAIGHT_PAIR = 7;
	/** plane 飞机 */
	public static final int PLANE = 8;
	/** plane with single 飞机带单 */
	public static final int PLANE_WITH_SINGLE = 9;
	/** plane with pair 飞机带对 */
	public static final int PLANE_WITH_PAIR = 10;
	/** four with single 四带两单 */
	public static final int FOUR_WITH_SINGLE = 11;
	/** four with pair 四带两对 */
	public static final int FOUR_WITH_PAIR = 12;
	/** bomb 炸弹 */
	public static final int FOUR_BOMB = 13;
	/** king bomb 王炸 */
	public static final int KING_BOMB = 14;

	/** the type names array */
	public static final String[] TYPE_NAMES = new String[]{"NO_TYPE", "SINGLE", "TWIN", "TRIPLE", "TRIPLE_WITH_SINGLE",
			"TRIPLE_WITH_PAIR", "STRAIGHT_SINGLE", "STRAIGHT_PAIR", "PLANE", "PLANE_WITH_SINGLE",
			"PLANE_WITH_PAIR", "FOUR_WITH_SINGLE", "FOUR_WITH_PAIR", "FOUR_BOMB", "KING_BOMB"};

	/** the type */
	@Getter
	@Setter
	private int type = 0;


	/**
	 * the key value of the poker.
	 * <p>
	 * 3 = 1
	 * 10 = 8
	 * J = 9
	 * Q = 10
	 * K = 11
	 * A = 12
	 * 2 = 13
	 * JOKER1 = 14
	 * JOKER2 = 15
	 */
	@Getter
	@Setter
	private int value = 0;

	/**
	 * special case
	 * 0 = no special
	 * 1 = FOUR_WITH_PAIR or PLANE_WITH_SINGLE: 33334444 can be 3333+44+44 or 333444+3+4
	 * 2 = PLANE or PLANE_WITH_SINGLE: 333444555666 can be 333444555666 or 444555666+3+3+3
	 */
	@Getter
	@Setter
	private int special = 0;

	/**
	 * Instantiates a empty Poker type with no type
	 */
	public PokerType() {
	}

	/**
	 * Instantiates a new Poker type.
	 *
	 * @param type  the type
	 * @param value the value
	 */
	public PokerType(int type, int value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * Instantiates a new Poker type with special
	 *
	 * @param type    the type
	 * @param value   the value
	 * @param special the special
	 */
	public PokerType(int type, int value, int special) {
		this.type = type;
		this.value = value;
		this.special = special;
	}

	@Override
	public String toString() {
		return String.format("%s(%d) VALUE(%d) SPECIAL(%d)", TYPE_NAMES[this.getType()], this.getType(), this.getValue(), this.getSpecial());
	}
}
