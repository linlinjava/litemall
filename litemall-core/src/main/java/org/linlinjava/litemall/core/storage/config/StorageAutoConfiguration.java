package org.linlinjava.litemall.core.storage.config;

import org.linlinjava.litemall.core.storage.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 装配四种存储适配器，并根据 active 为统一入口选择一个实现。
 * <p>各工厂均声明为 Bean；选择 active 不代表其余工厂不会被 Spring 初始化。
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageAutoConfiguration {

    private final StorageProperties properties;

    public StorageAutoConfiguration(StorageProperties properties) {
        this.properties = properties;
    }

    /**
     * 按 active 选择统一存储入口的委托实现，保持业务层调用方式一致。
     * @return 已绑定存储实现的服务
     * @throws RuntimeException active 不是 local、aliyun、tencent、qiniu 时
     * @throws NullPointerException active 未配置时
     */
    @Bean
    public StorageService storageService() {
        StorageService storageService = new StorageService();
        String active = this.properties.getActive();
        storageService.setActive(active);
        if (active.equals("local")) {
            storageService.setStorage(localStorage());
        } else if (active.equals("aliyun")) {
            storageService.setStorage(aliyunStorage());
        } else if (active.equals("tencent")) {
            storageService.setStorage(tencentStorage());
        } else if (active.equals("qiniu")) {
            storageService.setStorage(qiniuStorage());
        } else {
            throw new RuntimeException("当前存储模式 " + active + " 不支持");
        }

        return storageService;
    }

    /**
     * 将本地磁盘路径与访问地址配置注入对应适配器。
     * @return 完成配置的存储适配器
     */
    @Bean
    public LocalStorage localStorage() {
        LocalStorage localStorage = new LocalStorage();
        StorageProperties.Local local = this.properties.getLocal();
        localStorage.setAddress(local.getAddress());
        localStorage.setStoragePath(local.getStoragePath());
        return localStorage;
    }

    /**
     * 将阿里云 OSS 凭据与桶配置注入对应适配器。
     * @return 完成配置的存储适配器
     */
    @Bean
    public AliyunStorage aliyunStorage() {
        AliyunStorage aliyunStorage = new AliyunStorage();
        StorageProperties.Aliyun aliyun = this.properties.getAliyun();
        aliyunStorage.setAccessKeyId(aliyun.getAccessKeyId());
        aliyunStorage.setAccessKeySecret(aliyun.getAccessKeySecret());
        aliyunStorage.setBucketName(aliyun.getBucketName());
        aliyunStorage.setEndpoint(aliyun.getEndpoint());
        return aliyunStorage;
    }

    /**
     * 将腾讯云 COS 凭据、桶与地域配置注入对应适配器。
     * @return 完成配置的存储适配器
     */
    @Bean
    public TencentStorage tencentStorage() {
        TencentStorage tencentStorage = new TencentStorage();
        StorageProperties.Tencent tencent = this.properties.getTencent();
        tencentStorage.setSecretId(tencent.getSecretId());
        tencentStorage.setSecretKey(tencent.getSecretKey());
        tencentStorage.setBucketName(tencent.getBucketName());
        tencentStorage.setRegion(tencent.getRegion());
        return tencentStorage;
    }

    /**
     * 将七牛云凭据、桶与访问域名配置注入对应适配器。
     * @return 完成配置的存储适配器
     */
    @Bean
    public QiniuStorage qiniuStorage() {
        QiniuStorage qiniuStorage = new QiniuStorage();
        StorageProperties.Qiniu qiniu = this.properties.getQiniu();
        qiniuStorage.setAccessKey(qiniu.getAccessKey());
        qiniuStorage.setSecretKey(qiniu.getSecretKey());
        qiniuStorage.setBucketName(qiniu.getBucketName());
        qiniuStorage.setEndpoint(qiniu.getEndpoint());
        return qiniuStorage;
    }
}
