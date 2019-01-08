package com.xianglin.game.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.xianglin.act.common.service.facade.GamePlaneService;
import com.xianglin.act.common.service.facade.model.GamePlaneDTO;
import com.xianglin.act.common.service.facade.model.Response;
import com.xianglin.game.utils.AppSessionContext;
import com.xianglin.game.utils.GameException;
import com.xianglin.game.web.utils.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Describe :
 * Created by xingyali on 2018/6/1 14:35.
 * Update reason :
 */
@RestController
@RequestMapping("/forward/act/api/game/plane")
public class ForwardController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private GamePlaneService gamePlaneService;

    @RequestMapping("/start")
    public Object start() throws Exception {
        return doResult(gamePlaneService.start(getPartyId()));
    }

    @RequestMapping("/ranking")
    public Object weekRanking() throws Exception {
        return doResult(gamePlaneService.weekRanking(getPartyId()));
    }

    @RequestMapping("/reward")
    public Object reward(Long id,int score,int shotCount,int stage,int coinReward) throws Exception {
        logger.info("reward id = {},score = {},shotCount = {},stage = {},coinReward = {}",id,score,shotCount,stage,coinReward);
        return doResult(gamePlaneService.reward(GamePlaneDTO.builder().id(id)
                .score(score).shotCount(shotCount).stage(stage).coinReward(coinReward).partyId(getPartyId()). build()));
    }

    @RequestMapping("/share")
    public Object share() throws Exception {
        return doResult(gamePlaneService.share(getPartyId()));
    }

    private <T> JsonResult<T> doResult(Response<T> resp){
        logger.info("resp : {}", JSON.toJSON(resp.getResult()));
        JsonResult result = new JsonResult(resp.getResult());
        if(!resp.isSuccess()){
            result.setCode(resp.getCode()+"");
            result.setMessage(resp.getTips());
        }
        return result;
    }

    private Long getPartyId(){
        return AppSessionContext.ofPartyId().orElseThrow(() -> new GameException("用户未登陆"));
//        return 1000000000002429L;
    }
}
