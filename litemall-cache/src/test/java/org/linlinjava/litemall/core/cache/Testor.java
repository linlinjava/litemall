package org.linlinjava.litemall.core.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class Testor {
    @Cacheable(value = "litemall" ,key = "targetClass + ' - ' + methodName")
    public int increateAndGet() {
        return count++;
    }

    private int count = 0;
}