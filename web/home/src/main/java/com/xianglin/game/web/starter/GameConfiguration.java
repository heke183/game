package com.xianglin.game.web.starter;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ServletComponentScan(basePackages= {"com.xianglin.game.web.filter"})
public class GameConfiguration {



}
