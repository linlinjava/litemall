package org.linlinjava.litemall.core.notify.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商城通知配置。
 *
 * <p>绑定前缀为 {@code litemall.notify} 的配置项，包含邮件与短信（腾讯云/阿里云）发送相关配置。
 */
@ConfigurationProperties(prefix = "litemall.notify")
public class NotifyProperties {
    /** 邮件通知配置 */
    private Mail mail;
    /** 短信通知配置 */
    private Sms sms;

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public Sms getSms() {
        return sms;
    }

    public void setSms(Sms sms) {
        this.sms = sms;
    }

    /**
     * 邮件发送配置，对应配置项 litemall.notify.mail.*
     */
    public static class Mail {
        /** 是否启用邮件通知 */
        private boolean enable;
        /** SMTP 服务器地址 */
        private String host;
        /** SMTP 登录用户名 */
        private String username;
        /** SMTP 登录密码 */
        private String password;
        /** 发件人地址 */
        private String sendfrom;
        /** 收件人地址 */
        private String sendto;
        /** SMTP 端口 */
        private Integer port;
        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSendfrom() {
            return sendfrom;
        }

        public void setSendfrom(String sendfrom) {
            this.sendfrom = sendfrom;
        }

        public String getSendto() {
            return sendto;
        }

        public void setSendto(String sendto) {
            this.sendto = sendto;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }

    /**
     * 短信发送配置，对应配置项 litemall.notify.sms.*
     */
    public static class Sms {
        /** 是否启用短信通知 */
        private boolean enable;
        /** 当前生效的短信渠道，取值 tencent 或 aliyun */
        private String active;
        /** 短信签名 */
        private String sign;
        /** 腾讯云短信配置 */
        private Tencent tencent;
        /** 阿里云短信配置 */
        private Aliyun aliyun;
        /** 短信模板列表，元素为 {name -> templateId} 的映射 */
        private List<Map<String, String>> template = new ArrayList<>();

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public List<Map<String, String>> getTemplate() {
            return template;
        }

        public void setTemplate(List<Map<String, String>> template) {
            this.template = template;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public Tencent getTencent() {
            return tencent;
        }

        public void setTencent(Tencent tencent) {
            this.tencent = tencent;
        }

        public Aliyun getAliyun() {
            return aliyun;
        }

        public void setAliyun(Aliyun aliyun) {
            this.aliyun = aliyun;
        }

        /**
         * 腾讯云短信配置
         */
        public static class Tencent {
            /** 腾讯云短信应用 ID */
            private int appid;
            /** 腾讯云短信应用密钥 */
            private String appkey;

            public int getAppid() {
                return appid;
            }

            public void setAppid(int appid) {
                this.appid = appid;
            }

            public String getAppkey() {
                return appkey;
            }

            public void setAppkey(String appkey) {
                this.appkey = appkey;
            }
        }

        /**
         * 阿里云短信配置
         */
        public static class Aliyun {
            /** 阿里云区域 ID，如 cn-hangzhou */
            private String regionId;
            /** 阿里云 AccessKey ID */
            private String accessKeyId;
            /** 阿里云 AccessKey Secret */
            private String accessKeySecret;

            public String getRegionId() {
                return regionId;
            }

            public void setRegionId(String regionId) {
                this.regionId = regionId;
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
        }
    }

}
