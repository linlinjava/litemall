package org.linlinjava.litemall.core.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson JSON 序列化全局配置
 *
 * <p>统一规定后端返回给前端的 JSON 规则：</p>
 * <ul>
 *     <li>时间类型按 “yyyy-MM-dd HH:mm:ss / yyyy-MM-dd / HH:mm:ss” 格式化，不再输出时间戳；</li>
 *     <li>值为 null 的字段不输出，减小报文体积；</li>
 *     <li>反序列化时忽略前端多传的未知字段，避免直接报错。</li>
 * </ul>
 *
 * @author litemall
 * @since 1.0
 */
@Configuration
public class JacksonConfig {

    /**
     * 自定义 ObjectMapper 构建器，最高优先级覆盖 Spring Boot 默认配置
     *
     * @return Jackson2ObjectMapperBuilderCustomizer 定制器
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder builder) {
                // LocalDateTime / LocalDate / LocalTime 的序列化与反序列化统一格式
                builder.serializerByType(LocalDateTime.class,
                        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                builder.serializerByType(LocalDate.class,
                        new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                builder.serializerByType(LocalTime.class,
                        new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
                builder.deserializerByType(LocalDateTime.class,
                        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                builder.deserializerByType(LocalDate.class,
                        new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                builder.deserializerByType(LocalTime.class,
                        new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
                // null 字段不参与序列化
                builder.serializationInclusion(JsonInclude.Include.NON_NULL);
                // 前端多传未知字段时不报错，提升接口兼容性
                builder.failOnUnknownProperties(false);
                // 关闭把日期写成时间戳数组的默认行为
                builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }
        };
    }
}
