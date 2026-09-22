package org.linlinjava.litemall.core.notify;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * 腾讯云短信发送器。
 *
 * <p>基于腾讯云短信 SDK（SmsSingleSender）实现 {@link SmsSender}，
 * 支持直发与按模板发送；发送失败时返回 successful=false 并记录日志。
 */
public class TencentSmsSender implements SmsSender {
    /** 日志 */
    private final Log logger = LogFactory.getLog(TencentSmsSender.class);

    /** 腾讯云短信发送器（由配置注入） */
    private SmsSingleSender sender;
    /** 短信签名 */
    private String sign;

    //未使用
    public SmsSingleSender getSender() {
        return sender;
    }

    public void setSender(SmsSingleSender sender) {
        this.sender = sender;
    }

    /**
     * 直发短信（不指定模板）
     *
     * @param phone   接收通知的电话号码
     * @param content 短消息内容
     * @return 短信发送结果，失败时 successful=false
     */
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

    /**
     * 通过短信模板发送短信息
     *
     * @param phone      接收通知的电话号码
     * @param templateId 通知模板ID
     * @param params     模板参数，对应模板中 {1}、{2} 等占位符
     * @return 短信发送结果，失败时 successful=false
     */
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
