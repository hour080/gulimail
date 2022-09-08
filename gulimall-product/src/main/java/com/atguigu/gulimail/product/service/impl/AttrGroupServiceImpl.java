package com.atguigu.gulimail.product.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.product.dao.AttrGroupDao;
import com.atguigu.gulimail.product.entity.AttrGroupEntity;
import com.atguigu.gulimail.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    //其中的Query和PageUtils是自己封装的类
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params), //Page<T> page = new Page<>(curPage, limit);
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        IPage<AttrGroupEntity> page = null;
        if(catelogId == 0){
            page = this.page(
                    new Query<AttrGroupEntity>().getPage(params), //获取params中的page值和limit值
                    new QueryWrapper<AttrGroupEntity>()); //分页查询的数据封装在page中
            return new PageUtils(page);
        } else {
            String key = (String) params.get("key");
            //select * from pms_attr_group where catelog_id = #{catelogId} and (attr_group_id = key or attr_group_name like %key%,)
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>()
                                                            .eq("catelog_id",catelogId);
            if(StringUtils.hasText(key)){
                wrapper.and((obj) -> {
                    //like是双%的模糊匹配，如果只使用一个%，则可以使用likeLeft或者likeRight
                   obj.eq("attr_group_id", key).or().like("attr_group_name", key);
                });
            }
            page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        }
        return new PageUtils(page);
    }

}