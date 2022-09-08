package com.atguigu.gulimail.coupon.dao;

import com.atguigu.gulimail.coupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 15:08:00
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
