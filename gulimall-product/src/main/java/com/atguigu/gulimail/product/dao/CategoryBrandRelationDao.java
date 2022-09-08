package com.atguigu.gulimail.product.dao;

import com.atguigu.gulimail.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌分类关联
 * 
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 11:07:32
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {
	
}
