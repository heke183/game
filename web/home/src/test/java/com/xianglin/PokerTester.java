package com.xianglin;

import com.xianglin.game.biz.shard.exception.LandlordException;
import com.xianglin.game.biz.shard.exception.NoPokerTypeException;
import com.xianglin.game.biz.shard.exception.PokerTypeMissMatchException;
import com.xianglin.game.biz.shard.model.Poker;
import com.xianglin.game.biz.shard.model.PokerValidator;
import com.xianglin.game.biz.shard.model.PokerType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

/**
 * The type Poker tester.
 *
 * @author Yaen
 */
public class PokerTester {

	/**
	 * char card map to 1-54 order, set random color
	 *
	 * @param card card char, 3456789XJQKA2YZ, X=10,Y=JOKER(small), Z=JOKER(big)
	 * @return 1-54 order
	 */
	private static int card2Poker(char card) {
		Random r = new Random();
		switch (card) {
			case '0': // special case
				return 0;
			case '3':
				return (1 - 1) * 4 + 1 + r.nextInt(4);
			case '4':
				return (2 - 1) * 4 + 1 + r.nextInt(4);
			case '5':
				return (3 - 1) * 4 + 1 + r.nextInt(4);
			case '6':
				return (4 - 1) * 4 + 1 + r.nextInt(4);
			case '7':
				return (5 - 1) * 4 + 1 + r.nextInt(4);
			case '8':
				return (6 - 1) * 4 + 1 + r.nextInt(4);
			case '9':
				return (7 - 1) * 4 + 1 + r.nextInt(4);
			case 'X': // X = 10
				return (8 - 1) * 4 + 1 + r.nextInt(4);
			case 'J':
				return (9 - 1) * 4 + 1 + r.nextInt(4);
			case 'Q':
				return (10 - 1) * 4 + 1 + r.nextInt(4);
			case 'K':
				return (11 - 1) * 4 + 1 + r.nextInt(4);
			case 'A':
				return (12 - 1) * 4 + 1 + r.nextInt(4);
			case '2':
				return (13 - 1) * 4 + 1 + r.nextInt(4);
			case 'Y': // = JOKER1 (small)
				return 53;
			case 'Z': // = JOKER2 (big)
				return 54;
			default:
				throw new IllegalArgumentException("INVALID CARD: " + card);
		}
	}

	/**
	 * Poker checker.
	 * 3456789XJQKA2YZ
	 * <p>
	 * X=10
	 * Y=JOKER (small)
	 * Z=JOKER (big)
	 *
	 * @param cards     the cards
	 * @param pokerType the poker type
	 * @param maxCard   the max card
	 */
	public static void PokerChecker(String cards, int pokerType, char maxCard) {
		Assert.assertNotNull("cards should not be null!", cards);

		int length = cards.length();
		int[] pokers = new int[length];

		for (int i = 0; i < length; i++) {
			pokers[i] = card2Poker(cards.charAt(i));
		}

		PokerType type = PokerValidator.checkPokerType(pokers);
		if (type.getType() == pokerType && (pokerType == PokerType.NO_TYPE || type.getValue() == PokerValidator.poker2ValueWithoutColor(card2Poker(maxCard)))) {
			System.out.println("OK " + cards + " = " + type.toString());
		} else {
			throw new AssertionError("FAIL " + cards + " = " + type.toString());
		}
	}

	/**
	 * Sort cards by type, for human-read. return new sorted list, origin list is not modified.
	 * find poker type if not given, for test only
	 *
	 * @param cards the cards
	 * @return the integer [ ]
	 */
	public static Integer[] easySort(Integer[] cards) {
		return PokerValidator.sort(cards, PokerValidator.checkPokerType(cards));
	}

