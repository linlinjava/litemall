package org.linlinjava.litemall.core.notify.config;

import com.github.qcloudsms.SmsSingleSender;
import org.linlinjava.litemall.core.notify.AliyunSmsSender;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.linlinjava.litemall.core.notify.TencentSmsSender;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 通知功能自动装配配置。
 *
 * <p>根据 {@code litemall.notify} 配置构建 {@link NotifyService}，
 * 邮件/短信启用时分别注入对应发送器；短信渠道按 {@code active} 选择腾讯云或阿里云。
 */
@Configuration
@EnableConfigurationProperties(NotifyProperties.class)
public class NotifyAutoConfiguration {

    /** 通知配置 */
    private final NotifyProperties properties;

    public NotifyAutoConfiguration(NotifyProperties properties) {
        this.properties = properties;
    }

    /**
     * 构建通知服务，按配置装配邮件/短信发送器
     *
     * @return 配置完成的 NotifyService
     */
    @Bean
    public NotifyService notifyService() {
        NotifyService notifyService = new NotifyService();

        NotifyProperties.Mail mailConfig = properties.getMail();
        if (mailConfig.isEnable()) {
            notifyService.setMailSender(mailSender());
            notifyService.setSendFrom(mailConfig.getSendfrom());
            notifyService.setSendTo(mailConfig.getSendto());
        }

        NotifyProperties.Sms smsConfig = properties.getSms();
        if (smsConfig.isEnable()) {
            if(smsConfig.getActive().equals("tencent")) {
                notifyService.setSmsSender(tencentSmsSender());
            }
            else if(smsConfig.getActive().equals("aliyun")) {
                notifyService.setSmsSender(aliyunSmsSender());
            }

            notifyService.setSmsTemplate(smsConfig.getTemplate());
        }

        return notifyService;
    }

    /**
     * 构建 JavaMail 邮件发送器
     *
     * @return 基于配置的 JavaMailSender
     */
    public JavaMailSender mailSender() {
        NotifyProperties.Mail mailConfig = properties.getMail();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailConfig.getHost());
        mailSender.setUsername(mailConfig.getUsername());
        mailSender.setPassword(mailConfig.getPassword());
        mailSender.setPort(mailConfig.getPort());
        Properties properties = new Properties();
        properties.put("mail.smtp.auth",true);
        properties.put("mail.smtp.timeout",5000);
        properties.put("mail.smtp.starttls.enable",true);
        properties.put("mail.smtp.socketFactory.fallback", "false");
        //阿里云 必须加入配置 outlook配置又不需要 视情况而定.发送不成功多数是这里的配置问题
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.port", mailConfig.getPort());
        properties.put("debug", true);
        mailSender.setJavaMailProperties(properties);
        return mailSender;
    }

    /**
     * 构建腾讯云短信发送器
     *
     * @return 基于配置的 TencentSmsSender
     */
    public TencentSmsSender tencentSmsSender() {
        NotifyProperties.Sms smsConfig = properties.getSms();
        TencentSmsSender smsSender = new TencentSmsSender();
        NotifyProperties.Sms.Tencent tencent = smsConfig.getTencent();
        smsSender.setSender(new SmsSingleSender(tencent.getAppid(), tencent.getAppkey()));
        smsSender.setSign(smsConfig.getSign());
        return smsSender;
    }

    /**
     * 构建阿里云短信发送器
     *
     * @return 基于配置的 AliyunSmsSender
     */
    public AliyunSmsSender aliyunSmsSender() {
        NotifyProperties.Sms smsConfig = properties.getSms();
        AliyunSmsSender smsSender = new AliyunSmsSender();
        NotifyProperties.Sms.Aliyun aliyun = smsConfig.getAliyun();
        smsSender.setSign(smsConfig.getSign());
        smsSender.setRegionId(aliyun.getRegionId());
        smsSender.setAccessKeyId(aliyun.getAccessKeyId());
        smsSender.setAccessKeySecret(aliyun.getAccessKeySecret());
        return smsSender;
    }
}
