package com.yolo.demo.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yolo.demo.anno.DataSource;
import com.yolo.demo.common.DataSourceType;
import com.yolo.demo.entity.User;
import com.yolo.demo.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{


    @DataSource(DataSourceType.SLAVE)
    @Override
    public void saveOneTest(User user) {
        save(user);
    }
}
