package com.atguigu.gulimail.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Controller;

import javax.activation.DataSource;

/*
 1.开启服务注册与发现:网关将请求路由到其他服务，必须通过服务注册中心知道其他服务的ip地址
   配置nacos的注册中心地址
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) //排除掉数据源相关的配置
public class GulimailGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimailGatewayApplication.class, args);
    }
}
