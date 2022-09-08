package com.atguigu.gulimail.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*
 1.整合MyBatis-plus
 	1) 导入依赖  mybatis-plus-boot-starter
 	2) 配置
 	   1，配置数据源
 	      1)导入数据库的驱动 mysql-connector-java
 	      2)在application.yaml中配置数据源信息（包含url，密码，驱动名称，用户名）
 	   2.配置MyBatis-Plus
 	      1) 使用@MapperScan将指定包下的接口创建运行时代理类，添加到spring容器中
 	      2) 告诉MyBatis-Plus映射文件位置
   2.逻辑删除
	1)配置全局的逻辑删除规则
	2)3.1.0版本之前配置逻辑删除的组件Bean
	3)给Bean上加上逻辑删除注解@TableLogic(3.3.0版本以后可以不加)
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimail.product.dao")
public class GulimailProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimailProductApplication.class, args);
	}

}
