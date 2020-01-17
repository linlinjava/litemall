package org.linlinjava.litemall.core.notify;

/**
 * 发送短信的返回结果
 */
public class SmsResult {
    private boolean successful;
    private Object result;

    /**
     * 短信是否发送成功
     *
     * @return
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
