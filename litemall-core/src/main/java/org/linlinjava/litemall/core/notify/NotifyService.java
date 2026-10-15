package org.linlinjava.litemall.core.notify;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商城通知服务类。
 *
 * <p>统一封装邮件、短信两类通知能力，支持直发与模板发送。
 * 通过 {@link #isMailEnable()}、{@link #isSmsEnable()} 判断对应渠道是否已注入启用；
 * 邮件与短信的装配在 {@code NotifyAutoConfiguration} 中完成。
 */
public class NotifyService {
    /** 邮件发送器，为 null 表示未启用邮件通知 */
    private MailSender mailSender;
    /** 邮件发件人地址（来自配置 spring.mail.sendfrom） */
    private String sendFrom;
    /** 邮件收件人地址（来自配置 spring.mail.sendto） */
    private String sendTo;

    /** 短信发送器，为 null 表示未启用短信通知 */
    private SmsSender smsSender;
    /** 短信模板列表，元素为 {name -> templateId} 的映射 */
    private List<Map<String, String>> smsTemplate = new ArrayList<>();

    /** 预留的微信模板配置；保留现有设置入口，当前发送流程不读取该字段。 */
    private List<Map<String, String>> wxTemplate = new ArrayList<>();

    /**
     * 邮件通知是否启用
     *
     * @return true 表示已注入邮件发送器
     */
    public boolean isMailEnable() {
        return mailSender != null;
    }

    /**
     * 短信通知是否启用
     *
     * @return true 表示已注入短信发送器
     */
    public boolean isSmsEnable() {
        return smsSender != null;
    }

    /**
     * 短信消息通知
     *
     * @param phoneNumber 接收通知的电话号码
     * @param message     短消息内容，这里短消息内容必须已经在短信平台审核通过
     */
    @Async
    public void notifySms(String phoneNumber, String message) {
        if (smsSender == null)
            return;

        smsSender.send(phoneNumber, message);
    }

    /**
     * 短信模版消息通知
     *
     * @param phoneNumber 接收通知的电话号码
     * @param notifyType  通知类别，通过该枚举值在配置文件中获取相应的模版ID
     * @param params      通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     */
    @Async
    public void notifySmsTemplate(String phoneNumber, NotifyType notifyType, String[] params) {
        if (smsSender == null) {
            return;
        }

        String templateIdStr = getTemplateId(notifyType, smsTemplate);
        if (templateIdStr == null) {
            return;
        }

        smsSender.sendWithTemplate(phoneNumber, templateIdStr, params);
    }

    /**
     * 以同步的方式发送短信模版消息通知
     *
     * @param phoneNumber 接收通知的电话号码
     * @param notifyType  通知类别，通过该枚举值在配置文件中获取相应的模版ID
     * @param params      通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     * @return 短信发送结果；短信未启用时返回 null，模板缺失时由具体发送器处理
     */
    public SmsResult notifySmsTemplateSync(String phoneNumber, NotifyType notifyType, String[] params) {
        if (smsSender == null)
            return null;

        return smsSender.sendWithTemplate(phoneNumber, getTemplateId(notifyType, smsTemplate), params);
    }

    /**
     * 邮件消息通知,
     * 接收者在spring.mail.sendto中指定
     *
     * @param subject 邮件标题
     * @param content 邮件内容
     */
    @Async
    public void notifyMail(String subject, String content) {
        if (mailSender == null)
            return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sendFrom);
        message.setTo(sendTo);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    private String getTemplateId(NotifyType notifyType, List<Map<String, String>> values) {
        for (Map<String, String> item : values) {
            String notifyTypeStr = notifyType.getType();

            if (item.get("name").equals(notifyTypeStr))
                return item.get("templateId");
        }
        return null;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setSendFrom(String sendFrom) {
        this.sendFrom = sendFrom;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public void setSmsSender(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    public void setSmsTemplate(List<Map<String, String>> smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    /**
     * 保存预留的微信模板配置，不启用额外的消息发送渠道。
     * @param wxTemplate 微信模板配置列表
     */
    public void setWxTemplate(List<Map<String, String>> wxTemplate) {
        this.wxTemplate = wxTemplate;
    }
}
