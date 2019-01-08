package com.xianglin;

import com.xianglin.game.common.dal.model.CreditLevelEnum;

public class CreditLevelEnumTest {

    public static void main(String[] args) {
        CreditLevelEnum creditLevelEnum = CreditLevelEnum.selectCreditLevel(-100);
        System.out.println(creditLevelEnum);

        CreditLevelEnum creditLevelEnum0 = CreditLevelEnum.selectCreditLevel(100);
        System.out.println(creditLevelEnum0);

        CreditLevelEnum creditLevelEnum1 = CreditLevelEnum.selectCreditLevel(99999999);
        System.out.println(creditLevelEnum1);

        CreditLevelEnum creditLevelEnum2 = CreditLevelEnum.selectCreditLevel(100000000);
        System.out.println(creditLevelEnum2);
    }
}
