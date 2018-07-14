package org.linlinjava.litemall.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;

public class MailUtils {
    private static final Log logger = LogFactory.getLog(MailUtils.class);
    private static MailUtils mailUtils;

    private JavaMailSenderImpl mailSender;

    // TODO 邮箱相关后置后续应移到数据库，在后台配置完成，暂时先完成功能
    // 通知邮件送达地址
    private static final String SEND_TO = "ex@qq.com";

    private MailUtils() {
        mailSender = new JavaMailSenderImpl();
        // 配置发送邮箱设置，请按照自己邮箱填写
        mailSender.setHost("smtp.exmail.qq.com");
        mailSender.setUsername("ex@ex.com.cn");
        mailSender.setPassword("ex");
        mailSender.setDefaultEncoding("UTF-8");
    }

    public static MailUtils getMailUtils() {
        if (mailUtils == null)
            mailUtils = new MailUtils();

        return mailUtils;
    }

    public boolean sendEmail(String setSubject, String setText) {
        try {
            final MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom(mailSender.getUsername());
            message.setTo(SEND_TO);
            message.setSubject(setSubject);
            message.setText(setText);
            mailSender.send(mimeMessage);

            return true;
        } catch (Exception ex) {
            logger.error("通知邮件发送出错" + ex.getMessage());
            return false;
        }
    }
}
