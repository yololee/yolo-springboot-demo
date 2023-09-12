package com.yolo.demo.runner;

import com.corundumstudio.socketio.SocketIOServer;
import com.yolo.demo.handler.ChatMessageEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
class SocketIOServerRunner implements CommandLineRunner, DisposableBean {


    private final SocketIOServer socketIoServer;

    private final ChatMessageEventHandler chatMessageEventHandler;



    @Override
    public void run(String... args) {
        //namespace分别交给各自的Handler监听,这样就可以隔离，只有客户端指定namespace，才能访问对应Handler。
        //比如：http://localhost:9999/test?userId=12345
        socketIoServer.getNamespace("/chat").addListeners(chatMessageEventHandler);

        socketIoServer.start();
        log.info("SocketIOServer==============================启动成功");
    }


    @Override
    public void destroy() {
        //如果用kill -9  这个监听是没用的，有可能会导致你服务kill掉了，但是socket服务没有kill掉
        socketIoServer.stop();
        log.info("SocketIOServer==============================关闭成功");
    }
}