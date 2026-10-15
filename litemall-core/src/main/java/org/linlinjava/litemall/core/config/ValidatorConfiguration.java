package org.linlinjava.litemall.core.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 参数校验器配置类
 *
 * <p>配置 JSR-303 参数校验（@Valid / @NotBlank 等）使用 Hibernate Validator 实现，
 * 并开启 fail_fast：一旦遇到第一个校验失败就立即返回，不必等全部字段校验完，
 * 便于接口快速给出明确的错误提示。</p>
 *
 * @author litemall
 * @since 1.0
 */
@Configuration
public class ValidatorConfiguration {
    /**
     * 构造并注册全局 Validator
     *
     * @return fail-fast 模式的 Validator 实例
     */
    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty("hibernate.validator.fail_fast", "true")
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
