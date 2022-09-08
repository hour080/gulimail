package com.atguigu.gulimail.product;

import com.atguigu.gulimail.product.entity.BrandEntity;
import com.atguigu.gulimail.product.service.BrandService;
import com.atguigu.gulimail.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class GulimailProductApplicationTests {

	@Autowired
	BrandService brandService;

	@Autowired
	CategoryService categoryService;


	@Test
	void contextLoads() {
		/*BrandEntity brandEntity = new BrandEntity();
		brandEntity.setName("华为");
		brandService.save(brandEntity);
		System.out.println("保存成功");*/
//		BrandEntity brandEntity = new BrandEntity();
//		brandEntity.setBrandId(1L);
//		brandEntity.setDescript("华为");
//		brandService.updateById(brandEntity);
		//QueryWrapper为查询条件，是mybatis plus中实现查询的对象封装操作类


		List<BrandEntity> list =
				brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
		System.out.println(list);
	}

	@Test
	void testMeunLevels(){
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		//如果说filter过滤后
		System.out.println(list.stream().filter(num -> num > 3).map(integer -> {
			System.out.println(integer.toString());
			return integer.toString();
		}).collect(Collectors.toList()));
	}
	@Test
	void testPage(){
		Page page = new Page(2, 3);
		QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("show_status", 0);
		brandService.getBaseMapper().selectPage(page, queryWrapper);
		page.getRecords().forEach(System.out::println);
	}
	@Test
	void testPath(){
		for (Long aLong : categoryService.findCatelogPath(1L)) {
			System.out.println(aLong);
		}
	}
}
