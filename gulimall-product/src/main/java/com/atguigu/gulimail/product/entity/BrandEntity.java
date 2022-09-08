package com.atguigu.gulimail.product.entity;

import com.atguigu.common.valid.AddGroup;
import com.atguigu.common.valid.ListValue;
import com.atguigu.common.valid.UpdateGroup;
import com.atguigu.common.valid.UpdateStatusGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author hourui
 * @email hourui@gmail.com
 * @date 2022-08-24 11:07:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 品牌id
	 */
	@NotNull(message = "修改必须指定品牌id",groups = {UpdateGroup.class, UpdateStatusGroup.class})
	@Null(message = "新增不能指定品牌id",groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名必须提交",groups = {AddGroup.class, UpdateGroup.class}) //不能为空且必须包含至少一个非空字符
	private String name;
	/**
	 * 品牌logo地址
	 * 新增的时候logo地址不能为空
	 * 修改的时候logo地址可以为空(也就是不修改)，但是一旦不为空（修改），必须是一个合法的url地址
	 */
	@NotBlank(groups = {AddGroup.class})
	@URL(message = "logo必须是一个合法的url地址", groups = {AddGroup.class, UpdateGroup.class}) //如果只有一个@URL表示没提交该字段不会校验，一旦提交该字段的值则会进行校验
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(groups = {UpdateStatusGroup.class})
	@ListValue(values = {0, 1}, groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-z]$", message = "检索首字母必须是一个字母", groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序 ,整数字段不能使用@NotEmpty来进行校验
	 */
	@NotNull(groups = {AddGroup.class})
	@Min(value = 0, message = "排序必须大于等于0", groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}
