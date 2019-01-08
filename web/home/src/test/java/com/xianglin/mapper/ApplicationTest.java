package com.xianglin.mapper;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.xianglin.game.web.landlords.spring.support.EnableLandlordServer;
import com.xianglin.game.web.starter.GameWebHomeApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDubboConfiguration
@ComponentScan(basePackages = {"com.xianglin.game"})
@MapperScan("com.xianglin.game.common.dal.mapper")
public class ApplicationTest {

    public static void main(String[] args) {
        SpringApplication.run(GameWebHomeApplication.class, args);
    }
}
