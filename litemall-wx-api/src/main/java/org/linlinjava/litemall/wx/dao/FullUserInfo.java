package org.linlinjava.litemall.wx.dao;

public class FullUserInfo {
    private String encryptedData;
    private String errMsg;
    private String iv;
    private String rawData;
    private String signature;
    private UserInfo userInfo;

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public String getIv() {
        return iv;
    }

    public String getRawData() {
        return rawData;
    }

    public String getSignature() {
        return signature;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
