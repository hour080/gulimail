package com.atguigu.gulimail.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 1.想要远程调用别的服务
 * 1)引入open-feign依赖
 * 2)编写一个接口，告诉springcloud这个接口需要调用远程服务
 *   a.声明接口的每一个方法都指定了调用哪个远程服务的哪个请求
 * 3)开启远程调用功能  @EnableFeignClients。服务一启动，就会自动扫描指定包下的所有标有@FeignClient注解的接口，将其接口的动态代理类添加到容器中。
 */
@EnableDiscoveryClient
@EnableFeignClients("com.atguigu.gulimail.member.feign")
@SpringBootApplication
public class GulimailMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimailMemberApplication.class, args);
    }

}
