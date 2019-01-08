package com.xianglin;

import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RMapCache;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.event.EntryEvent;
import org.redisson.api.map.event.EntryExpiredListener;
import org.redisson.config.Config;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class RedissonTest {

    private static RedissonClient redissonClient;

    static {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://redis.dev.xianglin.com:6379");
        config.useSingleServer().setDatabase(10);

        redissonClient = Redisson.create(config);
    }

    @Test
    public void mapCacheTest() throws Exception {

        RMapCache<Object, Object> xxx = redissonClient.getMapCache("xxx");
        xxx.put(1, 1, 10, TimeUnit.SECONDS);

        xxx.addListener(new EntryExpiredListener<Object, Object>() {
            @Override
            public void onExpired(EntryEvent<Object, Object> event) {
                System.err.println(LocalDateTime.now().toLocalTime() + "expire:" + event.getKey());
                xxx.put(1, 1, 10, TimeUnit.SECONDS);
            }
        });
        Thread.currentThread().join();
    }

    @Test
    public void delayed() throws Exception {

        RQueue<Object> xxxx = redissonClient.getQueue("xxxx");
        RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(xxxx);

        delayedQueue.offer(1, 10, TimeUnit.SECONDS);
        delayedQueue.offer(1, 10, TimeUnit.SECONDS);
        delayedQueue.offer(1, 10, TimeUnit.SECONDS);
        delayedQueue.offer(1, 10, TimeUnit.SECONDS);

        while (true) {
            System.out.println(delayedQueue.poll());
        }
    }

}
