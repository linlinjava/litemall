package org.linlinjava.litemall.core.notify;

/**
 * 短信发送器接口。
 *
 * <p>定义向指定手机号发送短信（直发或按模板发送）的能力，
 * 由具体短信渠道实现，如腾讯云 {@link TencentSmsSender}、阿里云 {@link AliyunSmsSender}。
 */
public interface SmsSender {

    /**
     * 发送短信息
     *
     * @param phone   接收通知的电话号码
     * @param content 短消息内容
     * @return 短信发送结果
     */
    SmsResult send(String phone, String content);


    /**
     * 通过短信模版发送短信息
     *
     * @param phone      接收通知的电话号码
     * @param templateId 通知模板ID
     * @param params     通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     * @return 短信发送结果
     */
    SmsResult sendWithTemplate(String phone, String templateId, String[] params);
}