	@Test
	public void testBigger() {
		System.out.println("=======================");
		System.out.println("free bigger:");
		System.out.println("=======================");
		try {
			Poker poker1 = new Poker(new Integer[]{17, 18, 19, 20, 21, 22, 23, 24});
			Poker pre = new Poker(new Integer[]{9, 10, 11, 13, 14, 15, 22, 28});
			Assert.assertTrue(poker1.biggerThan(pre));
		} catch (PokerTypeMissMatchException e) {
			e.printStackTrace();
		} catch (NoPokerTypeException e) {
			e.printStackTrace();
		} catch (LandlordException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSort() {
		System.out.println("=======================");
		System.out.println("free sort:");
		System.out.println("=======================");
		// 33444555 -- 44455533
		System.out.println(Arrays.toString(easySort(new Integer[]{2, 3, 5, 6, 7, 9, 10, 11})));

		// 34445555 -- 44455535
		System.out.println(Arrays.toString(easySort(new Integer[]{2, 12, 5, 6, 7, 9, 10, 11})));

		// 44445555 -- 44455545
		System.out.println(Arrays.toString(easySort(new Integer[]{8, 12, 5, 6, 7, 9, 10, 11})));

		// QQQKKKAAA222 -- QQQKKKAAA222
		System.out.println(Arrays.toString(easySort(new Integer[]{37, 38, 39, 41, 42, 43, 45, 46, 47, 49, 50, 51})));

	}

	@Test
	public void testFreeRule() {
		System.out.println("=======================");
		System.out.println("free rule:");
		System.out.println("=======================");
//		PokerChecker("3456789", PokerType.STRAIGHT_SINGLE, '9');
//		PokerChecker("3456789X", PokerType.STRAIGHT_SINGLE, 'X');
//		PokerChecker("234567", PokerType.NO_TYPE, '0');
//		PokerChecker("33344456", PokerType.PLANE_WITH_SINGLE, '4');
//		PokerChecker("33344466", PokerType.PLANE_WITH_SINGLE, '4');
//		PokerChecker("33344566", PokerType.NO_TYPE, '0');
//		PokerChecker("33334444", PokerType.FOUR_WITH_PAIR, '4');
//		PokerChecker("4568XJQK", PokerType.NO_TYPE, '0');
//		PokerChecker("55578", PokerType.NO_TYPE, '0');
//		PokerChecker("AAA222", PokerType.NO_TYPE, '0');
		PokerChecker("KKKQQQAAA222", PokerType.PLANE_WITH_SINGLE, 'A');

	}

	@Test
	public void testPrdRule() {
		System.out.println("=======================");
		System.out.println("rule from PRD: ");
		System.out.println("=======================");
		PokerChecker("44445555", PokerType.FOUR_WITH_PAIR, '5');
		PokerChecker("444555YZ", PokerType.PLANE_WITH_SINGLE, '5');
		PokerChecker("3333444555", PokerType.PLANE_WITH_PAIR, '5');
		PokerChecker("4444YZ", PokerType.FOUR_WITH_SINGLE, '4');
		PokerChecker("333344445555", PokerType.PLANE_WITH_SINGLE, '5');
		PokerChecker("33335555", PokerType.FOUR_WITH_PAIR, '5');
		PokerChecker("AAAA2222", PokerType.FOUR_WITH_PAIR, '2');
	}

	@Test
	public void testFullCase() {
		// full case
		System.out.println("=======================");
		System.out.println("all single case:");
		System.out.println("=======================");
		PokerChecker("3", PokerType.SINGLE, '3');
		PokerChecker("4", PokerType.SINGLE, '4');
		PokerChecker("5", PokerType.SINGLE, '5');
		PokerChecker("6", PokerType.SINGLE, '6');
		PokerChecker("7", PokerType.SINGLE, '7');
		PokerChecker("8", PokerType.SINGLE, '8');
		PokerChecker("9", PokerType.SINGLE, '9');
		PokerChecker("X", PokerType.SINGLE, 'X');
		PokerChecker("J", PokerType.SINGLE, 'J');
		PokerChecker("Q", PokerType.SINGLE, 'Q');
		PokerChecker("K", PokerType.SINGLE, 'K');
		PokerChecker("A", PokerType.SINGLE, 'A');
		PokerChecker("2", PokerType.SINGLE, '2');
		PokerChecker("Y", PokerType.SINGLE, 'Y');
		PokerChecker("Z", PokerType.SINGLE, 'Z');


		System.out.println("=======================");
		System.out.println("all pair case:");
		System.out.println("=======================");
		PokerChecker("33", PokerType.PAIR, '3');
		PokerChecker("44", PokerType.PAIR, '4');
		PokerChecker("55", PokerType.PAIR, '5');
		PokerChecker("66", PokerType.PAIR, '6');
		PokerChecker("77", PokerType.PAIR, '7');
		PokerChecker("88", PokerType.PAIR, '8');
		PokerChecker("99", PokerType.PAIR, '9');
		PokerChecker("XX", PokerType.PAIR, 'X');
		PokerChecker("JJ", PokerType.PAIR, 'J');
		PokerChecker("QQ", PokerType.PAIR, 'Q');
		PokerChecker("KK", PokerType.PAIR, 'K');
		PokerChecker("AA", PokerType.PAIR, 'A');
		PokerChecker("22", PokerType.PAIR, '2');

		// should throw exception
		try {
			System.out.println("   TESTING none-existing case: 55555");
			PokerChecker("55555", PokerType.NO_TYPE, '0');
			throw new AssertionError("input card error!!");
		} catch (IllegalArgumentException ex) {
			// expected
		}
		try {
			System.out.println("   TESTING none-existing case: R");
			PokerChecker("R", PokerType.NO_TYPE, '0');
			throw new AssertionError("input card error!!");
		} catch (IllegalArgumentException ex) {
			// expected
		}
		try {
			System.out.println("   TESTING none-existing case: YY");
			PokerChecker("YY", PokerType.NO_TYPE, '0');
			throw new AssertionError("input card error!!");
		} catch (IllegalArgumentException ex) {
			// expected
		}
		try {
			System.out.println("   TESTING none-existing case: ZZ");
			PokerChecker("ZZ", PokerType.NO_TYPE, '0');
			throw new AssertionError("input card error!!");
		} catch (IllegalArgumentException ex) {
			// expected
		}


		System.out.println("=======================");
		System.out.println("all triple case:");
		System.out.println("=======================");
		PokerChecker("333", PokerType.TRIPLE, '3');
		PokerChecker("444", PokerType.TRIPLE, '4');
		PokerChecker("555", PokerType.TRIPLE, '5');
		PokerChecker("666", PokerType.TRIPLE, '6');
		PokerChecker("777", PokerType.TRIPLE, '7');
		PokerChecker("888", PokerType.TRIPLE, '8');
		PokerChecker("999", PokerType.TRIPLE, '9');
		PokerChecker("XXX", PokerType.TRIPLE, 'X');
		PokerChecker("JJJ", PokerType.TRIPLE, 'J');
		PokerChecker("QQQ", PokerType.TRIPLE, 'Q');
		PokerChecker("KKK", PokerType.TRIPLE, 'K');
		PokerChecker("AAA", PokerType.TRIPLE, 'A');
		PokerChecker("222", PokerType.TRIPLE, '2');
		//PokerChecker("YYY", PokerType.NO_TYPE, '0');
		//PokerChecker("ZZZ", PokerType.NO_TYPE, '0');


		System.out.println("=======================");
		System.out.println("all four case:");
		System.out.println("=======================");
		PokerChecker("3333", PokerType.FOUR_BOMB, '3');
		PokerChecker("4444", PokerType.FOUR_BOMB, '4');
		PokerChecker("5555", PokerType.FOUR_BOMB, '5');
		PokerChecker("6666", PokerType.FOUR_BOMB, '6');
		PokerChecker("7777", PokerType.FOUR_BOMB, '7');
		PokerChecker("8888", PokerType.FOUR_BOMB, '8');
		PokerChecker("9999", PokerType.FOUR_BOMB, '9');
		PokerChecker("XXXX", PokerType.FOUR_BOMB, 'X');
		PokerChecker("JJJJ", PokerType.FOUR_BOMB, 'J');
		PokerChecker("QQQQ", PokerType.FOUR_BOMB, 'Q');
		PokerChecker("KKKK", PokerType.FOUR_BOMB, 'K');
		PokerChecker("AAAA", PokerType.FOUR_BOMB, 'A');
		PokerChecker("2222", PokerType.FOUR_BOMB, '2');
		//PokerChecker("YYYY", PokerType.NO_TYPE, '0');
		//PokerChecker("ZZZZ", PokerType.NO_TYPE, '0');


		System.out.println("=======================");
		System.out.println("all 5 single straight:");
		System.out.println("=======================");
		PokerChecker("34567", PokerType.STRAIGHT_SINGLE, '7');
		PokerChecker("45678", PokerType.STRAIGHT_SINGLE, '8');
		PokerChecker("56789", PokerType.STRAIGHT_SINGLE, '9');
		PokerChecker("6789X", PokerType.STRAIGHT_SINGLE, 'X');
		PokerChecker("789XJ", PokerType.STRAIGHT_SINGLE, 'J');
		PokerChecker("89XJQ", PokerType.STRAIGHT_SINGLE, 'Q');
		PokerChecker("9XJQK", PokerType.STRAIGHT_SINGLE, 'K');
		PokerChecker("XJQKA", PokerType.STRAIGHT_SINGLE, 'A');
		PokerChecker("A2345", PokerType.NO_TYPE, '0');
		PokerChecker("23456", PokerType.NO_TYPE, '0');
		PokerChecker("JQKA2", PokerType.NO_TYPE, '0');


		System.out.println("=======================");
		System.out.println("all 3 pair straight:");
		System.out.println("=======================");
		PokerChecker("334455", PokerType.STRAIGHT_PAIR, '5');
		PokerChecker("445566", PokerType.STRAIGHT_PAIR, '6');
		PokerChecker("556677", PokerType.STRAIGHT_PAIR, '7');
		PokerChecker("667788", PokerType.STRAIGHT_PAIR, '8');
		PokerChecker("778899", PokerType.STRAIGHT_PAIR, '9');
		PokerChecker("8899XX", PokerType.STRAIGHT_PAIR, 'X');
		PokerChecker("99XXJJ", PokerType.STRAIGHT_PAIR, 'J');
		PokerChecker("XXJJQQ", PokerType.STRAIGHT_PAIR, 'Q');
		PokerChecker("JJQQKK", PokerType.STRAIGHT_PAIR, 'K');
		PokerChecker("QQKKAA", PokerType.STRAIGHT_PAIR, 'A');
		PokerChecker("223344", PokerType.NO_TYPE, '0');
		PokerChecker("KKAA22", PokerType.NO_TYPE, '0');
		PokerChecker("AA2233", PokerType.NO_TYPE, '0');


		System.out.println("=======================");
		System.out.println("all simple plane:");
		System.out.println("=======================");
		PokerChecker("333444", PokerType.PLANE, '4');
		PokerChecker("444555", PokerType.PLANE, '5');
		PokerChecker("555666", PokerType.PLANE, '6');
		PokerChecker("666777", PokerType.PLANE, '7');
		PokerChecker("777888", PokerType.PLANE, '8');
		PokerChecker("888999", PokerType.PLANE, '9');
		PokerChecker("999XXX", PokerType.PLANE, 'X');
		PokerChecker("XXXJJJ", PokerType.PLANE, 'J');
		PokerChecker("JJJQQQ", PokerType.PLANE, 'Q');
		PokerChecker("QQQKKK", PokerType.PLANE, 'K');
		PokerChecker("KKKAAA", PokerType.PLANE, 'A');
		PokerChecker("222333", PokerType.NO_TYPE, '0');
		PokerChecker("AAA222", PokerType.NO_TYPE, '0');

	}

	@Test
	public void testSomeLongCase() {
		System.out.println("=======================");
		System.out.println("some long single straight:");
		System.out.println("=======================");
		PokerChecker("34567", PokerType.STRAIGHT_SINGLE, '7');
		PokerChecker("345678", PokerType.STRAIGHT_SINGLE, '8');
		PokerChecker("3456789", PokerType.STRAIGHT_SINGLE, '9');
		PokerChecker("3456789X", PokerType.STRAIGHT_SINGLE, 'X');
		PokerChecker("3456789XJ", PokerType.STRAIGHT_SINGLE, 'J');
		PokerChecker("3456789XJQ", PokerType.STRAIGHT_SINGLE, 'Q');
		PokerChecker("3456789XJQK", PokerType.STRAIGHT_SINGLE, 'K');
		PokerChecker("3456789XJQKA", PokerType.STRAIGHT_SINGLE, 'A');
		PokerChecker("456789XJQKA", PokerType.STRAIGHT_SINGLE, 'A');
		PokerChecker("56789XJQKA", PokerType.STRAIGHT_SINGLE, 'A');
		PokerChecker("6789XJQKA", PokerType.STRAIGHT_SINGLE, 'A');
		PokerChecker("789XJQKA", PokerType.STRAIGHT_SINGLE, 'A');
		PokerChecker("89XJQKA", PokerType.STRAIGHT_SINGLE, 'A');
		PokerChecker("9XJQKA", PokerType.STRAIGHT_SINGLE, 'A');
		PokerChecker("XJQKA", PokerType.STRAIGHT_SINGLE, 'A');


		System.out.println("=======================");
		System.out.println("some long pair straight:");
		System.out.println("=======================");
		PokerChecker("334455", PokerType.STRAIGHT_PAIR, '5');
		PokerChecker("33445566", PokerType.STRAIGHT_PAIR, '6');
		PokerChecker("3344556677", PokerType.STRAIGHT_PAIR, '7');
		PokerChecker("334455667788", PokerType.STRAIGHT_PAIR, '8');
		PokerChecker("33445566778899", PokerType.STRAIGHT_PAIR, '9');
		PokerChecker("33445566778899XX", PokerType.STRAIGHT_PAIR, 'X');
		PokerChecker("33445566778899XXJJ", PokerType.STRAIGHT_PAIR, 'J');
		PokerChecker("33445566778899XXJJQQ", PokerType.STRAIGHT_PAIR, 'Q');
		PokerChecker("33445566778899XXJJQQKK", PokerType.STRAIGHT_PAIR, 'K');
		PokerChecker("33445566778899XXJJQQKKAA", PokerType.STRAIGHT_PAIR, 'A');
		PokerChecker("445566778899XXJJQQKKAA", PokerType.STRAIGHT_PAIR, 'A');
		PokerChecker("5566778899XXJJQQKKAA", PokerType.STRAIGHT_PAIR, 'A');
		PokerChecker("66778899XXJJQQKKAA", PokerType.STRAIGHT_PAIR, 'A');
		PokerChecker("778899XXJJQQKKAA", PokerType.STRAIGHT_PAIR, 'A');
		PokerChecker("8899XXJJQQKKAA", PokerType.STRAIGHT_PAIR, 'A');
		PokerChecker("99XXJJQQKKAA", PokerType.STRAIGHT_PAIR, 'A');
		PokerChecker("XXJJQQKKAA", PokerType.STRAIGHT_PAIR, 'A');
		PokerChecker("JJQQKKAA", PokerType.STRAIGHT_PAIR, 'A');
		PokerChecker("QQKKAA", PokerType.STRAIGHT_PAIR, 'A');


	}

}
