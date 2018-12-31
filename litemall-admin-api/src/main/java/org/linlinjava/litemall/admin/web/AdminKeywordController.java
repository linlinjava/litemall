package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
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

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String keyword, String url,
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

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallKeyword keywords) {
        Object error = validate(keywords);
        if (error != null) {
            return error;
        }
        keywordService.add(keywords);
        return ResponseUtil.ok(keywords);
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, @NotNull Integer id) {
        LitemallKeyword brand = keywordService.findById(id);
        return ResponseUtil.ok(brand);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallKeyword keywords) {
        Object error = validate(keywords);
        if (error != null) {
            return error;
        }
        if (keywordService.updateById(keywords) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(keywords);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallKeyword keyword) {
        Integer id = keyword.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        keywordService.deleteById(id);
        return ResponseUtil.ok();
    }

}
