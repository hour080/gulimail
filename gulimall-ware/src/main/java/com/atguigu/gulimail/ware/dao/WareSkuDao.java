package com.atguigu.gulimail.ware.dao;

import com.atguigu.gulimail.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 15:42:36
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
