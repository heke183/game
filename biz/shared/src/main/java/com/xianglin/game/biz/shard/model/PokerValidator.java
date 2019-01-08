package com.xianglin.game.biz.shard.model;

import com.xianglin.game.biz.shard.exception.NoPokerTypeException;
import com.xianglin.game.biz.shard.exception.PokerTypeMissMatchException;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Poker validator for type and compare.
 *
 * @author Yaen
 */
public class PokerValidator {

	private static final Logger logger = LoggerFactory.getLogger(PokerValidator.class);

	/**
	 * The constant NO_TYPE.
	 */
	public static final PokerType NO_TYPE = new PokerType();

	/**
	 * poker id to card value without color.
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
	 *
	 * @param poker the poker
	 * @return the int
	 */
	public static int poker2ValueWithoutColor(int poker) {
		if (poker == 53) return 14; // joker
		if (poker == 54) return 15; // joker
		if (poker >= 1 && poker <= 52) return (poker - 1) / 4 + 1;
		return 0;
	}

	/**
	 * get type with max card count, search from end of map
	 *
	 * @param type  the type
	 * @param map   the map
	 * @param count the count
	 * @return the poker type
	 */
	private static PokerType withMax(int type, int[] map, int count) {
		for (int i = map.length - 1; i >= 0; i--) {
			if (map[i] == count) return new PokerType(type, i);
		}
		return NO_TYPE;
	}

	/**
	 * Check poker type.
	 *
	 * @param pokers the pokers
	 * @return the poker type
	 */
	public static PokerType checkPokerType(int[] pokers) {
		logger.debug("checkPokerType: pokers = {}", pokers);

		// no exception when pokers is null or empty
		if (pokers == null || pokers.length == 0) return NO_TYPE;

		// generate card value map, map[1]=3, map[12]=A, map[14]=joker1, map[15]=joker2
		int[] map = new int[16];
		int card_count = pokers.length;

		for (int i = 0; i < map.length; i++) map[i] = 0;
		for (int poker : pokers) {
			map[poker2ValueWithoutColor(poker)]++;
		}

		// find max same count and min same count
		int max_count = 0;
		int min_count = 4;
		int max_value = 0;
		int min_value = 0;
		for (int i = 0; i < map.length; i++) {
			if (map[i] > 0) {
				if (map[i] > max_count) max_count = map[i];
				if (map[i] < min_count) min_count = map[i];
				max_value = i;
				if (min_value == 0) min_value = i;
			}
		}

		// basic check
		if (max_count > 4 || map[0] > 1 || map[14] > 1 || map[15] > 1) {
			logger.warn("input card not possible: pokers = {}", pokers);
			throw new IllegalArgumentException("input card not possible!");
		}

		// 1 card, single only
		if (card_count == 1) {
			return new PokerType(PokerType.SINGLE, max_value);
		}
		// 2 card, pare or king bomb only
		if (card_count == 2) {
			// joker can not be pair
			if (max_count == 2 && min_count == 2) return new PokerType(PokerType.PAIR, max_value);
			if (map[14] == 1 && map[15] == 1) return new PokerType(PokerType.KING_BOMB, 15);
			return NO_TYPE;
		}
		// 3 card, triple only
		if (card_count == 3) {
			if (max_count == 3 && min_count == 3) return new PokerType(PokerType.TRIPLE, max_value);
			return NO_TYPE;
		}
		// 4 card, bomb or 3+1
		if (card_count == 4) {
			if (max_count == 4 && min_count == 4) return new PokerType(PokerType.FOUR_BOMB, max_value);
			if (max_count == 3 && min_count == 1) return withMax(PokerType.TRIPLE_WITH_SINGLE, map, 3);
			return NO_TYPE;
		}

		// pick 3+2, kill 3+1+1
		if (card_count == 5 && max_count == 3) {
			if (min_count == 2) return withMax(PokerType.TRIPLE_WITH_PAIR, map, 3);
			return NO_TYPE;
		}

		// pick 4+1+1 (4+2 = 4+1+1)
		if (card_count == 6 && max_count == 4) return withMax(PokerType.FOUR_WITH_SINGLE, map, 4);

		// pick 4+2+2 (4+4 = 4+2+2)
		if (card_count == 8 && max_count == 4 && min_count == 2) return withMax(PokerType.FOUR_WITH_PAIR, map, 4);
		// special, 4+4 can be 4+2+2 or 3+3+1+1, default to 4+2+2
		if (card_count == 8 && max_count == 4 && min_count == 4) {
			PokerType special_type = withMax(PokerType.FOUR_WITH_PAIR, map, 4);
			special_type.setSpecial(1);
			return special_type;
		}

		// pick straight single
		if (card_count >= 5 && card_count <= 12 && max_count == 1 && max_value <= 12) {
			if (max_value - min_value + 1 == card_count) return new PokerType(PokerType.STRAIGHT_SINGLE, max_value);
			return NO_TYPE;
		}

		// pick straight pair
		if (card_count >= 6 && max_count == 2 && min_count == 2 && max_value <= 12) {
			if ((max_value - min_value + 1) * 2 == card_count)
				return new PokerType(PokerType.STRAIGHT_PAIR, max_value);
			return NO_TYPE;
		}

		// pick plane, with special case
		if (card_count >= 6 && max_count == 3 && min_count == 3 && max_value <= 12) {
			if ((max_value - min_value + 1) * 3 == card_count) {
				PokerType plane_type = new PokerType(PokerType.PLANE, max_value);

				// check special type like 333444555666 -- 444555666+3+3+3
				if (card_count == 12 || card_count == 24) plane_type.setSpecial(2);

				return plane_type;
			}
			return NO_TYPE;
		}

		// pick plane with single
		if (card_count >= 8 && max_count >= 3 && card_count % 4 == 0) {
			int plane_count = card_count / 4;
			// find continue plane count, from min(max, A)
			for (int i = Math.min(max_value, 12); i >= min_value + plane_count - 1; i--) {
				boolean is_plane = true;
				for (int j = 0; j < plane_count; j++) {
					if (map[i - j] < 3) {
						is_plane = false;
						break;
					}
				}
				// is plane, done
				if (is_plane) return new PokerType(PokerType.PLANE_WITH_SINGLE, i);
			}
		}

		// pick plain with pair
		if (card_count >= 10 && max_count >= 3 && min_count >= 2 && card_count % 5 == 0) {
			int plane_count = card_count / 5;
			// find continue plane count, from min(max, A)
			for (int i = Math.min(max_value, 12); i >= min_value + plane_count - 1; i--) {
				boolean is_plane = true;
				for (int j = 0; j < plane_count; j++) {
					if (map[i - j] < 3) {
						is_plane = false;
						break;
					}
				}
				// is plane, done
				if (is_plane) return new PokerType(PokerType.PLANE_WITH_PAIR, i);
			}
		}

		return NO_TYPE;
	}

