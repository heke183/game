package com.xianglin.game.web.landlords.model;

import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.EmptyStackException;

/**
 *
 * @param <E>
 */
public class RStack<E> {

    private RList<E> rList;

    public static <T> RStack getRStack(RedissonClient redissonClient, String name) {
        return new RStack<T>(redissonClient, name);
    }

    private RStack(RedissonClient redissonClient, String name) {
        this.rList = redissonClient.getList(name);
    }

    public E peek() {
        if (rList.size() == 0)
            return null;

        return rList.get(rList.size() - 1);
    }

    public boolean push(E e) {
        return rList.add(e);
    }

    public E pop() {
        if (rList.size() == 0)
            return null;

        E e = rList.get(rList.size());
        rList.remove(rList.size() - 1);
        return e;
    }
}
