package org.linlinjava.litemall.core.notify;

import org.linlinjava.litemall.core.notify.config.MailNotifyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service("mailSendService")
class MailSendService {
    @Autowired
    MailNotifyConfig config;

    private JavaMailSenderImpl mailSender;

    private JavaMailSenderImpl getMailSender() {
        if (mailSender == null) {
            mailSender = new JavaMailSenderImpl();
            mailSender.setHost(config.getHost());
            mailSender.setUsername(config.getUsername());
            mailSender.setPassword(config.getPassword());
        }

        return mailSender;
    }

    /**
     * 发送邮件通知
     *
     * @param setSubject 邮件标题
     * @param setText    邮件内容
     */
    public void sendEmail(String setSubject, String setText) {
        try {
            final MimeMessage mimeMessage = getMailSender().createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);

            message.setFrom(config.getUsername());
            message.setTo(config.getSendto());
            message.setSubject(setSubject);
            message.setText(setText);
            getMailSender().send(mimeMessage);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
