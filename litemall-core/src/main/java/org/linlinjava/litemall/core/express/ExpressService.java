package org.linlinjava.litemall.core.express;

import org.linlinjava.litemall.core.express.config.ExpressProperties;
import org.linlinjava.litemall.core.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * 快鸟物流查询服务
 * <p>
 * 3831775044640 韵达快递(YD)
 */
public class ExpressService {
    //请求url
    private String ReqURL = "http://api.kdniao.cc/Ebusiness/EbusinessOrderHandle.aspx";

    private ExpressProperties properties;

    public ExpressProperties getProperties() {
        return properties;
    }

    public void setProperties(ExpressProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取物流供应商名
     *
     * @param vendooCode
     * @return
     */
    public String getVendorName(String vendooCode) {
        for (Map<String, String> item : properties.getVendors()) {
            if (item.get("code").equals(vendooCode))
                return item.get("name");
        }
        return null;
    }

    /**
     * Json方式 查询订单物流轨迹
     *
     * @throws Exception
     */
    public String getOrderTracesByJson(String expCode, String expNo) throws Exception {
        if(!properties.isEnable()){
            return null;
        }

        String requestData = "{'OrderCode':'','ShipperCode':'" + expCode + "','LogisticCode':'" + expNo + "'}";

        Map<String, String> params = new HashMap<String, String>();
        params.put("RequestData", URLEncoder.encode(requestData, "UTF-8"));
        params.put("EBusinessID", properties.getAppId());
        params.put("RequestType", "1002");
        String dataSign = encrypt(requestData, properties.getAppKey(), "UTF-8");
        params.put("DataSign", URLEncoder.encode(dataSign, "UTF-8"));
        params.put("DataType", "2");

        String result = HttpUtil.sendPost(ReqURL, params);

        //根据公司业务处理返回的信息......

        return result;
    }

    /**
     * MD5加密
     *
     * @param str     内容
     * @param charset 编码方式
     * @throws Exception
     */
    private String MD5(String str, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * Sign签名生成
     *
     * @param content  内容
     * @param keyValue Appkey
     * @param charset  编码方式
     * @return DataSign签名
     */
    private String encrypt(String content, String keyValue, String charset)  {
        if (keyValue != null) {
            content = content + keyValue;
        }
        byte[] src = new byte[0];
        try {
            src = MD5(content, charset).getBytes(charset);
            return Base64Utils.encodeToString(src);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
