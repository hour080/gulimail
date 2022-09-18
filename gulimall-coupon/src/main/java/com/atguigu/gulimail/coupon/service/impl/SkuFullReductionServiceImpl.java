package com.atguigu.gulimail.coupon.service.impl;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.gulimail.coupon.entity.MemberPriceEntity;
import com.atguigu.gulimail.coupon.entity.SkuLadderEntity;
import com.atguigu.gulimail.coupon.service.MemberPriceService;
import com.atguigu.gulimail.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.coupon.dao.SkuFullReductionDao;
import com.atguigu.gulimail.coupon.entity.SkuFullReductionEntity;
import com.atguigu.gulimail.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //1.保存满减打折，会员价
        //保存sku的优惠、满减等信息 gulimail_sms -> sms_sku_ladder、sms_sku_full_reduction、sms_member_price
        //满几件打几折
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo, skuLadderEntity);
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        if(skuReductionTo.getFullCount() > 0){
            skuLadderService.save(skuLadderEntity);
        }
        //满多少元减多少元
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(skuReductionTo.getPriceStatus());
        if(skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) == 1){
            this.save(skuFullReductionEntity);
        }
        //保存会员价格。例如铜牌会员价格和银牌会员价格
        List<MemberPriceEntity> collect = skuReductionTo.getMemberPrice().stream().map(memberPrice -> {
            MemberPriceEntity entity = new MemberPriceEntity();
            entity.setMemberLevelId(memberPrice.getId());
            entity.setMemberLevelName(memberPrice.getName());
            entity.setMemberPrice(memberPrice.getPrice());
            entity.setSkuId(skuReductionTo.getSkuId());
            entity.setAddOther(1); //默认叠加其他优惠
            return entity;
            //返回true的保留，返回false的去除
        }).filter(item -> {
            return item.getMemberPrice().compareTo(BigDecimal.ZERO) == 1;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);
    }

}