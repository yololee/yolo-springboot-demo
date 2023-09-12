package com.yolo.demo.handler;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.yolo.demo.cache.ClientCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class SocketIOConnectHandler {

    private final ClientCache clientCache;

    /**
     * 建立连接
     * @param client 客户端的SocketIO
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {

        //因为我定义用户的参数为userId，你也可以定义其他名称 客户端请求 http://localhost:9999?userId=12345
        String userId = client.getHandshakeData().getSingleUrlParam("userId");

        //同一个页面sessionid一样的
        UUID sessionId = client.getSessionId();

        //保存用户的信息在缓存里面
        clientCache.saveClient(userId,sessionId,client);

        log.info("SocketIOServerHandler-用户id:{},sessionId:{},建立连接成功",userId,sessionId);


    }

    /**
     * 关闭连接
     * @param client 客户端的SocketIO
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client){

        //因为我定义用户的参数为userId，你也可以定义其他名称
        String userId = client.getHandshakeData().getSingleUrlParam("userId");

        //sessionId,页面唯一标识
        UUID sessionId = client.getSessionId();

        //clientCache.deleteUserCacheByUserId(userId);
        //只会删除用户某个页面会话的缓存，不会删除该用户不同会话的缓存，比如：用户同时打开了谷歌和QQ浏览器，当你关闭谷歌时候，只会删除该用户谷歌的缓存会话
        clientCache.deleteSessionClientByUserId(userId,sessionId);

        log.info("SocketIOServerHandler-用户id:{},sessionId:{},关闭连接成功",userId,sessionId);
    }





}

