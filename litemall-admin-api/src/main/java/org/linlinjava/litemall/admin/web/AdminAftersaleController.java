package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallAftersale;
import org.linlinjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/aftersale")
@Validated
public class AdminAftersaleController {
    private final Log logger = LogFactory.getLog(AdminAftersaleController.class);

    @Autowired
    private LitemallAftersaleService aftersaleService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;

    @RequiresPermissions("admin:aftersale:list")
    @RequiresPermissionsDesc(menu = {"商城管理", "售后管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(Integer orderId, String aftersaleSn,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallAftersale> aftersaleList = aftersaleService.querySelective(orderId, aftersaleSn, page, limit, sort, order);
        return ResponseUtil.okList(aftersaleList);
    }


    @RequiresPermissions("admin:aftersale:delete")
    @RequiresPermissionsDesc(menu = {"商城管理", "售后管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallAftersale aftersale) {
        aftersaleService.deleteById(aftersale.getId());
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:aftersale:batch-delete")
    @RequiresPermissionsDesc(menu = {"商城管理", "售后管理"}, button = "批量删除")
    @PostMapping("/batch-delete")
    public Object batchDelete(@RequestBody String body) {
        List<Integer> ids = JacksonUtil.parseIntegerList(body, "ids");
        aftersaleService.deleteByIds(ids);
        return ResponseUtil.ok();
    }
}
