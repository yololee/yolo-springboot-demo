package com.yolo.demo.config;


import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


//@Configuration
public class RabbitMQConfiguration {


    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        String rabbitmqHost = "127.0.0.1";
        String rabbitmqPort = "5672";
        String rabbitmqUsername = "admin";
        String rabbitmqPassword = "admin";
        String rabbitmqVirtualHost = "my_vhost";
        connectionFactory.setHost(rabbitmqHost);
        connectionFactory.setPort(Integer.parseInt(rabbitmqPort));
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        connectionFactory.setVirtualHost(rabbitmqVirtualHost);
//        connectionFactory.setPublisherReturns(true);//开启return模式
//        connectionFactory.setPublisherConfirms(true);//开启confirm模式
        return connectionFactory;
    }


    @Bean(name = "rabbitTemplate")
    //必须是prototype类型
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(rabbitConnectionFactory());
    }

    @Bean("customContainerFactory")
    public SimpleRabbitListenerContainerFactory containerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(1);  //设置线程数
        factory.setMaxConcurrentConsumers(1); //最大线程数
//        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);//设置为手动确认
        configurer.configure(factory, connectionFactory);
        return factory;
    }
}
