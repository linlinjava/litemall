package org.linlinjava.litemall.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Value("${swagger.enable:true}")
    private boolean enableSwagger;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enableSwagger)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.linlinjava.litemall"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("litemall")
                .description("又一个小商城。litemall = Spring Boot后端 + Vue管理员前端 + 微信小程序用户前端")
                .termsOfServiceUrl("https://github.com/linlinjava/litemall")
                .version("0.1.0")
                .license("MIT")
                .licenseUrl("https://github.com/linlinjava/litemall/blob/master/LICENSE")
                .contact(new Contact("linlinjava", "https://github.com/linlinjava", "linlinjava@163.com"))
                .build();
    }
}
