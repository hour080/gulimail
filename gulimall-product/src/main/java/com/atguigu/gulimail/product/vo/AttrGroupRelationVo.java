package com.atguigu.gulimail.product.vo;

import lombok.Data;

/**
 * TODO
 *
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/9/11 10:38
 */
@Data
public class AttrGroupRelationVo {
    /**
     * 属性id
     */
    private Long attrId;
    /**
     * 保存到pms_attr表中使用的字段，除了要保存属性id以外，还要在中间表中保存属性所属的分组id(attr_group_id)
     */
    private Long attrGroupId;
}
