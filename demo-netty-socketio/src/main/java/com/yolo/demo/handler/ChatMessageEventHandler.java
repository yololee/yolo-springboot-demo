package com.yolo.demo.handler;

import cn.hutool.core.util.RandomUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.corundumstudio.socketio.protocol.Packet;
import com.corundumstudio.socketio.protocol.PacketType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yolo.demo.cache.ClientCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component(value= "chatMessageEventHandler")
@Slf4j
@RequiredArgsConstructor
public class ChatMessageEventHandler{

    private final SocketIOServer socketIOServer;

    private final ClientCache clientCache;

    private static final int testPushCount = 0;
    private static final String NAMESPACE = "/chat";

    //测试使用
    @OnEvent("socketIOHandler")
    public void testHandler(SocketIOClient client, String data, AckRequest ackRequest) throws JsonProcessingException {
        log.info("SocketIOHandler:{}",data);
        if(ackRequest.isAckRequested()){
            //返回给客户端，说我接收到了
            ackRequest.sendAckData("SocketIOHandler",data);
        }
    }


    //加入房间
    @OnEvent("joinRoom")
    public void joinRooms(SocketIOClient client, String data, AckRequest ackRequest){
        client.joinRoom(data);
        if(ackRequest.isAckRequested()){
            //返回给客户端，说我接收到了
            ackRequest.sendAckData("加入房间","成功");
        }
    }


    //离开房间
    @OnEvent("leaveRoom")
    public void leaveRoom(SocketIOClient client, String data, AckRequest ackRequest){
        client.leaveRoom(data);
        if(ackRequest.isAckRequested()){
            //返回给客户端，说我接收到了
            ackRequest.sendAckData("离开房间","成功");
        }
    }

    //获取该用户所有房间
    @OnEvent("getUserRooms")
    public void getUserRooms(SocketIOClient client, String data, AckRequest ackRequest){
        String userId = client.getHandshakeData().getSingleUrlParam("userId");
        Set<String> allRooms = client.getAllRooms();
        for (String room:allRooms){
            System.out.println("房间名称："+room);
        }
        log.info("服务器收到消息,客户端用户id：{} | 客户发送的消息：{} | 是否需要返回给客户端内容:{} ",userId,data,ackRequest.isAckRequested());

        if(ackRequest.isAckRequested()){
            //返回给客户端，说我接收到了
            ackRequest.sendAckData("你好","哈哈哈");
        }
    }


    @OnEvent("sendRoomMessage")
    public void sendRoomMessage(SocketIOClient client, String data, AckRequest ackRequest){
        String userId = client.getHandshakeData().getSingleUrlParam("userId");
        Set<String> allRooms = client.getAllRooms();
        for (String room:allRooms){
            log.info("房间：{}",room);
            //发送给指定空间名称以及房间的人，并且排除不发给自己
            socketIOServer.getNamespace(NAMESPACE).getRoomOperations(room).sendEvent("message",client, data);
            //发送给指定空间名称以及房间的人，包括自己
            //socketIoServer.getNamespace("/socketIO").getRoomOperations(room).sendEvent("message", data);;
        }
        if(ackRequest.isAckRequested()){
            //返回给客户端，说我接收到了
            ackRequest.sendAckData("发送消息到指定的房间","成功");
        }

    }

    //广播消息给指定的Namespace下所有客户端
    @OnEvent("sendNamespaceMessage")
    public void sendNamespaceMessage(SocketIOClient client, String data, AckRequest ackRequest){
        socketIOServer.getNamespace(NAMESPACE).getBroadcastOperations().sendEvent("message",client, data);;
        if(ackRequest.isAckRequested()){
            //返回给客户端，说我接收到了
            ackRequest.sendAckData("发送消息到指定的房间","成功");
        }

    }

    @OnEvent("testSend")
    public void sendNamespace(String data){
        socketIOServer.getNamespace(NAMESPACE).getBroadcastOperations().sendEvent(data);;
//        if(ackRequest.isAckRequested()){
//            //返回给客户端，说我接收到了
//            ackRequest.sendAckData("发送消息到指定的房间","成功");
//        }

    }

    //点对点
    public void sendMessageOne(String userId){
        HashMap<UUID, SocketIOClient> userClient = clientCache.getUserClient(userId);
        for (UUID sessionId : userClient.keySet()) {
            socketIOServer.getNamespace(NAMESPACE).getClient(sessionId).sendEvent("message", "这是点对点发送");
        }

    }
    /**
     * 测试无限推送
     * */
    @OnEvent(value = "testPush")
    public void onTestPushEvent(SocketIOClient client, AckRequest request, String data) {
        System.out.println("开始推送了..................");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
        // 循环任务，按照上一次任务的发起时间计算下一次任务的开始时间
        scheduler.schedule(((
                        () -> {
                            Random random = new Random();
                            Packet packet = new Packet(PacketType.EVENT);
                            packet.setData(random.nextInt(100));
                            client.sendEvent("testPush", new Point(random.nextInt(100), random.nextInt(100)));
                        })),
                1, TimeUnit.SECONDS);


    }
}


