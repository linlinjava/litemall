package org.linlinjava.litemall.wx.service;

import org.linlinjava.litemall.core.system.SystemInfoPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SystemInistService {
    private SystemInistService systemInistService;
    @Autowired
    private Environment environment;

    @PostConstruct
    public void inist() {
        systemInistService = this;

        SystemInfoPrinter.printInfo("WX-API 初始化信息", getSystemInfo());
    }

    private Map<String, String> getSystemInfo() {

        Map<String, String> infos = new LinkedHashMap<>();

        infos.put(SystemInfoPrinter.CREATE_PART_COPPER + 0, "系统信息");
        // 测试获取application-db.yml配置信息
        infos.put("数据库USER", environment.getProperty("spring.datasource.druid.username"));
        infos.put("数据库地址", environment.getProperty("spring.datasource.druid.url"));
        // 测试获取application.yml配置信息
        infos.put("调试级别", environment.getProperty("logging.level.org.linlinjava.litemall.wx"));

        infos.put(SystemInfoPrinter.CREATE_PART_COPPER + 1, "模块状态");
        infos.put("邮件", environment.getProperty("litemall.notify.mail.enable"));
        infos.put("短信", environment.getProperty("litemall.notify.sms.enable"));
        infos.put("模版消息", environment.getProperty("litemall.notify.wx.enable"));
        infos.put("快递信息", environment.getProperty("litemall.express.enable"));
        // 测试获取application-core.yml配置信息
        infos.put("快递鸟ID", environment.getProperty("litemall.express.appId"));
        infos.put("对象存储", environment.getProperty("litemall.storage.active"));
        infos.put("本地对象存储路径", environment.getProperty("litemall.storage.local.storagePath"));
        infos.put("本地对象访问地址", environment.getProperty("litemall.storage.local.address"));
        infos.put("本地对象访问端口", environment.getProperty("litemall.storage.local.port"));


        infos.put(SystemInfoPrinter.CREATE_PART_COPPER + 2, "微信相关");
        // 测试获取application-wx.yml配置信息
        infos.put("微信APP KEY", environment.getProperty("litemall.wx.app-id"));
        // 测试获取application-wx.yml配置信息
        infos.put("微信APP-SECRET", environment.getProperty("litemall.wx.app-secret"));
        // 测试获取application-wx.yml配置信息
        infos.put("微信支付MCH-ID", environment.getProperty("litemall.wx.mch-id"));
        // 测试获取application-wx.yml配置信息
        infos.put("微信支付MCH-KEY", environment.getProperty("litemall.wx.mch-key"));
        // 测试获取application-wx.yml配置信息
        infos.put("微信支付通知地址", environment.getProperty("litemall.wx.notify-url"));


        return infos;
    }
}
