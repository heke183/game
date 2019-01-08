package com.xianglin.game.web.landlords.exector;

import java.util.concurrent.ExecutorService;

public interface ExecutorFactory {

    ExecutorService createExecutorService();
}
