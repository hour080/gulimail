package com.atguigu.gulimail.member.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimail-coupon")  //告诉springcloud这个接口是一个远程客户端，向注册中心中的gulimail-coupon服务发送Http请求
public interface CouponFeignService {

    //以后调用CouponFeignService的membercoupons方法，它就会去注册中心找gulimail-coupon所在的机器IP地址和端口号，然后再去调用指定请求所对应的方法(处理器映射器，处理器适配器来执行目标方法，包括参数解析器和返回值处理器)
    //http://gulimail-coupon/coupon/coupon/member/list
    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();
}
