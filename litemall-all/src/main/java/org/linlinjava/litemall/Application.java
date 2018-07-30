package org.linlinjava.litemall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {
        "org.linlinjava.litemall",
        "org.linlinjava.litemall.core",
        "org.linlinjava.litemall.db",
        "org.linlinjava.litemall.wx",
        "org.linlinjava.litemall.admin"})
@MapperScan("org.linlinjava.litemall.db.dao")
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}