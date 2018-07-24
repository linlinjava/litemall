package org.linlinjava.litemall.core.notify.config;

import com.github.qcloudsms.SmsSingleSender;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.linlinjava.litemall.core.notify.TencentSmsSender;
import org.linlinjava.litemall.core.notify.WxTemplateSender;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@EnableConfigurationProperties(NotifyProperties.class)
public class NotifyAutoConfiguration {

    private final NotifyProperties properties;

    public NotifyAutoConfiguration(NotifyProperties properties) {
        this.properties = properties;
    }

    @Bean
    public NotifyService notifyService(){
        NotifyService notifyService = new NotifyService();

        NotifyProperties.Mail mailConfig = properties.getMail();
        if(mailConfig.isEnable()) {
            notifyService.setMailSender(mailSender());
            notifyService.setSendFrom(mailConfig.getSendfrom());
            notifyService.setSendTo(mailConfig.getSendto());
        }

        NotifyProperties.Sms smsConfig = properties.getSms();
        if(smsConfig.isEnable()){
            notifyService.setSmsSender(tencentSmsSender());
            notifyService.setSmsTemplate(smsConfig.getTemplate());
        }

        NotifyProperties.Wx wxConfig = properties.getWx();
        if(wxConfig.isEnable()){
            notifyService.setWxTemplateSender(wxTemplateSender());
            notifyService.setWxTemplate(wxConfig.getTemplate());
        }
        return notifyService;
    }

    @Bean
    public JavaMailSender mailSender(){
        NotifyProperties.Mail mailConfig = properties.getMail();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailConfig.getHost());
        mailSender.setUsername(mailConfig.getUsername());
        mailSender.setPassword(mailConfig.getPassword());
        return mailSender;
    }

    @Bean
    public WxTemplateSender wxTemplateSender(){
        WxTemplateSender wxTemplateSender = new WxTemplateSender();
        return wxTemplateSender;
    }

    @Bean
    public TencentSmsSender tencentSmsSender(){
        NotifyProperties.Sms smsConfig = properties.getSms();
        TencentSmsSender smsSender = new TencentSmsSender();
        smsSender.setSender(new SmsSingleSender(smsConfig.getAppid(), smsConfig.getAppkey()));
        return smsSender;
    }
}
