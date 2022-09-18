package com.atguigu.gulimail.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import com.atguigu.gulimail.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimail.ware.service.PurchaseDetailService;
import com.atguigu.gulimail.ware.service.WareSkuService;
import com.atguigu.gulimail.ware.vo.MergeVo;
import com.atguigu.gulimail.ware.vo.PurchaseDoneVo;
import com.atguigu.gulimail.ware.vo.PurchaseItemDoneVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.ware.dao.PurchaseDao;
import com.atguigu.gulimail.ware.entity.PurchaseEntity;
import com.atguigu.gulimail.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    WareSkuService wareSkuService; //用于保存商品的库存信息
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询未领取的采购单，也就是status为0或者status为1的采购单
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageUnreceivedPurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        //如果采购单id为null,说明要新建一个采购单,并保存到数据库
        if(purchaseId == null){
            //默认配置采购单的创建时间、更新时间和状态
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode()); //0表示该采购单为新建采购单
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity); //保存到数据库后就会产生采购单id
            purchaseId = purchaseEntity.getId();
        }
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        //获取到采购单id以后，需要修改采购需求的状态以及采购单id
        //如果采购需求的状态为0和1，也就是新建和已分配的状态才可以将采购需求合并为采购项
        List<PurchaseDetailEntity> entities = purchaseDetailService.listByIds(items)
                .stream().filter(item -> {
                    if(item.getStatus() == WareConstant.PurchaseDetailStatusEnum.CREATED.getCode()
                    || item.getStatus() == WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode()){
                        return true;
                    }else {
                        return false;
                    }
                }).collect(Collectors.toList());
        List<PurchaseDetailEntity> collect = entities.stream().map(item -> {
            PurchaseDetailEntity entity = new PurchaseDetailEntity();
            entity.setId(item.getId());
            entity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode()); //当采购单分配给采购需求时，采购需求的状态就变为已分配
            entity.setPurchaseId(finalPurchaseId);
            return entity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        //由于操作了采购单，所以采购单的更新时间需要进行修改
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(finalPurchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    @Transactional
    @Override
    public void receivePurchase(List<Long> purchaseIds) {
        List<PurchaseEntity> collect = purchaseIds.stream().map(id -> {
            PurchaseEntity entity = this.getById(id);
            return entity;
        }).filter(item -> {
            //1.确认当前采购单是新建状态或者已分配状态，这样采购单才可以被领取
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()
                    || item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            //改变采购单的状态为已领取,改变采购单的更新时间
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());
        //2.改变采购单的状态
        this.updateBatchById(collect); //批量修改可以领取的采购单的状态
        //3.改变采购需求的状态，也就是通过采购单id找到所有的采购需求
        collect.forEach(item -> {
            //根据采购单id获取所有的采购需求(采购项)
            List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntites = entities.stream().map(entity -> {
                PurchaseDetailEntity entity1 = new PurchaseDetailEntity();
                //这里不需要给entity1的其他属性赋值，否则会修改其他属性
                entity1.setId(entity.getId());
                entity1.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return entity1;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntites); //根据采购项id来修改采购项的状态
        });

    }

    /**
     * 完成采购
     * @param doneVo 包含采购单id以及每一个采购项的状态信息
     */
    @Transactional
    @Override
    public void done(PurchaseDoneVo doneVo) {
        Long id = doneVo.getId();
        //1.改变采购单中每一个采购项的状态
        Boolean flag = true; //只要有一个采购项失败，flag就为false
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(item.getItemId()); //设置采购项的id
            if(item.getStatus() == WareConstant.PurchaseDetailStatusEnum.FAILED.getCode()){
                flag = false;
                detailEntity.setStatus(item.getStatus()); //修改当前采购项的状态为采购失败
            }else {
                //修改当前采购项的状态为已完成
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISHED.getCode());
                //3.将成功采购的商品入库，也就是给商品库存添加相应的数量 需要哪个sku_id给哪个ware_id添加几个
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
            }
            updates.add(detailEntity);
        }
        //批量更新所有的采购项
        purchaseDetailService.updateBatchById(updates);
        //2.改变采购单状态(根据采购项的状态来决定)
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        if(flag){
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISHED.getCode());
        } else {
          purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.EXISTERROR.getCode());
        }
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

}