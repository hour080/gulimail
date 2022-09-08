package com.atguigu.gulimail.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * TODO
 *
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/8/30 23:15
 */
@Configuration
public class GulimallCorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //1.配置跨域
        corsConfiguration.addAllowedHeader("*"); //允许哪些请求头进行跨域
        corsConfiguration.addAllowedMethod("*"); //允许哪些请求方式
        corsConfiguration.addAllowedOriginPattern("*"); //允许哪些源
        corsConfiguration.setAllowCredentials(true); //允许携带cookie进行跨域，否则跨域请求会丢失cookie信息
        source.registerCorsConfiguration("/**", corsConfiguration); //任意路径都要进行跨域配置
        return new CorsWebFilter(source);
    }

}
