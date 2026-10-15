package org.linlinjava.litemall.core.notify;

/**
 * 发送短信的返回结果。
 *
 * <p>封装短信是否发送成功，以及短信渠道方返回的原始结果对象。
 */
public class SmsResult {
    /** 短信是否发送成功（true 表示成功） */
    private boolean successful;
    /** 短信渠道方返回的原始结果对象 */
    private Object result;

    /**
     * 短信是否发送成功
     *
     * @return true 表示发送成功，false 表示发送失败
     */
    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
