package com.atguigu.gulimail.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * TODO
 *
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/9/17 20:11
 */
@Data
public class MergeVo {
    private Long purchaseId; //采购单id
    private List<Long> items; //采购需求id
}
