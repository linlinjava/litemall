package org.linlinjava.litemall.core.notify;

/**
 * 商城通知类型枚举。
 *
 * <p>每种通知类型对应短信/微信模板配置中的一个名称（{@code type}），
 * {@link NotifyService} 根据该名称在模板列表中查找对应的模板 ID。
 */
public enum NotifyType {
    /** 支付成功通知 */
    PAY_SUCCEED("paySucceed"),
    /** 发货通知 */
    SHIP("ship"),
    /** 退款通知 */
    REFUND("refund"),
    /** 验证码通知（如短信验证码） */
    CAPTCHA("captcha");

    /** 模板配置中使用的类型标识 */
    private String type;

    NotifyType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
