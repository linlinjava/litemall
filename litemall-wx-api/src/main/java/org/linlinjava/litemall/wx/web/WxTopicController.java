package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.db.domain.LitemallTopic;
import org.linlinjava.litemall.db.service.LitemallTopicService;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/topic")
public class WxTopicController {
    @Autowired
    private LitemallTopicService topicService;

    /**
     * 专题列表
     */
    @RequestMapping("list")
    public Object list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<LitemallTopic> topicList = topicService.queryList(page, size);
        int total = topicService.queryTotal();
        Map<String, Object> data = new HashMap();
        data.put("data", topicList);
        data.put("count", total);
        return ResponseUtil.ok(data);
    }

    /**
     * 专题详情
     */
    @RequestMapping("detail")
    public Object detail(Integer id) {
        if(id == null){
            return ResponseUtil.fail402();
        }

        LitemallTopic topic = topicService.findById(id);
        return ResponseUtil.ok(topic);
    }

    /**
     * 相关专题
     */
    @RequestMapping("related")
    public Object related(Integer id) {
        if(id == null){
            return ResponseUtil.fail402();
        }

        List<LitemallTopic> topicRelatedList = topicService.queryRelatedList(id, 0, 4);
        return ResponseUtil.ok(topicRelatedList);
    }
}