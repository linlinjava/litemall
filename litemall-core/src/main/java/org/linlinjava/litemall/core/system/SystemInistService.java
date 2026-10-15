package org.linlinjava.litemall.core.system;

import org.linlinjava.litemall.core.util.SystemInfoPrinter;
import org.linlinjava.litemall.db.service.LitemallSystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 系统启动初始化服务
 *
 * <p>在 Spring 容器启动完成后（{@link PostConstruct}）自动执行：</p>
 * <ol>
 *     <li>从数据库加载全部系统配置；若某配置项缺失则用 DEFAULT_CONFIGS 兜底并回写数据库；</li>
 *     <li>把配置整体载入 {@link SystemConfig} 的内存缓存；</li>
 *     <li>在控制台打印一份系统信息横幅（端口、数据库、各模块开关、微信配置等），便于部署后核对。</li>
 * </ol>
 *
 * <p>注：类名 Inist 为原项目历史拼写，未改动以免影响引用。</p>
 *
 * @author litemall
 * @since 1.0
 */
@Component
class SystemInistService {
    // 自注入引用，供内部静态/非静态方法间相互调用（保留原项目写法）
    private SystemInistService systemInistService;

    @Autowired
    private Environment environment;

    /** 容器初始化入口：先保存自身引用，再载入配置、打印启动横幅 */
    @PostConstruct
    private void inist() {
        systemInistService = this;
        initConfigs();
        SystemInfoPrinter.printInfo("Litemall 初始化信息", getSystemInfo());
    }

    /** 各项系统配置的默认值：数据库没有时以此为准 */
    private final static Map<String, String> DEFAULT_CONFIGS = new HashMap<>();

    static {
        // 小程序首页各区块默认展示条数
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_WX_INDEX_NEW, "6");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_WX_INDEX_HOT, "6");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_WX_INDEX_BRAND, "4");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_WX_INDEX_TOPIC, "4");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_WX_INDEX_CATLOG_LIST, "4");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_WX_INDEX_CATLOG_GOODS, "4");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_WX_SHARE, "false");
        // 运费：默认 8 元，满 88 元免邮
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_EXPRESS_FREIGHT_VALUE, "8");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_EXPRESS_FREIGHT_MIN, "88");
        // 订单：未支付 30 分钟取消，未确认 7 天自动确认，7 天自动好评
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_ORDER_UNPAID, "30");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_ORDER_UNCONFIRM, "7");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_ORDER_COMMENT, "7");
        // 商城信息默认值
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_MALL_NAME, "litemall");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_MALL_ADDRESS, "上海");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_MALL_Latitude, "31.201900");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_MALL_LONGITUDE, "121.587839");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_MALL_PHONE, "021-xxxx-xxxx");
        DEFAULT_CONFIGS.put(SystemConfig.LITEMALL_MALL_QQ, "705144434");
    }

    @Autowired
    private LitemallSystemConfigService litemallSystemConfigService;

    /** 读取数据库配置，缺项用默认值补齐并回写，最后载入内存缓存 */
    private void initConfigs() {
        // 1. 读取数据库全部配置信息
        Map<String, String> configs = litemallSystemConfigService.queryAll();

        // 2. 对默认配置中存在、但数据库缺失的项，补一条默认值并落库
        for (Map.Entry<String, String> entry : DEFAULT_CONFIGS.entrySet()) {
            if (configs.containsKey(entry.getKey())) {
                continue;
            }
            configs.put(entry.getKey(), entry.getValue());
            litemallSystemConfigService.addConfig(entry.getKey(), entry.getValue());
        }

        SystemConfig.setConfigs(configs);
    }

    /** 汇总启动时要打印的系统信息（端口、数据源、模块开关、微信配置、系统设置） */
    private Map<String, String> getSystemInfo() {
        Map<String, String> infos = new LinkedHashMap<>();

        infos.put(SystemInfoPrinter.CREATE_PART_COPPER + 0, "系统信息");
        infos.put("服务器端口", environment.getProperty("server.port"));
        infos.put("数据库USER", environment.getProperty("spring.datasource.druid.username"));
        infos.put("数据库地址", environment.getProperty("spring.datasource.druid.url"));
        infos.put("调试级别", environment.getProperty("logging.level.org.linlinjava.litemall.wx"));

        infos.put(SystemInfoPrinter.CREATE_PART_COPPER + 1, "模块状态");
        infos.put("邮件", environment.getProperty("litemall.notify.mail.enable"));
        infos.put("短信", environment.getProperty("litemall.notify.sms.enable"));
        infos.put("模版消息", environment.getProperty("litemall.notify.wx.enable"));
        infos.put("快递信息", environment.getProperty("litemall.express.enable"));
        infos.put("快递鸟ID", environment.getProperty("litemall.express.appId"));
        infos.put("对象存储", environment.getProperty("litemall.storage.active"));
        infos.put("本地对象存储路径", environment.getProperty("litemall.storage.local.storagePath"));
        infos.put("本地对象访问地址", environment.getProperty("litemall.storage.local.address"));
        infos.put("本地对象访问端口", environment.getProperty("litemall.storage.local.port"));

        infos.put(SystemInfoPrinter.CREATE_PART_COPPER + 2, "微信相关");
        infos.put("微信APP KEY", environment.getProperty("litemall.wx.app-id"));
        infos.put("微信APP-SECRET", environment.getProperty("litemall.wx.app-secret"));
        infos.put("微信支付MCH-ID", environment.getProperty("litemall.wx.mch-id"));
        infos.put("微信支付MCH-KEY", environment.getProperty("litemall.wx.mch-key"));
        infos.put("微信支付通知地址", environment.getProperty("litemall.wx.notify-url"));

        infos.put(SystemInfoPrinter.CREATE_PART_COPPER + 3, "系统设置");
        infos.put("自动创建朋友圈分享图", Boolean.toString(SystemConfig.isAutoCreateShareImage()));
        infos.put("商场名称", SystemConfig.getMallName());
        infos.put("首页显示记录数：NEW,HOT,BRAND,TOPIC,CatlogList,CatlogMore",
                SystemConfig.getNewLimit() + "," + SystemConfig.getHotLimit() + "," + SystemConfig.getBrandLimit() +
                        "," + SystemConfig.getTopicLimit() + "," + SystemConfig.getCatlogListLimit() + "," + SystemConfig.getCatlogMoreLimit());

        return infos;
    }
}
