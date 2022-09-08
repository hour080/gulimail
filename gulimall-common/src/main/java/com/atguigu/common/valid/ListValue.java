package com.atguigu.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {ListValueConstraintValidator.class}) //校验注解使用哪个校验器校验
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE }) //注解可以标注在哪些位置
@Retention(RUNTIME) //注解的获取时机
public @interface ListValue {
    String message() default "{com.atguigu.common.valid.ListValue.message}"; //自定义校验注解的校验出错信息

    Class<?>[] groups() default { };  //校验注解的分组信息，也就是在哪种情况下适用

    Class<? extends Payload>[] payload() default { }; //自定义注解的负载信息

    int[] values()  default { };
}
