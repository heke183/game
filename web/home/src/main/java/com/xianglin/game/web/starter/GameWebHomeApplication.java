package com.xianglin.game.web.starter;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.xianglin.game.web.landlords.spring.support.EnableLandlordServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDubboConfiguration
@EnableLandlordServer
@ComponentScan(basePackages = {"com.xianglin.game"})
@MapperScan("com.xianglin.game.common.dal.mapper")
public class GameWebHomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameWebHomeApplication.class, args);
    }
}
