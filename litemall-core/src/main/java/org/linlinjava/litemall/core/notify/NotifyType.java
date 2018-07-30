package org.linlinjava.litemall.core.notify;

public enum NotifyType {
    PAY_SUCCEED("paySucceed"),
    SHIP("ship"),
    REFUND("refund"),
    CAPTCHA("captcha");

    NotifyType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return this.type;
    }
}
