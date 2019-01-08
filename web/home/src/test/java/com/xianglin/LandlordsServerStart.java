package com.xianglin;

import com.xianglin.game.web.landlords.DefaultLandlordsServer;

public class LandlordsServerStart {

    public static void main(String[] args) throws Exception {
        new DefaultLandlordsServer().start();
        Thread.currentThread().join();
    }
}
