package com.yolo.properties;

import com.yolo.properties.config.ApplicationProperty;
import com.yolo.properties.config.DeveloperProperty;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

@SpringBootApplication
//@PropertySource("classpath:application-prod.yml")
public class DemoPropertiesApplication implements InitializingBean {

//    @Autowired
//    private ApplicationProperty applicationProperty;

//    @Autowired
//    private DeveloperProperty developerProperty;

//    @Autowired
//    private Environment environment;

//    @Value("${developer.qq}")
//    private String qq;

    public static void main(String[] args) {
        SpringApplication.run(DemoPropertiesApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        System.out.println("方式一：@Value " + applicationProperty.getName() + " - " + applicationProperty.getVersion());
//        System.out.println("方式二：@ConfigurationProperties " + developerProperty.getName() + " - " + developerProperty.getQq());
//        System.out.println("方式三：Environment " + environment.getProperty("developer.qq"));
//        System.out.println("方式四：Environment " + qq);

        Properties props = new Properties();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("application.properties")),
                    StandardCharsets.UTF_8);
            props.load(inputStreamReader);
        } catch (IOException e1) {
            System.out.println(e1);
        }
        System.out.println("方式五：Properties Name：" + props.getProperty("server.port"));
    }
}
