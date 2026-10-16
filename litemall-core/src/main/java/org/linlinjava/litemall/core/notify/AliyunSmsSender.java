package org.linlinjava.litemall.core.notify;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.qcloud.cos.utils.Jackson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.JacksonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信发送器。
 *
 * <p>基于阿里云短信 API（dysmsapi）实现 {@link SmsSender}。
 * 注意：阿里云仅支持模板发送（{@link #sendWithTemplate}），
 * 直发 {@link #send} 为桩实现，始终返回发送失败；如需直发请走模板发送或改用其他渠道。
 */
public class AliyunSmsSender implements SmsSender {
    /** 日志 */
    private final Log logger = LogFactory.getLog(AliyunSmsSender.class);

    /** 阿里云区域 ID，如 cn-hangzhou */
    private String regionId;
    /** 阿里云 AccessKey ID */
    private String accessKeyId;
    /** 阿里云 AccessKey Secret */
    private String accessKeySecret;
    /** 短信签名 */
    private String sign;

    /** 阿里云短信发送成功的返回码 */
    private final String okCode = "OK";

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    /**
     * 直发短信（桩实现）。
     *
     * <p>阿里云短信 API 不支持不指定模板的直发，此方法为占位实现，始终返回发送失败；
     * 实际发送请使用 {@link #sendWithTemplate}。
     *
     * @param phone   接收通知的电话号码
     * @param content 短消息内容
     * @return 固定返回 successful=false
     */
    @Override
    public SmsResult send(String phone, String content) {
        SmsResult smsResult = new SmsResult();
        smsResult.setSuccessful(false);
        return smsResult;
    }

    /**
     * 通过短信模板发送短信息。
     *
     * <p>阿里云模板参数约定：单参数用 code，多参数用 code/code1/code2...（见下方注释），
     * 开发者需保证申请的模板参数名与此约定一致。
     *
     * @param phone      接收通知的电话号码
     * @param templateId 通知模板ID
     * @param params     模板参数值
     * @return 短信发送结果，失败时 successful=false
     */
    @Override
    public SmsResult sendWithTemplate(String phone, String templateId, String[] params) {
        DefaultProfile profile = DefaultProfile.getProfile(this.regionId, this.accessKeyId, this.accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", this.regionId);
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", this.sign);
        request.putQueryParameter("TemplateCode", templateId);
        /*
          NOTE：阿里云短信和腾讯云短信这里存在不一致
          腾讯云短信模板参数是数组，因此短信模板形式如 “短信参数{1}， 短信参数{2}”
          阿里云短信模板参数是JSON，因此短信模板形式如“短信参数{param1}， 短信参数{param2}”
          为了保持统一，我们假定阿里云短信里面的参数是code，code1，code2...

          如果开发者在阿里云短信申请的模板参数是其他命名，请开发者自行调整这里的代码，或者直接写死。
         */
        String templateParam = "{}";
        if(params.length == 1){
            Map<String, String> data = new HashMap<>();
            data.put("code", params[0]);
            templateParam = JacksonUtil.toJson(data);
        }
        else if(params.length > 1){
            Map<String, String> data = new HashMap<>();
            data.put("code", params[0]);
            for(int i = 1; i < params.length; i++){
                data.put("code" + i, params[i]);
            }
            templateParam = JacksonUtil.toJson(data);
        }
        request.putQueryParameter("TemplateParam", templateParam);

        try {
            CommonResponse response = client.getCommonResponse(request);
            SmsResult smsResult = new SmsResult();
            smsResult.setResult(response);
            String code =  Jackson.jsonNodeOf(response.getData()).get("Code").asText();
            if (response.getHttpResponse().isSuccess() && okCode.equals(code) ){
                smsResult.setSuccessful(true);
            }else {
                smsResult.setSuccessful(false);
                logger.error("短信发送失败："+response.getData());
            }

            return smsResult;
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        SmsResult smsResult = new SmsResult();
        smsResult.setSuccessful(false);
        return smsResult;
    }
}
