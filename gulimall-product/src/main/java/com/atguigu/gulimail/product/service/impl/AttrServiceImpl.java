package com.atguigu.gulimail.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.gulimail.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimail.product.dao.AttrGroupDao;
import com.atguigu.gulimail.product.dao.CategoryDao;
import com.atguigu.gulimail.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimail.product.entity.AttrGroupEntity;
import com.atguigu.gulimail.product.entity.CategoryEntity;
import com.atguigu.gulimail.product.service.CategoryService;
import com.atguigu.gulimail.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimail.product.vo.AttrRespVo;
import com.atguigu.gulimail.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.product.dao.AttrDao;
import com.atguigu.gulimail.product.entity.AttrEntity;
import com.atguigu.gulimail.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static org.springframework.beans.BeanUtils.copyProperties;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存属性信息到pms_attr表和pms_attr_attrgroup_relation表中
     * 保存属性的分类id和属性所属的分组id
     * @param attr
     */
    @Transactional //涉及两张表
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        copyProperties(attr, attrEntity);
        //1.保存基本数据
        this.save(attrEntity);
        //2.如果当前属性是基本属性，保存关联关系。如果是销售属性，则不保存关联关系.
        //并且如果基本属性没有关联属性分组，也不可以保存到中间表中，否则中间表中存在分组id为null的记录
        if(attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null){
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            entity.setAttrId(attrEntity.getAttrId()); //这里不能使用attr来获取attrid，因为前端传来的数据中没有attrid，它是数据库自动生成的主键
            entity.setAttrGroupId(attr.getAttrGroupId());
            relationDao.insert(entity);
        }
    }

    /**
     * 获取指定类别下的基本属性（带有所属分类的路径和所属的属性分组）
     * @param params
     * @param catelogId
     * @param type base表示基本属性，数据库字段值为1; sale表示销售属性，数据库字段值为0。
     * 注意，基本属性存在属性分组，而销售属性不存在属性分组，销售属性是SKU的属性，包含颜色和存储空间类别
     * @return
     */
    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type",
                        "base".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode()); //查询条件构造器,首先先进行属性类别的判断
        if(catelogId != 0){
            wrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if(StringUtils.hasText(key)){
            wrapper.and(obj -> {   //由于("attr_id" = key or "attr_name" like %key%)是一个整体条件
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),
                wrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> BaseVos = records.stream().map(attrEntity -> {
            AttrRespVo AttrRespVo = new AttrRespVo();
            copyProperties(attrEntity, AttrRespVo); //将attrEntity的属性拷贝到AttrRespVo中
            //1.设置当前属性所属的分组名称,基本属性才会设置属性分组
            if("base".equalsIgnoreCase(type)){
                //1.1 根据中间表查询属性attrEntity所属的属性分组id,一个属性对应一个属性分组id
                AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (attrId != null && attrId.getAttrGroupId() != null) {
                    //1.2 根据属性分组id查询属性分组的名称
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                    AttrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            //2.设置当前属性所属的分类名称
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            AttrRespVo.setCatelogName(categoryEntity != null ? categoryEntity.getName() : null);
            return AttrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(BaseVos);
        return pageUtils;
    }

    /**
     * 实现修改对话框中所属分类和所属分组的回显，返回带有分类路径和属性分组id的vo对象
     * @param attrId
     * @return
     */
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo respVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, respVo); //首先根据属性id查询到属性信息，将属性信息拷贝到AttrRespVo中
        //1.设置属性所属的分组id和分组名称
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity attrgroupRelation = relationDao.selectOne( //查询属性所属的分组id
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            Long attrGroupId = attrgroupRelation.getAttrGroupId();
            respVo.setAttrGroupId(attrGroupId);
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
            if(attrGroupEntity != null){
                respVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }
        //设置属性的分类路径和分类id以及分类名称
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        respVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if(categoryEntity != null){
            respVo.setCatelogName(categoryEntity.getName());
        }
        return respVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity); //首先更新当前表的信息
        if(attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //1.修改分组关联
            Long attrId = attr.getAttrId();
            Long count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attrId);
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            //查看当前属性有没有属性分组信息， 如果有就是修改，如果没有就是新增
            if(count > 0){
                //1.修改分组关联，也就是修改属性所属的分组信息，即中间表
                relationDao.update(relationEntity, new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            }else{
                relationDao.insert(relationEntity);
            }
        }
    }

    /**
     * 根据分组id查找关联的所有基本属性，不考虑销售属性(针对具体某一个商品)
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        List<AttrEntity> attrEntities = null;
        //entities是根据属性分组id在中间表中查出的数据，因此首先要判断非空
        if(entities != null && entities.size() > 0){
            /*List<AttrEntity> result = entities.stream().map(attrAttrgroupRelationEntity ->
                    this.getById(attrAttrgroupRelationEntity.getAttrId())
            ).collect(Collectors.toList()); 这样写要访问多次数据库,给数据库带来压力*/
            Set<Long> attrIds = entities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toSet());
            attrEntities = this.listByIds(attrIds);  //这样就只需要查询一次数据库，底层的sql语句是where id in ()
        }
        return attrEntities;
    }

    //传过来的AttrGroupRelationVo里面包括属性id和属性分组id，删除需要按照属性id和属性分组id来删除
    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        //where "attr_id" = 1L and "attr_group_id" = 1L or "attr_id" = 2L and "attr_group_id" = 3L
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteRelation(entities);
    }

    /**
     * 获取可以关联到当前分组的属性信息
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //1.当前分组只能关联自己所属分类里面的所有基本属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);//找到当前属性分组的所属分类id
        Long catelogId = attrGroupEntity.getCatelogId();
        //2.当前分组只能关联别的分组没有引用的属性
        //2.1 当前分类下的其他分组(不包括当前分组)的id和本分组的id，也就是本分类下的所有属性分组collect
        List<AttrGroupEntity> entities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> collect = entities.stream().map(entity -> entity.getAttrGroupId()).collect(Collectors.toList());
        //2.2 其他分组关联的属性和本分组关联的属性
        List<Long> attrIds = null;
        if(collect != null || collect.size() > 0){
            List<AttrAttrgroupRelationEntity> attrs = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
            attrIds = attrs.stream().map(item -> item.getAttrId()).collect(Collectors.toList());
        }
        //2.3 从当前分类的所有属性中移除其他分组关联属性和本分组关联的属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(attrIds != null && attrIds.size() > 0){ //如果其他分类关联的属性为空，则直接查询属性表中属于指定分类的属性
            wrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if(StringUtils.hasText(key)){
            wrapper.and(obj -> {
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

}