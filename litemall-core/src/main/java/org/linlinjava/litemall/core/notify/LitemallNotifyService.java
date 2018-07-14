package org.linlinjava.litemall.core.notify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Litemall商城通知服务类
 */
@PropertySource(value = "classpath:notify.properties")
@Service("litemallNotifyService")
public class LitemallNotifyService {
    @Autowired
    MailSendService mailSendService;
    @Autowired
    SMSSendService smsSendService;
    @Autowired
    Environment environment;

    @Value("${sprint.mail.enable}")
    private boolean sendMailEnable;
    @Value("${spring.sms.enable}")
    private boolean sendSMSEnable;

    public void notifySMSMessage(String phoneNumber,String message) {
        if (!sendSMSEnable)
            return;

        smsSendService.sendSMS(phoneNumber, message);
    }

    /**
     * 短信模版通知
     * @param phoneNumber   接收通知的电话号码
     * @param params        通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     * @param notifyType    通知类别，通过该枚举值在配置文件中获取相应的模版ID
     */
    public void notifySMSTemplate(String phoneNumber, String[] params, NotifyUtils.NotifyType notifyType) {
        if (!sendSMSEnable)
            return;

        int templateId = -1;
        switch (notifyType) {
            case PAY_COMPLATED:
                templateId = Integer.parseInt(environment.getProperty("spring.sms.template.pay.complated"));
                break;
            case VERIFICATIONCODE:
                templateId = Integer.parseInt(environment.getProperty("spring.sms.template.verificationcode"));
                break;
        }

        if (templateId != -1)
            smsSendService.sendSMSWithTemplate(phoneNumber, templateId, params);
    }

    /**
     * 发送邮件通知,接收者在spring.mail.sendto中指定
     * @param setSubject    邮件标题
     * @param setText       邮件内容
     */
    public void notifyMailMessage(String setSubject, String setText) {
        if(!sendMailEnable)
            return;

        mailSendService.sendEmail(setSubject, setText);
    }
}
