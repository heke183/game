package com.xianglin.game.biz.shard.model;

import com.xianglin.game.biz.shard.exception.LandlordException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The type Poker.
 */
public class Poker {

	/**
	 * 1-54 的牌序号，与前端一致
	 *
	 * 3: 1-4
	 * 4: 5-8
	 * 5: 9-12
	 * 6: 13-16
	 * 7: 17-20
	 * 8: 21-24
	 * 9: 25-28
	 * 10: 29-32
	 * J: 33-36
	 * Q: 37-40
	 * K: 41-44
	 * A: 45-48
	 * 2: 49-52
	 * Joker1（小王）: 53
	 * Joker2（大王）: 54
	 */
	@Getter
	private Integer[] pokers;

	/**
	 * 当前的牌型，只读
	 */
	//@Getter
	private PokerType type;

	/**
	 * 构造一个完全空白的牌型，仅用作特殊处理。所有行为不会报错
	 */
	public Poker() {
	}

	/**
	 * 构造一个牌型，此时不做任何计算处理.
	 *
	 * @param pokers the pokers
	 */
	public Poker(Integer[] pokers) {
		this.pokers = pokers;
	}

	/**
	 * 构造一个牌型，此时不做任何计算处理.
	 *
	 * @param pokers the pokers
	 */
	public Poker(List<Integer> pokers) {
		if (pokers != null) {
			Integer[] integers = new Integer[pokers.size()];
			this.pokers = pokers.toArray(integers);
		}
	}

	/**
	 * 获取一副完整的排，经过随机洗牌.
	 *
	 * @return the list
	 */
	public static List<Integer> shuffle() {
		List<Integer> arrayList = new ArrayList<>(54);
		for (int i = 1; i <= 54; i++) {
			arrayList.add(i);
		}
		Collections.shuffle(arrayList);
		return arrayList;
	}

	/**
	 * 比较牌型的大小，如果是特殊牌型，且符合规则，则按照上一次的牌型设置类型。
	 *
	 * @param prev 上一次的牌
	 * @return 当前的排可以压过上一次的牌时返回true ，其他情况都是false
	 * @throws LandlordException the landlord exception
	 */
	public boolean biggerThan(Poker prev) throws LandlordException {
		boolean bigger = PokerValidator.biggerThan(this.getPokers(), prev.getPokers(), this.getType(), prev.getType());

		// 如果是特殊牌型，需要调整当前牌型
		if (bigger && this.getType().getType() != prev.getType().getType() && this.getType().getSpecial() > 0) {
			this.getType().setType(prev.getType().getType());
			this.sort();
		}

		return bigger;
	}

	/**
	 * 比较牌型的大小，如果是特殊牌型，且符合规则，则按照上一次的牌型设置当前的类型
	 *
	 * @param cur  当前的牌
	 * @param prev 上一次的牌
	 * @return 当前的排可以压过上一次的牌时为true ，其他情况都是false
	 * @throws LandlordException the landlord exception
	 */
	public static boolean biggerThan(Poker cur, Poker prev) throws LandlordException {
		return cur.biggerThan(prev);
	}

	/**
	 * 获取牌型，并缓存到类变量。同时将牌按照牌型进行排序
	 * 如果没有牌，类型为NO_TYPE
	 * 复杂度O(n)左右。不过牌最多54张，性能要求不高
	 *
	 * @return the type
	 */
	public PokerType getType() {
		if (this.type == null) {
			this.type = PokerValidator.checkPokerType(this.getPokers());
			this.sort();
		}
		return type;
	}

	/**
	 * 根据牌型对手牌进行排序
	 */
	public void sort() {
		this.pokers = PokerValidator.sort(this.getPokers(), this.getType());
	}

	@Override
	public String toString() {
		return "Poker{" +
				"pokers=" + Arrays.toString(pokers) +
				", type=" + type +
				'}';
	}
}
