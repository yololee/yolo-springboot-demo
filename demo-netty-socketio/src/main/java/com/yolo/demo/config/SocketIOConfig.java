package com.yolo.demo.config;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Optional;

//@Configuration
@RequiredArgsConstructor
public class SocketIOConfig {


    private final SocketIoProperties socketIoProperties;

    @Bean(name = "SocketIOServer")
    public SocketIOServer socketIoServer() {
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setTcpNoDelay(true);
        socketConfig.setSoLinger(0);
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setSocketConfig(socketConfig);
        config.setHostname(socketIoProperties.getHost());
        config.setPort(socketIoProperties.getPort());
        config.setBossThreads(socketIoProperties.getBossCount());

        config.setWorkerThreads(socketIoProperties.getWorkCount());
        config.setAllowCustomRequests(socketIoProperties.isAllowCustomRequests());
        config.setUpgradeTimeout(socketIoProperties.getUpgradeTimeout());
        config.setPingTimeout(socketIoProperties.getPingTimeout());
        config.setPingInterval(socketIoProperties.getPingInterval());

        //服务端
        final SocketIOServer server = new SocketIOServer(config);

        //添加命名空间（如果你不需要命名空间，下面的代码可以去掉）
        Optional.ofNullable(socketIoProperties.getNamespaces()).ifPresent(nss ->
                Arrays.stream(nss).forEach(server::addNamespace));


        return server;
    }

    //这个对象是用来扫描socketio的注解，比如 @OnConnect、@OnEvent
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(@Qualifier("SocketIOServer") SocketIOServer socketIoServer) {

        return new SpringAnnotationScanner(socketIoServer);
    }
}
