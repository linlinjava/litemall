package org.linlinjava.litemall.core.notify.util;

import java.util.List;
import java.util.Map;

public class ConfigUtil {

    /**
     * 通过枚举获取对应的 templateId，注意 application.yaml 里字段名必须一致
     *
     * @param notifyType
     * @param values
     * @return
     */
    public static String getTemplateId(NotifyType notifyType, List<Map<String, String>> values) {
        for (Map<String, String> item : values) {
            String notifyTypeStr = getNotifyType(notifyType);

            if (item.get("name").equals(notifyTypeStr))
                return item.get("templateId");
        }
        return "";
    }

    /**
     * 该处字符串对应 application.yaml 里 template.name 的值,请注意
     *
     * @param notifyType
     * @return
     */
    private static String getNotifyType(NotifyType notifyType) {
        switch (notifyType) {
            case PAY_SUCCEED:
                return "paySucceed";
            case CAPTCHA:
                return "captcha";
            case SHIP:
                return "ship";
            case REFUND:
                return "refund";
        }

        return "";
    }

    /**
     * 该枚举定义了所有的需要通知的事件，调用通知时作为参数
     * <p>
     * PAY_SUCCEED 支付成功，通常用于用户支付成功
     * CAPTCHA 验证码，通常用于登录、注册、找回密码
     */
    public enum NotifyType {
        PAY_SUCCEED,
        SHIP,
        REFUND,
        CAPTCHA
    }
}
