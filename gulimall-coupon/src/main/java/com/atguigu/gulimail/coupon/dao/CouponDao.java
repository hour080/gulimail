package com.atguigu.gulimail.coupon.dao;

import com.atguigu.gulimail.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 15:08:00
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
