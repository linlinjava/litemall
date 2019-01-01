package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallBrand;
import org.linlinjava.litemall.db.service.LitemallBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/brand")
@Validated
public class AdminBrandController {
    private final Log logger = LogFactory.getLog(AdminBrandController.class);

    @Autowired
    private LitemallBrandService brandService;

    @RequiresPermissions("admin:brand:list")
    @GetMapping("/list")
    public Object list(String id, String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallBrand> brandList = brandService.querySelective(id, name, page, limit, sort, order);
        int total = brandService.countSelective(id, name, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", brandList);

        return ResponseUtil.ok(data);
    }

    private Object validate(LitemallBrand brand) {
        String name = brand.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }

        String desc = brand.getDesc();
        if (StringUtils.isEmpty(desc)) {
            return ResponseUtil.badArgument();
        }

        BigDecimal price = brand.getFloorPrice();
        if (price == null) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @RequiresPermissions("admin:brand:create")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallBrand brand) {
        Object error = validate(brand);
        if (error != null) {
            return error;
        }
        brandService.add(brand);
        return ResponseUtil.ok(brand);
    }

    @RequiresPermissions("admin:brand:read")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallBrand brand = brandService.findById(id);
        return ResponseUtil.ok(brand);
    }

    @RequiresPermissions("admin:brand:update")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallBrand brand) {
        Object error = validate(brand);
        if (error != null) {
            return error;
        }
        if (brandService.updateById(brand) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(brand);
    }

    @RequiresPermissions("admin:brand:delete")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallBrand brand) {
        Integer id = brand.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        brandService.deleteById(id);
        return ResponseUtil.ok();
    }

}
