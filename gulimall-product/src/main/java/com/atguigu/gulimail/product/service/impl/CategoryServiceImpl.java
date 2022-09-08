package com.atguigu.gulimail.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.product.dao.CategoryDao;
import com.atguigu.gulimail.product.entity.CategoryEntity;
import com.atguigu.gulimail.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查出所有分类，因为设置了逻辑删除，所有查询的时候会查询show_status=1的数据
        List<CategoryEntity> entities = list();
        //2.组装成父子的树形结构
        //2.1 找到所有的一级分类 parent_cid为0的都是一级分类
        List<CategoryEntity> level1Menus = entities.stream().
                filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(getChildren(menu, entities)); //给一级分类中的所有分类实体属性赋值
                    return menu;
                }).sorted((menu1, menu2) -> {
                    return(menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
                .collect(Collectors.toList());
        //2.2 找到所有的二级分类
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前删除的菜单，是否被别的地方引用
        //逻辑删除 show_status为1表示显示，也就是没有删除。而show_status为01表示不显示，也就是逻辑删除
        removeBatchByIds(asList); //mybatis-plus设置了逻辑删除，事实上并没有真的删除，执行的是更新语句
    }

    /**
     * 得到指定分类id到所属一级分类id的路径
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        CategoryEntity byId = this.getById(catelogId);
        while(true){
            paths.add(0, byId.getCatId());
            if(byId.getParentCid() == 0){
                break;
            }
            byId = this.getById(byId.getParentCid());
        }
        return paths.toArray(new Long[paths.size()]);
    }

    /**
     * 从所有的分类数据中求指定分类的子分类
     * sort小的值排在前面
     * @param root 分类实体
     * @param all 所有的分类数据
     * @return
     */
    public List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream()
                //Long数值在[-128,127]会直接从缓存中返回对应的引用，否则新创建一个新的实例，如果这里使用==比较的是地址，根分类的catId和本分类的parentCid是两个不同的Long对象，虽然值相同
                .filter(entity -> entity.getParentCid().equals(root.getCatId())) //如果当前分类下没有子分类，这个filter后的List是一个空List，而不是里面存有null的List。因此也不会执行getChildren函数，当前递归中止，返回的结果是一个[]
                .map(menu -> { //menu指的是某一个一级分类下的所有二级分类
                    menu.setChildren(getChildren(menu, all)); //求某一个二级分类menu下所有的三级分类
                    return menu;
                })
                .sorted((menu1, menu2) -> {
                    //如果menu1.getSort()为null, 使用null做减法会报出空指针异常
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
                .collect(Collectors.toList());
        return children;
    }

}