package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * TODO
 *
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/9/16 0:04
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount; //满几件
    private BigDecimal discount; //打几折
    private int countStatus; //是否叠加优惠
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
