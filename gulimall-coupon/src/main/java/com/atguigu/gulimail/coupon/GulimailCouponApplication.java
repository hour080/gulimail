package com.atguigu.gulimail.coupon;

import com.atguigu.gulimail.coupon.controller.CouponController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient  //开启Nacos的服务注册与发现功能，这样这个微服务就可以注册到注册中心了
@SpringBootApplication
public class GulimailCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimailCouponApplication.class, args);
    }

}
