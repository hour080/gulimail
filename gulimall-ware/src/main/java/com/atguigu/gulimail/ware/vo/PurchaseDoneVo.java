package com.atguigu.gulimail.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO
 *
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/9/18 10:08
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id; //采购单id
    private List<PurchaseItemDoneVo> items;
}
