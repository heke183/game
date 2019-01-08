package com.xianglin;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

public class RandomTest {

    @Test
    public void nextIntTest() {

        int i = ThreadLocalRandom.current().nextInt(3);
        System.out.println(i);
        Assert.assertTrue(i < 3);
    }
}
