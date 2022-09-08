package com.atguigu.gulimail.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimail.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 15:35:38
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

