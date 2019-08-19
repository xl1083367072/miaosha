package com.xl.miaosha.validation;

import com.xl.miaosha.utils.ValidateUtil;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidation implements ConstraintValidator<IsMobile,String> {

    private boolean required = true;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(required){
            return ValidateUtil.isMobile(value);
        }else {
            if(StringUtils.isEmpty(value)){
                return true;
            }else {
                return ValidateUtil.isMobile(value);
            }
        }
    }

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }
}
