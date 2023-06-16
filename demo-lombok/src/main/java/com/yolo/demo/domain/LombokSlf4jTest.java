package com.yolo.demo.domain;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LombokSlf4jTest {

    // @Slf4j会自动生成下面这个Logger对象
    // private static final Logger log = LoggerFactory.getLogger(LombokSlf4jTest.class);
    public static void main(String[] args) {
        log.error("error信息");
        log.warn("warn信息");
        log.info("info信息");
        log.debug("debug信息");
        log.trace("trace信息");
    }
}
