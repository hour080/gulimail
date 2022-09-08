package com.atguigu.gulimail.coupon.dao;

import com.atguigu.gulimail.coupon.entity.SeckillSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动场次
 * 
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 15:08:00
 */
@Mapper
public interface SeckillSessionDao extends BaseMapper<SeckillSessionEntity> {
	
}
