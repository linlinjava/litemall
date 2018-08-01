package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallTopic;
import org.linlinjava.litemall.db.service.LitemallTopicService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String title, String subtitle,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        List<LitemallTopic> topicList = topicService.querySelective(title, subtitle, page, limit, sort, order);
        int total = topicService.countSelective(title, subtitle, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", topicList);

        return ResponseUtil.ok(data);
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallTopic topic){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        topic.setAddTime(LocalDateTime.now());
        topicService.add(topic);
        return ResponseUtil.ok(topic);
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, @NotNull Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        LitemallTopic brand = topicService.findById(id);
        return ResponseUtil.ok(brand);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallTopic topic){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        topicService.updateById(topic);
        return ResponseUtil.ok(topic);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallTopic topic){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        topicService.deleteById(topic.getId());
        return ResponseUtil.ok();
    }

}
