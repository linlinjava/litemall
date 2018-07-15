package org.linlinjava.litemall.core.notify;

public class NotifyUtils {
    /**
     * 该枚举定义了所有的需要通知的事件，调用通知时作为参数
     *
     * PAY_SUCCEED 支付成功，通常用于用户支付成功
     * CAPTCHA 验证码，通常用于登录、注册、找回密码
     */
    public enum NotifyType {
        PAY_SUCCEED,
        CAPTCHA
    }
}
