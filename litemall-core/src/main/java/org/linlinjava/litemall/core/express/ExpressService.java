package org.linlinjava.litemall.core.express;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.express.config.ExpressProperties;
import org.linlinjava.litemall.core.express.dao.ExpressInfo;
import org.linlinjava.litemall.core.util.HttpUtil;
import org.springframework.util.Base64Utils;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流查询服务
 * <p>
 * 快递鸟即时查询API http://www.kdniao.com/api-track
 */
public class ExpressService {

    private final Log logger = LogFactory.getLog(ExpressService.class);
    //请求url
    private String ReqURL = "http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";

    private ExpressProperties properties;

    public ExpressProperties getProperties() {
        return properties;
    }

    public void setProperties(ExpressProperties properties) {
        this.properties = properties;
    }

    /**
      * 根据供应商编码获取物流供应商名称。
     *
      * @param vendorCode 物流供应商编码
      * @return 供应商名称；未找到时返回 {@code null}
     */
    public String getVendorName(String vendorCode) {
        for (Map<String, String> item : properties.getVendors()) {
            if (item.get("code").equals(vendorCode))
                return item.get("name");
        }
        return null;
    }

    public List<Map<String, String>> getVendors() {
        return properties.getVendors();
    }

    /**
     * 查询指定物流单的物流信息。未启用物流查询或查询失败时返回 {@code null}。
     *
     * @param expCode 物流供应商编码
     * @param expNo 物流单号
     * @return 物流信息；未启用或查询失败时返回 {@code null}
     */
    public ExpressInfo getExpressInfo(String expCode, String expNo) {
        if (!properties.isEnable()) {
            return null;
        }

        try {
            String result = getOrderTracesByJson(expCode, expNo);
            ObjectMapper objMap = new ObjectMapper();
            ExpressInfo ei = objMap.readValue(result, ExpressInfo.class);
            ei.setShipperName(getVendorName(expCode));
            return ei;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
        * 以 JSON 格式请求物流轨迹。
     *
        * @param expCode 物流供应商编码
        * @param expNo 物流单号
        * @return 物流查询接口返回的 JSON
        * @throws Exception 请求编码或 HTTP 请求失败时抛出
     */
    private String getOrderTracesByJson(String expCode, String expNo) throws Exception {
        String requestData = "{'OrderCode':'','ShipperCode':'" + expCode + "','LogisticCode':'" + expNo + "'}";

        Map<String, String> params = new HashMap<String, String>();
        params.put("RequestData", URLEncoder.encode(requestData, "UTF-8"));
        params.put("EBusinessID", properties.getAppId());
        params.put("RequestType", "1002");
        String dataSign = encrypt(requestData, properties.getAppKey(), "UTF-8");
        params.put("DataSign", URLEncoder.encode(dataSign, "UTF-8"));
        params.put("DataType", "2");

        String result = HttpUtil.sendPost(ReqURL, params);

        return result;
    }

    /**
        * 使用指定字符集计算字符串的 MD5 摘要。
     *
        * @param str 待摘要的内容
        * @param charset 字符编码
        * @return 小写十六进制摘要
        * @throws Exception 无法获取 MD5 算法或字符编码时抛出
     */
    private String MD5(String str, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuilder sb = new StringBuilder(32);
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
    private String encrypt(String content, String keyValue, String charset) {
        if (keyValue != null) {
            content = content + keyValue;
        }
        byte[] src;
        try {
            src = MD5(content, charset).getBytes(charset);
            return Base64Utils.encodeToString(src);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }


}
