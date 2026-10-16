package org.linlinjava.litemall.core.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 绑定 litemall.storage 配置，为本地及三种云存储提供初始化参数。
 * <p>active 选择实际使用的实现，凭据属于部署配置，不应写入源码注释。
 */
@ConfigurationProperties(prefix = "litemall.storage")
public class StorageProperties {
    /** 启用的存储类型：local、aliyun、tencent 或 qiniu。 */
    private String active;
    private Local local;
    private Aliyun aliyun;
    private Tencent tencent;
    private Qiniu qiniu;

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public Aliyun getAliyun() {
        return aliyun;
    }

    public void setAliyun(Aliyun aliyun) {
        this.aliyun = aliyun;
    }

    public Tencent getTencent() {
        return tencent;
    }

    public void setTencent(Tencent tencent) {
        this.tencent = tencent;
    }

    public Qiniu getQiniu() {
        return qiniu;
    }

    public void setQiniu(Qiniu qiniu) {
        this.qiniu = qiniu;
    }

    public static class Local {
        /** 本地文件对外访问前缀，需与资源映射一致并包含所需分隔符。 */
        private String address;
        /** 服务器磁盘保存目录，初始化时尝试创建。 */
        private String storagePath;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getStoragePath() {
            return storagePath;
        }

        public void setStoragePath(String storagePath) {
            this.storagePath = storagePath;
        }
    }

    public static class Tencent {
        /** 腾讯云访问密钥标识。 */
        private String secretId;
        /** 云服务访问密钥，由部署配置注入。 */
        private String secretKey;
        /** 腾讯云存储桶所属地域。 */
        private String region;
        /** 目标对象存储桶名称。 */
        private String bucketName;

        public String getSecretId() {
            return secretId;
        }

        public void setSecretId(String secretId) {
            this.secretId = secretId;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }

    public static class Aliyun {
        /** 服务端地址或访问域名，具体格式由相应适配器使用方式决定。 */
        private String endpoint;
        /** 阿里云访问密钥标识。 */
        private String accessKeyId;
        /** 阿里云访问密钥，由部署配置注入。 */
        private String accessKeySecret;
        /** 目标对象存储桶名称。 */
        private String bucketName;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }

    public static class Qiniu {
        /** 服务端地址或访问域名，具体格式由相应适配器使用方式决定。 */
        private String endpoint;
        /** 七牛云访问密钥标识。 */
        private String accessKey;
        /** 云服务访问密钥，由部署配置注入。 */
        private String secretKey;
        /** 目标对象存储桶名称。 */
        private String bucketName;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }
}
