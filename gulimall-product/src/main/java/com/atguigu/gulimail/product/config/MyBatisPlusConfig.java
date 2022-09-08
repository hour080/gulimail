package com.atguigu.gulimail.product.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * TODO
 * 分页插件配置
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/9/8 17:25
 */
@Configuration
@EnableTransactionManagement //开启事务功能
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        innerInterceptor.setOverflow(true); //设置请求的页面大于最大页的操作，true表示返回到首页，false表示继续请求
        innerInterceptor.setMaxLimit(1000L);
        interceptor.addInnerInterceptor(innerInterceptor);
        return interceptor;
    }
}
