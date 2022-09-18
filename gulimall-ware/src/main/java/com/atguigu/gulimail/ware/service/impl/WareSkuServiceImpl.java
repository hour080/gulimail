package com.atguigu.gulimail.ware.service.impl;

import com.atguigu.common.utils.R;
import com.atguigu.gulimail.ware.feign.ProductFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.ware.dao.WareSkuDao;
import com.atguigu.gulimail.ware.entity.WareSkuEntity;
import com.atguigu.gulimail.ware.service.WareSkuService;
import org.springframework.util.StringUtils;


@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(StringUtils.hasText(skuId)){
            wrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if(StringUtils.hasText(wareId)){
            wrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据已完成的采购项，向商品库存中添加sku
     * @param skuId 要添加的商品id
     * @param wareId 添加商品所在的仓库id
     * @param skuNum 要添加的商品id的数量
     */
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.判断商品库存表是否存在相应记录
        List<WareSkuEntity> skuEntities = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(skuEntities.isEmpty()){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0); //设置锁定库存为0
            R info = productFeignService.info(skuId); //这里传过来是json数据，R是一个map
            //捕获远程调用的异常
            try{
                if(info.getCode() == 0){
                    //如果远程调用成功
                    Map<String, Object> data = (Map<String, Object>) info.get("skuInfo"); //其中的skuinfo也是一个map,因为传过来的数据是json格式的对象，对应于java中的map
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){
                log.error("远程调用商品服务获得sku名字出现异常！！");
            }
            this.baseMapper.insert(wareSkuEntity);
        }else {
            this.baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

}