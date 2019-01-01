package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallTopic;
import org.linlinjava.litemall.db.service.LitemallTopicService;
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
@RequestMapping("/admin/topic")
@Validated
public class AdminTopicController {
    private final Log logger = LogFactory.getLog(AdminTopicController.class);

    @Autowired
    private LitemallTopicService topicService;

    @RequiresPermissions("admin:topic:list")
    @GetMapping("/list")
    public Object list(String title, String subtitle,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallTopic> topicList = topicService.querySelective(title, subtitle, page, limit, sort, order);
        int total = topicService.countSelective(title, subtitle, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", topicList);

        return ResponseUtil.ok(data);
    }

    private Object validate(LitemallTopic topic) {
        String title = topic.getTitle();
        if (StringUtils.isEmpty(title)) {
            return ResponseUtil.badArgument();
        }
        String content = topic.getContent();
        if (StringUtils.isEmpty(content)) {
            return ResponseUtil.badArgument();
        }
        BigDecimal price = topic.getPrice();
        if (price == null) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @RequiresPermissions("admin:topic:create")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallTopic topic) {
        Object error = validate(topic);
        if (error != null) {
            return error;
        }
        topicService.add(topic);
        return ResponseUtil.ok(topic);
    }

    @RequiresPermissions("admin:topic:read")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallTopic topic = topicService.findById(id);
        return ResponseUtil.ok(topic);
    }

    @RequiresPermissions("admin:topic:update")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallTopic topic) {
        Object error = validate(topic);
        if (error != null) {
            return error;
        }
        if (topicService.updateById(topic) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(topic);
    }

    @RequiresPermissions("admin:topic:delete")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallTopic topic) {
        topicService.deleteById(topic.getId());
        return ResponseUtil.ok();
    }

}
