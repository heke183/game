package com.xianglin.game.web.landlords.load.balance;

public interface LoadBalancer {

    Channel select();
}
