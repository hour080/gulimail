package com.atguigu.gulimail.member.dao;

import com.atguigu.gulimail.member.entity.GrowthChangeHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成长值变化历史记录
 * 
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 15:23:17
 */
@Mapper
public interface GrowthChangeHistoryDao extends BaseMapper<GrowthChangeHistoryEntity> {
	
}
