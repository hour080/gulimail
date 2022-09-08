package com.atguigu.gulimail.product.dao;

import com.atguigu.gulimail.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 11:07:32
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
