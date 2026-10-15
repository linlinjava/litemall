package org.linlinjava.litemall.core.system;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统运行配置（内存缓存）
 *
 * <p>后台“系统配置”页里的各项参数（首页展示条数、运费规则、订单超时时间、商场名称地址等）
 * 都保存在数据库 litemall_system 表中。本类把它们加载到一个静态 Map 里做内存缓存，
 * 业务代码通过静态 getter 直接读取，避免每次查库；后台修改配置后调用 updateConfigs 刷新缓存。</p>
 *
 * @author litemall
 * @since 1.0
 */
public class SystemConfig {
    // —— 小程序首页展示相关配置 key ——
    public final static String LITEMALL_WX_INDEX_NEW = "litemall_wx_index_new";
    public final static String LITEMALL_WX_INDEX_HOT = "litemall_wx_index_hot";
    public final static String LITEMALL_WX_INDEX_BRAND = "litemall_wx_index_brand";
    public final static String LITEMALL_WX_INDEX_TOPIC = "litemall_wx_index_topic";
    public final static String LITEMALL_WX_INDEX_CATLOG_LIST = "litemall_wx_catlog_list";
    public final static String LITEMALL_WX_INDEX_CATLOG_GOODS = "litemall_wx_catlog_goods";
    public final static String LITEMALL_WX_SHARE = "litemall_wx_share";
    // —— 运费相关配置 key ——
    public final static String LITEMALL_EXPRESS_FREIGHT_VALUE = "litemall_express_freight_value";
    public final static String LITEMALL_EXPRESS_FREIGHT_MIN = "litemall_express_freight_min";
    // —— 订单相关配置 key（超时时间，单位分钟/天）——
    public final static String LITEMALL_ORDER_UNPAID = "litemall_order_unpaid";
    public final static String LITEMALL_ORDER_UNCONFIRM = "litemall_order_unconfirm";
    public final static String LITEMALL_ORDER_COMMENT = "litemall_order_comment";
    // —— 商场信息相关配置 key ——
    public final static String LITEMALL_MALL_NAME = "litemall_mall_name";
    public final static String LITEMALL_MALL_ADDRESS = "litemall_mall_address";
    public final static String LITEMALL_MALL_PHONE = "litemall_mall_phone";
    public final static String LITEMALL_MALL_QQ = "litemall_mall_qq";
    public final static String LITEMALL_MALL_LONGITUDE = "litemall_mall_longitude";
    public final static String LITEMALL_MALL_Latitude = "litemall_mall_latitude";

    /** 全部系统配置的内存缓存，key 为配置名，value 为配置值 */
    private static Map<String, String> SYSTEM_CONFIGS = new HashMap<>();

    private static String getConfig(String keyName) {
        return SYSTEM_CONFIGS.get(keyName);
    }

    private static Integer getConfigInt(String keyName) {
        return Integer.parseInt(SYSTEM_CONFIGS.get(keyName));
    }

    private static Boolean getConfigBoolean(String keyName) {
        return Boolean.valueOf(SYSTEM_CONFIGS.get(keyName));
    }

    private static BigDecimal getConfigBigDec(String keyName) {
        return new BigDecimal(SYSTEM_CONFIGS.get(keyName));
    }

    /** 首页“新品推荐”展示条数 */
    public static Integer getNewLimit() {
        return getConfigInt(LITEMALL_WX_INDEX_NEW);
    }

    /** 首页“热卖”展示条数 */
    public static Integer getHotLimit() {
        return getConfigInt(LITEMALL_WX_INDEX_HOT);
    }

    /** 首页“品牌”展示条数 */
    public static Integer getBrandLimit() {
        return getConfigInt(LITEMALL_WX_INDEX_BRAND);
    }

    /** 首页“专题”展示条数 */
    public static Integer getTopicLimit() {
        return getConfigInt(LITEMALL_WX_INDEX_TOPIC);
    }

    /** 分类页一级分类展示条数 */
    public static Integer getCatlogListLimit() {
        return getConfigInt(LITEMALL_WX_INDEX_CATLOG_LIST);
    }

    /** 分类下商品展示条数 */
    public static Integer getCatlogMoreLimit() {
        return getConfigInt(LITEMALL_WX_INDEX_CATLOG_GOODS);
    }

    /** 是否自动生成朋友圈分享图 */
    public static boolean isAutoCreateShareImage() {
        return getConfigBoolean(LITEMALL_WX_SHARE);
    }

    /** 运费金额（元） */
    public static BigDecimal getFreight() {
        return getConfigBigDec(LITEMALL_EXPRESS_FREIGHT_VALUE);
    }

    /** 免邮门槛（元） */
    public static BigDecimal getFreightLimit() {
        return getConfigBigDec(LITEMALL_EXPRESS_FREIGHT_MIN);
    }

    /** 未支付订单自动取消时长 */
    public static Integer getOrderUnpaid() {
        return getConfigInt(LITEMALL_ORDER_UNPAID);
    }

    /** 未确认收货自动确认时长 */
    public static Integer getOrderUnconfirm() {
        return getConfigInt(LITEMALL_ORDER_UNCONFIRM);
    }

    /** 订单自动好评时长 */
    public static Integer getOrderComment() {
        return getConfigInt(LITEMALL_ORDER_COMMENT);
    }

    public static String getMallName() {
        return getConfig(LITEMALL_MALL_NAME);
    }

    public static String getMallAddress() {
        return getConfig(LITEMALL_MALL_ADDRESS);
    }

    public static String getMallPhone() {
        return getConfig(LITEMALL_MALL_PHONE);
    }

    public static String getMallQQ() {
        return getConfig(LITEMALL_MALL_QQ);
    }

    public static String getMallLongitude() {
        return getConfig(LITEMALL_MALL_LONGITUDE);
    }

    public static String getMallLatitude() {
        return getConfig(LITEMALL_MALL_Latitude);
    }

    /** 启动时整体载入数据库配置到内存缓存 */
    public static void setConfigs(Map<String, String> configs) {
        SYSTEM_CONFIGS = configs;
    }

    /** 后台修改配置后，增量更新内存缓存 */
    public static void updateConfigs(Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            SYSTEM_CONFIGS.put(entry.getKey(), entry.getValue());
        }
    }
}
