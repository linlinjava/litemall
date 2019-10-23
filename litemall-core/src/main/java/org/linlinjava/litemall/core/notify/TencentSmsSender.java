package org.linlinjava.litemall.core.notify;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/*
 * 腾讯云短信服务
 */
public class TencentSmsSender implements SmsSender {
    private final Log logger = LogFactory.getLog(TencentSmsSender.class);

    private SmsSingleSender sender;
    private String sign;

    public SmsSingleSender getSender() {
        return sender;
    }

    public void setSender(SmsSingleSender sender) {
        this.sender = sender;
    }

    @Override
    public SmsResult send(String phone, String content) {
        try {
            SmsSingleSenderResult result = sender.send(0, "86", phone, content, "", "");
            logger.debug(result);

            SmsResult smsResult = new SmsResult();
            smsResult.setSuccessful(true);
            smsResult.setResult(result);
            return smsResult;
        } catch (HTTPException | IOException e) {
            logger.error(e.getMessage(), e);
        }

        SmsResult smsResult = new SmsResult();
        smsResult.setSuccessful(false);
        return smsResult;
    }

    @Override
    public SmsResult sendWithTemplate(String phone, String templateId, String[] params) {
        try {
            SmsSingleSenderResult result = sender.sendWithParam("86", phone, Integer.parseInt(templateId), params, this.sign, "", "");
            logger.debug(result);

            SmsResult smsResult = new SmsResult();
            smsResult.setSuccessful(true);
            smsResult.setResult(result);
            return smsResult;
        } catch (HTTPException | IOException e) {
            logger.error(e.getMessage(), e);
        }

        SmsResult smsResult = new SmsResult();
        smsResult.setSuccessful(false);
        return smsResult;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