	/**
	 * Check poker type poker type.
	 *
	 * @param pokers the pokers
	 * @return the poker type
	 */
	public static PokerType checkPokerType(Integer[] pokers) {
		return checkPokerType(ArrayUtils.toPrimitive(pokers));
	}

	/**
	 * check the curPokers is Bigger than prePokers, return false for any other case
	 *
	 * @param curPokers the cur pokers
	 * @param prePokers the pre pokers
	 * @param curType   the cur type
	 * @param preType   the pre type
	 * @return true for bigger, false for any other case
	 * @throws PokerTypeMissMatchException the poker type miss match exception
	 * @throws NoPokerTypeException        the no poker type exception
	 */
	public static boolean biggerThan(int[] curPokers, int[] prePokers, PokerType curType, PokerType preType) throws PokerTypeMissMatchException, NoPokerTypeException {
		if (curPokers.length == 0 || prePokers.length == 0)
			throw new NoPokerTypeException("current poker or previous poker is empty");

		// find pre type if not given
		if (preType == null) preType = checkPokerType(prePokers);

		logger.debug("biggerThan: pre pokers = {}, pre type = {}", prePokers, preType);

		// pre is no type
		if (preType == null || preType.getType() == PokerType.NO_TYPE)
			throw new NoPokerTypeException("previous poker has no type");

		// pre is king bomb
		if (preType.getType() == PokerType.KING_BOMB) return false;

		// get cur type
		// TODO only check pre type for performance, -- minor
		if (curType == null) curType = checkPokerType(curPokers);

		logger.debug("biggerThan: cur pokers = {}, cur type = {}", curPokers, curType);

		// cur is no type
		if (curType == null || curType.getType() == PokerType.NO_TYPE)
			throw new NoPokerTypeException("current poker has no type");

		// check king bomb
		if (curType.getType() == PokerType.KING_BOMB) return true;

		// check bomb & none-bomb, all 4 case
		if (curType.getType() == PokerType.FOUR_BOMB) {
			if (preType.getType() == PokerType.FOUR_BOMB) {
				// both bomb, check value
				return curType.getValue() > preType.getValue();
			} else {
				// bomb > anything
				return true;
			}
		} else {
			if (preType.getType() == PokerType.FOUR_BOMB) {
				// anything < bomb
				return false;
			} else {
				// both none-bomb

				// check special type
				if (curType.getSpecial() == 1 && curType.getType() == PokerType.FOUR_WITH_PAIR && preType.getType() == PokerType.PLANE_WITH_SINGLE
						&& curPokers.length == prePokers.length && prePokers.length == 8) {
					return curType.getValue() > preType.getValue();
				}

				if (curType.getSpecial() == 2 && curType.getType() == PokerType.PLANE && preType.getType() == PokerType.PLANE_WITH_SINGLE
						&& curPokers.length == prePokers.length && prePokers.length == 12) {
					return curType.getValue() > preType.getValue();
				}

				// normal, check type, size, value
				if (curType.getType() != preType.getType()) throw new PokerTypeMissMatchException("type miss match");
				return (curPokers.length == prePokers.length && curType.getValue() > preType.getValue());
			}
		}
	}

