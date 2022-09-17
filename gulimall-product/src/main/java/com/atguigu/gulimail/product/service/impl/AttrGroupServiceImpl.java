package com.atguigu.gulimail.product.service.impl;

import com.atguigu.gulimail.product.entity.AttrEntity;
import com.atguigu.gulimail.product.service.AttrService;
import com.atguigu.gulimail.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private AttrService attrService;

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
        String key = (String) params.get("key");
        //select * from pms_attr_group where catelog_id = #{catelogId} and (attr_group_id = key or attr_group_name like %key%,)
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if(StringUtils.hasText(key)){
            wrapper.and((obj) -> {
                //like是双%的模糊匹配，如果只使用一个%，则可以使用likeLeft或者likeRight
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        if(catelogId == 0){
            page = this.page(
                    new Query<AttrGroupEntity>().getPage(params), //获取params中的page值和limit值
                    wrapper); //分页查询的数据封装在page中
            return new PageUtils(page);
        } else {
            //有分类id才进行拼接，也就是点击三级分类，参数就会有catelog_id
            //SELECT * FROM pms_attr_group WHERE ((attr_group_id = ? OR attr_group_name LIKE ?)
            //AND catelog_id = ?) LIMIT ?
            wrapper.eq("catelog_id",catelogId);
            page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        }
        return new PageUtils(page);
    }

    /**
     * 根据分类id查出所有分组和这些属性分组里面关联的所有属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //1.查询所有分组
        List<AttrGroupEntity> entities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<AttrGroupWithAttrsVo> collect = null;
        if(entities != null){
            collect = entities.stream().map(group -> {
                AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
                BeanUtils.copyProperties(group, attrGroupWithAttrsVo);
                //2.查询分组的所有属性getRelationAttr
                List<AttrEntity> relationAttr = attrService.getRelationAttr(attrGroupWithAttrsVo.getAttrGroupId());
                attrGroupWithAttrsVo.setAttrs(relationAttr);
                return attrGroupWithAttrsVo;
            }).collect(Collectors.toList());
        }
        return collect;
    }

}