package com.atguigu.gulimail.product.vo;

import lombok.Data;

/**
 * TODO
 *
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/9/9 16:10
 */
@Data
public class AttrRespVo extends AttrVo {
    private String catelogName; //属性所属的分类名
    private String groupName;  //属性所属的分组名
    private Long[] catelogPath; //属性所属的分类路径
}
