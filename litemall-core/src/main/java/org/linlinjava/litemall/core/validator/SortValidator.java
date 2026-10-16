package org.linlinjava.litemall.core.validator;

import com.google.common.collect.Lists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * {@link Sort} 注解的校验器，校验排序字段是否在允许范围内。
 */
public class SortValidator implements ConstraintValidator<Sort, String> {
    private List<String> valueList;

    @Override
    public void initialize(Sort sort) {
        valueList = Lists.newArrayList();
        /* 将允许值统一转为大写，便于校验时忽略大小写 */
        for (String val : sort.accepts()) {
            valueList.add(val.toUpperCase());
        }
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        /* 校验时忽略大小写 */
        if (!valueList.contains(s.toUpperCase())) {
            return false;
        }
        return true;
    }
}
