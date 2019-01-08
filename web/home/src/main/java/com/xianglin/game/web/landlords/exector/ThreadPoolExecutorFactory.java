package com.xianglin.game.web.landlords.exector;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class ThreadPoolExecutorFactory implements ExecutorFactory {

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    @Override
    public ExecutorService createExecutorService() {
        return new ThreadPoolExecutor(
                AVAILABLE_PROCESSORS << 1,
                512,
                120L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(32768),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("PROCESS_DELAY#THREAD");
                        return thread;
                    }
                }
        );
    }
}
