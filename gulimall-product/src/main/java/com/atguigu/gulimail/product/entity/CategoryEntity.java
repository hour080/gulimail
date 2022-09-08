package com.atguigu.gulimail.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 商品三级分类
 * 
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 11:07:32
 */
@Data
@TableName("pms_category")
public class CategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	@TableId
	private Long catId;
	/**
	 * 分类名称
	 */
	private String name;
	/**
	 * 父分类id
	 */
	private Long parentCid;
	/**
	 * 层级
	 */
	private Integer catLevel;
	/**
	 * 是否显示[0-不显示，1显示]
	 */
	@TableLogic  //局部逻辑删除
	private Integer showStatus;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 图标地址
	 */
	private String icon;
	/**
	 * 计量单位
	 */
	private String productUnit;
	/**
	 * 商品数量
	 */
	private Integer productCount;
	/**
	 * 所有的子分类实体，该属性在数据表中不存在，这样从数据库中查询到的数据就不会封装到该属性中
	 * 如果该字段值为空列表，则不需要返回给前端，否则前端的级联选择框会多渲染一层
	 */
	@TableField(exist = false)
	@JsonInclude(JsonInclude.Include.NON_EMPTY) //如果列表的长度不为0，才会带上该属性序列化为json
	private List<CategoryEntity> children;

}
