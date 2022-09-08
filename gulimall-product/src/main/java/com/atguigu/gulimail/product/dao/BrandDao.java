package com.atguigu.gulimail.product.dao;

import com.atguigu.gulimail.product.entity.BrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 品牌
 * 
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 11:07:32
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {

}
