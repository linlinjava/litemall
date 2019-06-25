package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.service.LitemallSystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/config")
@Validated
public class AdminConfigController {
    private final Log logger = LogFactory.getLog(AdminConfigController.class);

    @Autowired
    private LitemallSystemConfigService systemConfigService;

    @RequiresPermissions("admin:config:mall:list")
    @RequiresPermissionsDesc(menu = {"配置管理", "商场配置"}, button = "详情")
    @GetMapping("/mall")
    public Object listMall() {
        Map<String, String> data = systemConfigService.listMail();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:mall:updateConfigs")
    @RequiresPermissionsDesc(menu = {"配置管理", "商场配置"}, button = "编辑")
    @PostMapping("/mall")
    public Object updateMall(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemConfigService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:config:express:list")
    @RequiresPermissionsDesc(menu = {"配置管理", "运费配置"}, button = "详情")
    @GetMapping("/express")
    public Object listExpress() {
        Map<String, String> data = systemConfigService.listExpress();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:express:updateConfigs")
    @RequiresPermissionsDesc(menu = {"配置管理", "运费配置"}, button = "编辑")
    @PostMapping("/express")
    public Object updateExpress(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemConfigService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:config:order:list")
    @RequiresPermissionsDesc(menu = {"配置管理", "订单配置"}, button = "详情")
    @GetMapping("/order")
    public Object lisOrder() {
        Map<String, String> data = systemConfigService.listOrder();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:order:updateConfigs")
    @RequiresPermissionsDesc(menu = {"配置管理", "订单配置"}, button = "编辑")
    @PostMapping("/order")
    public Object updateOrder(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemConfigService.updateConfig(data);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:config:wx:list")
    @RequiresPermissionsDesc(menu = {"配置管理", "小程序配置"}, button = "详情")
    @GetMapping("/wx")
    public Object listWx() {
        Map<String, String> data = systemConfigService.listWx();
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:config:wx:updateConfigs")
    @RequiresPermissionsDesc(menu = {"配置管理", "小程序配置"}, button = "编辑")
    @PostMapping("/wx")
    public Object updateWx(@RequestBody String body) {
        Map<String, String> data = JacksonUtil.toMap(body);
        systemConfigService.updateConfig(data);
        SystemConfig.updateConfigs(data);
        return ResponseUtil.ok();
    }
}
