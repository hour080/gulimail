package com.atguigu.gulimail.gateway;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimailGatewayApplicationTests {

    private final Logger logger = LoggerFactory.getLogger(GulimailGatewayApplicationTests.class);
    @Test
    void contextLoads() {
        logger.trace("这是trace日志");
        logger.debug("这是debug日志");
        logger.info("这是info日志");
        logger.warn("这是warn日志");
        logger.error("这是error日志");
    }

}
