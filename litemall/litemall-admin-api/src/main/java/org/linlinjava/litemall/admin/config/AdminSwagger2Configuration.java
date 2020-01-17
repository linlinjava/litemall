package org.linlinjava.litemall.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger在线文档配置<br>
 * 项目启动后可通过地址：http://host:ip/swagger-ui.html 查看在线文档
 *
 * @author enilu
 * @version 2018-07-24
 */

@Configuration
@EnableSwagger2
public class AdminSwagger2Configuration {
    @Bean
    public Docket adminDocket() {

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("admin")
                .apiInfo(adminApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.linlinjava.litemall.admin.web"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo adminApiInfo() {
        return new ApiInfoBuilder()
                .title("litemall-admin API")
                .description("litemall管理后台API")
                .termsOfServiceUrl("https://github.com/linlinjava/litemall")
                .contact("https://github.com/linlinjava/litemall")
                .version("1.0")
                .build();
    }
}
