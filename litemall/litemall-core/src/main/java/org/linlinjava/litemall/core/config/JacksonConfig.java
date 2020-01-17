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

@Configuration
public class JacksonConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder builder) {
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
                builder.serializationInclusion(JsonInclude.Include.NON_NULL);
                builder.failOnUnknownProperties(false);
                builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }
        };
    }
}
