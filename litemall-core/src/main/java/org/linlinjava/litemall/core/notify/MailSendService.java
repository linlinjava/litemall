package org.linlinjava.litemall.core.notify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

@PropertySource(value = "classpath:notify.properties")
@Service("mailSendService")
class MailSendService {
    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.sendto}")
    private String sendto;

    /**
     * 异步发送邮件通知
     * @param setSubject 邮件标题
     * @param setText 邮件内容
     */
    @Async("nofityAsync")
    public void sendEmail(String setSubject, String setText) {
        try {
            final MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);

            message.setFrom(from);
            message.setTo(sendto);
            message.setSubject(setSubject);
            message.setText(setText);
            mailSender.send(mimeMessage);

        } catch (Exception ex) {

        }
    }
}
