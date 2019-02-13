package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallKeyword;
import org.linlinjava.litemall.db.service.LitemallKeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/keyword")
@Validated
public class AdminKeywordController {
    private final Log logger = LogFactory.getLog(AdminKeywordController.class);

    @Autowired
    private LitemallKeywordService keywordService;

    @RequiresPermissions("admin:keyword:list")
    @RequiresPermissionsDesc(menu={"商场管理" , "关键词"}, button="查询")
    @GetMapping("/list")
    public Object list(String keyword, String url,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallKeyword> brandList = keywordService.querySelective(keyword, url, page, limit, sort, order);
        int total = keywordService.countSelective(keyword, url, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", brandList);

        return ResponseUtil.ok(data);
    }

    private Object validate(LitemallKeyword keywords) {
        String keyword = keywords.getKeyword();
        if (StringUtils.isEmpty(keyword)) {
            return ResponseUtil.badArgument();
        }
        String url = keywords.getUrl();
        if (StringUtils.isEmpty(url)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @RequiresPermissions("admin:keyword:create")
    @RequiresPermissionsDesc(menu={"商场管理" , "关键词"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallKeyword keywords) {
        Object error = validate(keywords);
        if (error != null) {
            return error;
        }
        keywordService.add(keywords);
        return ResponseUtil.ok(keywords);
    }

    @RequiresPermissions("admin:keyword:read")
    @RequiresPermissionsDesc(menu={"商场管理" , "关键词"}, button="详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallKeyword brand = keywordService.findById(id);
        return ResponseUtil.ok(brand);
    }

    @RequiresPermissions("admin:keyword:update")
    @RequiresPermissionsDesc(menu={"商场管理" , "关键词"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallKeyword keywords) {
        Object error = validate(keywords);
        if (error != null) {
            return error;
        }
        if (keywordService.updateById(keywords) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(keywords);
    }

    @RequiresPermissions("admin:keyword:delete")
    @RequiresPermissionsDesc(menu={"商场管理" , "关键词"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallKeyword keyword) {
        Integer id = keyword.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        keywordService.deleteById(id);
        return ResponseUtil.ok();
    }

}
