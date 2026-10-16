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
 * 封装快递鸟即时查询，为订单详情提供物流轨迹及快递公司名称。
 * <p>关闭开关或请求、解析异常时返回 null；非空响应仍须检查 Success，
 * 不能把返回对象直接当作查询成功。
 */
public class ExpressService {

    private final Log logger = LogFactory.getLog(ExpressService.class);
    // 服务商即时查询入口；enable 为 false 时不会发送请求。
    private String ReqURL = "http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";

    private ExpressProperties properties;

    public ExpressProperties getProperties() {
        return properties;
    }

    public void setProperties(ExpressProperties properties) {
        this.properties = properties;
    }

    /**
     * 将订单中的快递公司编码转换为页面展示名称。
     * @param vendorCode 与 vendors 配置中 code 对应的公司编码
     * @return 配置中的公司名称；没有匹配项时返回 null
     */
    public String getVendorName(String vendorCode) {
        for (Map<String, String> item : properties.getVendors()) {
            if (item.get("code").equals(vendorCode))
                return item.get("name");
        }
        return null;
    }

    /**
     * 提供可选快递公司，供后台发货时选择。
     * @return 配置中的公司列表，每项包含 code 和 name；返回原列表而非副本
     */
    public List<Map<String, String>> getVendors() {
        return properties.getVendors();
    }

    /**
     * 查询物流轨迹并补充本地快递公司名称。
     * <p>关闭查询或发生异常时返回 null；上游业务失败仍可能返回 Success=false 的对象。
     * @param expCode 快递公司编码，应与上游协议及本地 vendors 配置一致
     * @param expNo 运单号，保留字符串以避免丢失前导零
     * @return 查询响应；未启用或请求、解析异常时返回 null
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
     * 按现有快递鸟即时查询协议提交运单查询。
     * <p>签名基于未 URL 编码的请求数据计算，再分别对请求数据和签名编码。
     * @param expCode 快递公司编码
     * @param expNo 运单号
     * @return 服务商返回的原始 JSON 字符串
     * @throws Exception 请求参数编码等处理发生异常时
     */
    private String getOrderTracesByJson(String expCode, String expNo) throws Exception {
        String requestData = "{'OrderCode':'','ShipperCode':'" + expCode + "','LogisticCode':'" + expNo + "'}";

        Map<String, String> params = new HashMap<String, String>();
        params.put("RequestData", URLEncoder.encode(requestData, "UTF-8"));
        params.put("EBusinessID", properties.getAppId());
        // 1002 为本实现采用的即时物流查询类型，2 表示 JSON 响应。
        params.put("RequestType", "1002");
        String dataSign = encrypt(requestData, properties.getAppKey(), "UTF-8");
        params.put("DataSign", URLEncoder.encode(dataSign, "UTF-8"));
        params.put("DataType", "2");

        String result = HttpUtil.sendPost(ReqURL, params);


        return result;
    }

    /**
     * 计算协议签名需要的小写十六进制 MD5 摘要，不用于密码存储或内容加密。
     * @param str 待摘要的请求数据与密钥拼接结果
     * @param charset 将字符串转换为字节时使用的字符集
     * @return 32 位小写十六进制摘要
     * @throws Exception 算法不可用或字符集不受支持时
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
     * 将请求数据与 AppKey 拼接后的 MD5 十六进制字符串进行 Base64 编码。
     * <p>这里编码的是摘要字符串的字节，不是 MD5 原始摘要字节；需保持协议顺序。
     * @param content 未经 URL 编码的请求内容
     * @param keyValue 服务商 AppKey；为 null 时当前实现不拼接密钥
     * @param charset 摘要及字符串转换使用的字符集
     * @return 未 URL 编码的签名；计算异常时返回 null
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
