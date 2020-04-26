# 性能

## 1 小程序性能

## 2 VUE性能

## 3 Spring Boot性能

### 3.1 gzip压缩

在litemall-all模块中配置gzip压缩
```
server:
  compression:
    enabled: true
    min-response-size: 2048
    mime-types: application/javascript,text/css,application/json,application/xml,text/html,text/xml,text/plain
```

## 4 数据库性能

## 5 其他