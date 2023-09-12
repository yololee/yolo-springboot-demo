package com.yolo.demo.cache;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这是存储用户的缓存信息
 */
@Component
public class ClientCache {


    /**
     * 用于存储用户的socket缓存信息
     */
    private static final ConcurrentHashMap<String, HashMap<UUID, SocketIOClient>> CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();


    /**
     * 保存用户信息
     *
     * @param userId         用户id
     * @param sessionId      会话id
     * @param socketIoClient socketIoClient
     */
    public void saveClient(String userId,UUID sessionId,SocketIOClient socketIoClient){
        HashMap<UUID, SocketIOClient> sessionIdClientCache = CONCURRENT_HASH_MAP.get(userId);
        if(sessionIdClientCache == null){
            sessionIdClientCache = new HashMap<>();
        }
        sessionIdClientCache.put(sessionId,socketIoClient);
        CONCURRENT_HASH_MAP.put(userId,sessionIdClientCache);
    }


    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @return {@link HashMap}<{@link UUID},{@link SocketIOClient}>
     */
    public HashMap<UUID,SocketIOClient> getUserClient(String userId){
        return CONCURRENT_HASH_MAP.get(userId);
    }


    /**
     * 根据用户id和session删除用户某个session信息
     *
     * @param userId    用户id
     * @param sessionId 会话id
     */
    public void deleteSessionClientByUserId(String userId,UUID sessionId){
        CONCURRENT_HASH_MAP.get(userId).remove(sessionId);
    }


    /**
     * 删除用户缓存信息
     *
     * @param userId 用户id
     */
    public void deleteUserCacheByUserId(String userId){
        CONCURRENT_HASH_MAP.remove(userId);
    }
}


