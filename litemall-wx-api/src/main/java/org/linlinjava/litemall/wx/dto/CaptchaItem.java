package org.linlinjava.litemall.wx.dto;

import java.time.LocalDateTime;

/**
 * 验证码实体类，用于缓存验证码发送
 */
public class CaptchaItem {
    private String phoneNumber;
    private String code;
    private LocalDateTime expireTime;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
}