package com.atguigu.gulimail.order.dao;

import com.atguigu.gulimail.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 15:35:38
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
