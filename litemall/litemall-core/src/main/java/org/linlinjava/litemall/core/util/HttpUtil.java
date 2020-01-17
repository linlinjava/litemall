package org.linlinjava.litemall.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 向指定 URL 发送POST方法的请求
 *
 * @return 远程资源的响应结果
 */
public class HttpUtil {

    private static final Log logger = LogFactory.getLog(HttpUtil.class);

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url    发送请求的 URL
     * @param params 请求的参数集合
     * @return 远程资源的响应结果
     */
    @SuppressWarnings("unused")
    public static String sendPost(String url, Map<String, String> params) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            if (params != null) {
                StringBuilder param = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (param.length() > 0) {
                        param.append("&");
                    }
                    param.append(entry.getKey());
                    param.append("=");
                    param.append(entry.getValue());

                }

                out.write(param.toString());
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return result.toString();
    }
}
