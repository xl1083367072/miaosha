package com.xl.miaosha.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = IsMobileValidation.class)
public @interface IsMobile {

    boolean required() default true;

    String message() default "手机号格式有误";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };


}
