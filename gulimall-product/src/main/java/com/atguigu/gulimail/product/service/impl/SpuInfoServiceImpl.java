package com.atguigu.gulimail.product.service.impl;

import com.atguigu.common.to.MemberPrice;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimail.product.dao.SpuInfoDescDao;
import com.atguigu.gulimail.product.entity.*;
import com.atguigu.gulimail.product.feign.CouponFeignService;
import com.atguigu.gulimail.product.service.*;
import com.atguigu.gulimail.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimail.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * ???????????????????????????????????????
     * @param vo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1.??????spu??????????????? pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity); //??????????????????????????????????????????id???????????????javabean?????????id???
        Long spuId = spuInfoEntity.getId();
        //2.??????spu??????????????? pms_spu_info_desc(???????????????????????????????????????????????????)
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity desc = new SpuInfoDescEntity();
        desc.setSpuId(spuId);
        desc.setDecript(String.join(",", decript)); //???decript????????????????????????????????????
        spuInfoDescService.save(desc);
        //3.??????spu???????????? pms_spu_images
        SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuId, images);
        //4.??????spu??????????????? pms_product_attr_value???
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        productAttrValueService.saveBaseAttrs(spuId, baseAttrs);
        //5.??????spu??????????????? gulimail_sms -> sms_spu_bounds
        Bounds bounds = vo.getBounds(); //??????????????????spu??????????????????????????????
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuId);
        R r = couponFeignService.saveSpuBounds(spuBoundsTo);
        if(r.getCode() != 0){
            log.error("????????????spu??????????????????!");
        }
        //5.??????spu???????????????sku??????
        List<Skus> skus = vo.getSkus();
        if(!CollectionUtils.isEmpty(skus)){
            skus.forEach(sku -> {
                //????????????????????????
                String defaultImg = "";
                for (Images image : sku.getImages()) {
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L); //?????????????????????
                skuInfoEntity.setSpuId(spuId);
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //5.1 ??????sku??????????????? pms_sku_info
                skuInfoService.save(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();
                //?????????????????????????????????
                List<SkuImagesEntity> imagesEntities = sku.getImages().stream().map(image -> {
                    //image???????????????????????? imgUrl???defaultImg
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    BeanUtils.copyProperties(image, skuImagesEntity);
                    skuImagesEntity.setSkuId(skuId);
                    return skuImagesEntity;
                }).filter(entity -> {
                    //??????true?????????????????????false??????????????????
                    return StringUtils.hasText(entity.getImgUrl());
                }).collect(Collectors.toList());
                //5.2 ??????sku??????????????? pms_sku_images
                skuImagesService.saveBatch(imagesEntities);
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity valueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, valueEntity);
                    valueEntity.setSkuId(skuId);
                    return valueEntity;
                }).collect(Collectors.toList());
                //5.3 ??????sku????????????????????? pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                //5.4 ??????sku??????????????????????????? gulimail_sms -> sms_sku_ladder???sms_sku_full_reduction???sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                //????????????SkuReductionTo???MemberPrice???sku???MemberPrice?????????????????????BeanUtils????????????
                List<MemberPrice> prices = sku.getMemberPrice().stream().map(memberPrice -> {
                    MemberPrice price = new MemberPrice();
                    price.setId(memberPrice.getId());
                    price.setName(memberPrice.getName());
                    price.setPrice(memberPrice.getPrice());
                    return price;
                }).collect(Collectors.toList());
                skuReductionTo.setMemberPrice(prices);
                skuReductionTo.setSkuId(skuId); //skuId??????sku???????????????????????????????????????????????????????????????
                //???????????????????????????????????????????????????????????????????????????????????????
                if(skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) == 1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode() != 0){
                        log.error("????????????sku????????????????????????!");
                    }
                }
            });
        }


    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.save(spuInfoEntity);
    }

    @Override
    public PageUtils queryPagByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(StringUtils.hasText(key)){
            wrapper.and(w -> { //where (id = key or spu_name like '%key%') ,???????????????????????????????????????
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        String status = (String) params.get("status");
        if(StringUtils.hasText(status)){
            wrapper.eq("publish_status", status);
        }
        String brandId = (String) params.get("brandId");
        if(StringUtils.hasText(brandId) && !"0".equals(brandId)){
            wrapper.eq("brand_id", brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if(StringUtils.hasText(catelogId) && !"0".equals(catelogId)){
            wrapper.eq("catalog_id", catelogId);
        }
        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

}