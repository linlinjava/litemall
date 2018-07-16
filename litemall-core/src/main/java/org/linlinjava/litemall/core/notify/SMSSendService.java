package org.linlinjava.litemall.core.notify;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;
import org.linlinjava.litemall.core.notify.config.SMSNotifyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service("smsSendService")
class SMSSendService {
    @Autowired
    SMSNotifyConfig config;

    public void sendSMS(String phoneNumber, String content) {
        try {
            SmsSingleSender ssender = new SmsSingleSender(config.getAppid(), config.getAppkey());
            SmsSingleSenderResult result = ssender.send(0, "86", phoneNumber,
                    content, "", "");

//            System.out.println(result);
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
    }

    /**
     * 通过模版发送短信息
     * @param phoneNumber
     * @param templateId
     * @param params
     */
    public void sendSMSWithTemplate(String phoneNumber, int templateId, String[] params) {
        try {
            SmsSingleSender ssender = new SmsSingleSender(config.getAppid(), config.getAppkey());
            SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumber,
                    templateId, params, config.getSign(), "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
//            System.out.println(result);
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
    }
}
