package org.linlinjava.litemall.core.notify.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "litemall.notify")
public class NotifyProperties {
    private Mail mail;
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

    public static class Mail {
        private boolean enable;
        private String host;
        private String username;
        private String password;
        private String sendfrom;
        private String sendto;
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

    public static class Sms {
        private boolean enable;
        private String active;
        private String sign;
        private Tencent tencent;
        private Aliyun aliyun;
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

        public static class Tencent {
            private int appid;
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

        public static class Aliyun {
            private String regionId;
            private String accessKeyId;
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
