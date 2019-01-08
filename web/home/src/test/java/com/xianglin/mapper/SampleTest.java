package com.xianglin.mapper;

import com.xianglin.game.common.dal.mapper.PlayersMapper;
import com.xianglin.game.common.dal.model.PlayerDO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationTest.class})
public class SampleTest {

    @Autowired
    private PlayersMapper playersMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<PlayerDO> userList = playersMapper.selectAll();
        Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }

}