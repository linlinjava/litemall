package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallCoupon;
import org.linlinjava.litemall.db.domain.LitemallCouponUser;
import org.linlinjava.litemall.db.service.LitemallCouponService;
import org.linlinjava.litemall.db.service.LitemallCouponUserService;
import org.linlinjava.litemall.db.util.CouponConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/coupon")
@Validated
public class AdminCouponController {
    private final Log logger = LogFactory.getLog(AdminCouponController.class);

    @Autowired
    private LitemallCouponService couponService;
    @Autowired
    private LitemallCouponUserService couponUserService;

    @RequiresPermissions("admin:coupon:list")
    @RequiresPermissionsDesc(menu = {"推广管理", "优惠券管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String name, Short type, Short status,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallCoupon> couponList = couponService.querySelective(name, type, status, page, limit, sort, order);
        return ResponseUtil.okList(couponList);
    }

    @RequiresPermissions("admin:coupon:listuser")
    @RequiresPermissionsDesc(menu = {"推广管理", "优惠券管理"}, button = "查询用户")
    @GetMapping("/listuser")
    public Object listuser(Integer userId, Integer couponId, Short status,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer limit,
                           @Sort @RequestParam(defaultValue = "add_time") String sort,
                           @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallCouponUser> couponList = couponUserService.queryList(userId, couponId, status, page,
                limit, sort, order);
        return ResponseUtil.okList(couponList);
    }

    private Object validate(LitemallCoupon coupon) {
        String name = coupon.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @RequiresPermissions("admin:coupon:create")
    @RequiresPermissionsDesc(menu = {"推广管理", "优惠券管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallCoupon coupon) {
        Object error = validate(coupon);
        if (error != null) {
            return error;
        }

        // 如果是兑换码类型，则这里需要生存一个兑换码
        if (coupon.getType().equals(CouponConstant.TYPE_CODE)) {
            String code = couponService.generateCode();
            coupon.setCode(code);
        }

        couponService.add(coupon);
        return ResponseUtil.ok(coupon);
    }

    @RequiresPermissions("admin:coupon:read")
    @RequiresPermissionsDesc(menu = {"推广管理", "优惠券管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallCoupon coupon = couponService.findById(id);
        return ResponseUtil.ok(coupon);
    }

    @RequiresPermissions("admin:coupon:update")
    @RequiresPermissionsDesc(menu = {"推广管理", "优惠券管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallCoupon coupon) {
        Object error = validate(coupon);
        if (error != null) {
            return error;
        }
        if (couponService.updateById(coupon) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(coupon);
    }

    @RequiresPermissions("admin:coupon:delete")
    @RequiresPermissionsDesc(menu = {"推广管理", "优惠券管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallCoupon coupon) {
        couponService.deleteById(coupon.getId());
        return ResponseUtil.ok();
    }

}
