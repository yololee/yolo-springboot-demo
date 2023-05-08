package com.yolo.mybatis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.yolo.mybatis.dto.UserDTO;
import com.yolo.mybatis.mapper.UserMapper;
import com.yolo.mybatis.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@SpringBootTest(classes = DemoOrmMybatisApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class UserMapperAnnoTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void selectAllUser(){
        List<User> userList = userMapper.selectAllUser();
        Assert.assertTrue(CollUtil.isNotEmpty(userList));
        log.debug("【userList】= {}", userList);
    }

    @Test
    public void selectUserById() {
        User user = userMapper.selectUserById(1L);
        Assert.assertNotNull(user);
        log.debug("【user】= {}", user);
    }

    @Test
    public void selectByIds() {
        List<User> userList = userMapper.selectByIds(ListUtil.of(1, 2));
        Assert.assertNotNull(userList);
        log.debug("【userList】= {}", userList);
    }

    @Test
    public void selectPage() {
        List<User> userList = userMapper.selectPage(UserDTO.builder().name("user_1").build());
        Assert.assertNotNull(userList);
        log.debug("【userList】= {}", userList);
    }

    @Test
    public void saveUser() {
        User user = User.builder().name("张三").email("123@qq.com").password("123123").salt("123").status(0).createTime(new Date()).lastUpdateTime(new Date()).phoneNumber("123456789").build();
        Integer num = userMapper.saveUser(user);
        Assert.assertNotNull(num);
        log.debug("【num】= {}", num);
    }

    @Test
    public void insertList() {
        User user1 = User.builder().name("李四").email("123456@qq.com").password("123123").salt("123").status(0).createTime(new Date()).lastUpdateTime(new Date()).phoneNumber("123654356").build();
        User user2 = User.builder().name("王五").email("12334123@qq.com").password("123123").salt("123").status(0).createTime(new Date()).lastUpdateTime(new Date()).phoneNumber("123542").build();
        Integer num = userMapper.insertList(ListUtil.of(user1, user2));
        Assert.assertNotNull(num);
        log.debug("【num】= {}", num);
    }

    @Test
    public void updateBatchStateById() {
        Integer num = userMapper.updateBatchStateById(ListUtil.of(1,2),0);
        Assert.assertNotNull(num);
        log.debug("【num】= {}", num);
    }

    @Test
    public void updateBatch() {
        User user1 = User.builder().id(10L).name("李四123").email("1234324456@qq.com").build();
        User user2 = User.builder().id(11L).name("王五321").email("1233321344123@qq.com").build();
        Integer num = userMapper.updateBatch(ListUtil.of(user1, user2));
        Assert.assertNotNull(num);
        log.debug("【num】= {}", num);
    }

    @Test
    public void deleteById() {
        Integer num = userMapper.deleteById(3L);
        Assert.assertNotNull(num);
        log.debug("【num】= {}", num);
    }

    @Test
    public void batchDelete() {
        Integer num = userMapper.batchDelete(ListUtil.of(10,11));
        Assert.assertNotNull(num);
        log.debug("【num】= {}", num);
    }




}
