package com.yolo.demo.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.yolo.demo.common.dto.ApiResponse;
import com.yolo.demo.domain.User;
import com.yolo.demo.utils.HostHolder;
import com.yolo.demo.utils.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private OnlineCounter onlineCounter;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private HostHolder hostHolder;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
          // 1.获取请求头中的token
        String token = request.getHeader("authorization");
        if (StringUtils.isEmpty(token)){
            ApiResponse apiResponse = ApiResponse.of(400, "未携带请求头信息，不合法", null);
            String jsonStr = JSONUtil.toJsonStr(apiResponse);
            WebUtils.renderString(response,jsonStr);
            return false;
        }
        User user =(User) redisTemplate.opsForValue().get(token);
        if (ObjectUtil.isNull(user)){
            ApiResponse apiResponse = ApiResponse.of(400, "token过期，请重新登录", null);
            String jsonStr = JSONUtil.toJsonStr(apiResponse);
            WebUtils.renderString(response,jsonStr);
            return false;
        }

        // 当请求执行到此处，说明当前token是有效的,对token续期
        redisTemplate.opsForValue().set(token,user,60, TimeUnit.SECONDS);
        // 在本次请求中持有当前用户，方便业务使用
        hostHolder.setUser(user);
        // 覆盖之前的map统计时间，使用最新的token有效期时长
        onlineCounter.insertToken(user.getName());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
         // 释放前挡用户，防止内存泄露
         hostHolder.clear();
    }

}

