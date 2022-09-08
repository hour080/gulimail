package com.atguigu.gulimail.order.dao;

import com.atguigu.gulimail.order.entity.OrderReturnReasonEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退货原因
 * 
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 15:35:38
 */
@Mapper
public interface OrderReturnReasonDao extends BaseMapper<OrderReturnReasonEntity> {
	
}