	/**
	 * Bigger than boolean.
	 *
	 * @param curPokers the cur pokers
	 * @param prePokers the pre pokers
	 * @param curType   the cur type
	 * @param preType   the pre type
	 * @return the boolean
	 * @throws PokerTypeMissMatchException the poker type miss match exception
	 * @throws NoPokerTypeException        the no poker type exception
	 */
	public static boolean biggerThan(Integer[] curPokers, Integer[] prePokers, PokerType curType, PokerType preType) throws PokerTypeMissMatchException, NoPokerTypeException {
		return biggerThan(ArrayUtils.toPrimitive(curPokers), ArrayUtils.toPrimitive(prePokers), curType, preType);
	}

	/**
	 * Sort cards by type, for human-read. return new sorted list, origin list is not modified.
	 * if origin cards is empty or type is empty, do nothing and return origin cards
	 *
	 * @param cards the cards
	 * @param type  the type
	 * @return sorted list
	 */
	public static Integer[] sort(Integer[] cards, PokerType type) {
		if (cards == null || type == null || cards.length == 0 || type.getType() == PokerType.NO_TYPE)
			return cards;

		int card_count = 0;
		int min = 99;
		int max = type.getValue() * 4;

		// special type, move key card to front, key should not be Joker
		switch (type.getType()) {
			case PokerType.TRIPLE_WITH_PAIR:
			case PokerType.TRIPLE_WITH_SINGLE:
			case PokerType.FOUR_WITH_PAIR:
			case PokerType.FOUR_WITH_SINGLE:
				// 3+1 or 3+2 or 4+1+1 or 4+2+2, only 1 key card
				card_count = 1;
				break;
			case PokerType.PLANE_WITH_PAIR:
				// 333+222, multi key card
				card_count = cards.length / 5;
				break;
			case PokerType.PLANE_WITH_SINGLE:
				// 333+111, multi key card
				card_count = cards.length / 4;
				break;
		}

		// set min for some type, others keep 99
		if (card_count > 0) min = max - (card_count * 4) + 1;

		// collect cards to key and nonkey
		List<Integer> key = new ArrayList<>();
		List<Integer> nonkey = new ArrayList<>();

		for (Integer card : cards) {
			if (card >= min && card <= max) {
				key.add(card);
			} else {
				nonkey.add(card);
			}
		}

		// sort each
		Collections.sort(key);
		Collections.sort(nonkey);

		// special case : plane_with_single, 333+111, maybe same of 4
		if (type.getType() == PokerType.PLANE_WITH_SINGLE) {
			int cur_value = 0;
			int last_value = 0;
			int count_value = 1;

			// copy key and re-collect
			List<Integer> key2 = new ArrayList<>();

			for (Integer card : key) {
				cur_value = (card - 1) / 4 + 1;
				if (cur_value != last_value) {
					// is different, reset count
					last_value = cur_value;
					count_value = 1;
					key2.add(card);
				} else {
					// is same, check count
					count_value++;
					if (count_value > 3) {
						// overflow, send to nonkey
						nonkey.add(card);
					} else {
						// in count, send to key
						key2.add(card);
					}
				}
			}

			// copy back if changed
			if (key2.size() != key.size()) {
				key.clear();
				key.addAll(key2);
				Collections.sort(nonkey);
			}
		}

		// combine together
		key.addAll(nonkey);

		return key.toArray(new Integer[0]);
	}

}
