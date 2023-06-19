package com.yolo.demo.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineCounter {

    /**
     * 每次打开此类是该属性只初始化一次
     */
    private static final Map<String,Object> COUNT_MAP = new ConcurrentHashMap<>();


    /**
     * 当一个用户登录时，就往map中构建一个k-v键值对
     * k- 用户名，v 当前时间+过期时间间隔，这里以60s为例子
     * 如果用户在过期时间间隔内频繁对网站进行操作，那摩对应
     * 她的登录凭证token的有效期也会一直续期，因此这里使用用户名作为k可以覆盖之前
     * 用户登录的旧值，从而不会出现重复统计的情况
     */
    public void insertToken(String userName){
        long currentTime = System.currentTimeMillis();
        COUNT_MAP.put(userName,currentTime+60*1000);
    }

    /**
     * 当用户注销登录时，将移除map中对应的键值对
     * 避免当用户下线时，该计数器还错误的将该用户当作
     * 在线用户进行统计
     * @param userName
     */
    public void deleteToken(String userName){
        COUNT_MAP.remove(userName);
    }

    /**
     * 统计用户在线的人数
     * @return
     */
    public Integer getOnlineCount(){
        int onlineCount = 0;
        Set<String> nameList = COUNT_MAP.keySet();
        long currentTime = System.currentTimeMillis();
        for (String name : nameList) {
            Long value = (Long) COUNT_MAP.get(name);
            if (value > currentTime){
                // 说明该用户登录的令牌还没有过期
                onlineCount++;
            }
        }
        return onlineCount;
    }
}
