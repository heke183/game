package com.xianglin.game.biz.shard.model;

import com.xianglin.game.utils.constant.LandlordConstant;
import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Credit implements Cloneable {

    /**
     * 底分
     */
    @Builder.Default
    private int baseScore = LandlordConstant.DEFAULT_BASE_SCORE;

    /**
     * 倍数
     */
    @Builder.Default
    private volatile int multiple = LandlordConstant.DEFAULT_MULTIPLE;

    /**
     * 房间系数
     */
    @Builder.Default
    private int N = 1;

    /**
     * 系统每盘回收的游戏币
     */
    @Builder.Default
    private int A = 0;

    /**
     * 倍率（x2）你
     */
    private volatile int multiplePower;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void init() {
        baseScore = LandlordConstant.DEFAULT_BASE_SCORE;
        multiple = LandlordConstant.DEFAULT_MULTIPLE;
        N = 1;
        A = 0;
        multiplePower = 0;
    }
}
