package org.linlinjava.litemall.core.notify;

import org.json.JSONObject;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 微信模版消息通知，未完成
 */
@PropertySource(value = "classpath:notify.properties")
@Service("wxTemplateMsgSendService")
public class WXTemplateMsgSendService {
    /**
     * 发送微信消息(模板消息)
     *
     * @param touser    用户 OpenID
     * @param templatId 模板消息ID
     * @param formId    payId或者表单ID
     * @param clickurl  URL置空，则在发送后，点击模板消息会进入一个空白页面（ios），或无法点击（android）。
     * @param topcolor  标题颜色
     * @param data      详细内容
     * @return
     */
    public String sendWechatMsgToUser(String token, String touser, String templatId, String formId, String clickurl, String topcolor, JSONObject data) {
        String tmpurl = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + token;
        JSONObject json = new JSONObject();
        json.put("touser", touser);
        json.put("template_id", templatId);
        json.put("form_id", formId);
        json.put("url", clickurl);
        json.put("topcolor", topcolor);
        json.put("data", data);
        try {
            JSONObject result = httpsRequest(tmpurl, "POST", json.toString());
//            log.info("发送微信消息返回信息：" + resultJson.get("errcode"));
            String errmsg = (String) result.get("errmsg");
            if (!"ok".equals(errmsg)) {  //如果为errmsg为ok，则代表发送成功，公众号推送信息给用户了。
                return "error";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    /**
     * 发送https请求
     *
     * @param requestUrl    请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr     提交的数据
     * @return JSONObject(通过JSONObject.get ( key)的方式获取json对象的属性值)
     */
    private JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            jsonObject = new JSONObject(buffer.toString());
        } catch (ConnectException ce) {
//            log.error("连接超时：{}", ce);
        } catch (Exception e) {
//            log.error("https请求异常：{}", e);
        }
        return jsonObject;
    }

    /**
     * 微信请求 - 信任管理器
     */
    private class MyX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            //        return new X509Certificate[0];
            return null;
        }
    }
}
