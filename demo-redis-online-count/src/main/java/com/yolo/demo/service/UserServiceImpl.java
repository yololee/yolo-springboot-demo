package com.yolo.demo.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yolo.demo.common.dto.ApiResponse;
import com.yolo.demo.config.OnlineCounter;
import com.yolo.demo.domain.User;
import com.yolo.demo.dto.LoginParam;
import com.yolo.demo.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private OnlineCounter onlineCounter;

    @Override
    public ApiResponse selectUserList() {
        return ApiResponse.ofSuccess(list());
    }

    @Override
    public ApiResponse login(LoginParam loginParam) {
        String name = loginParam.getName();

        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getName, name));
        if (ObjectUtil.isNull(user)){
            throw new RuntimeException("用户名或者密码不正确");
        }
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        log.info("当前账号对应的token是: {}",token);
        redisTemplate.opsForValue().set(token,user,60, TimeUnit.SECONDS);
        // 往map中添加一条用户记录
        onlineCounter.insertToken(name);
        return ApiResponse.ofSuccess();
    }

    @Override
    public ApiResponse logout(HttpServletRequest request) {
        String authorization = request.getHeader("authorization");
        User user = (User) redisTemplate.opsForValue().get(authorization);
        redisTemplate.delete(authorization);
        if (ObjectUtil.isNotNull(user)){
            onlineCounter.deleteToken(user.getName());
        }

        return ApiResponse.ofSuccess();
    }

    @Override
    public ApiResponse getOnLineCount() {
        return ApiResponse.ofSuccess(onlineCounter.getOnlineCount());
    }
}
