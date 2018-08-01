package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.db.domain.LitemallTopic;
import org.linlinjava.litemall.db.service.LitemallTopicService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/topic")
@Validated
public class WxTopicController {
    @Autowired
    private LitemallTopicService topicService;

    /**
     * 专题列表
     *
     * @param page 分页页数
     * @param size 分页大小
     * @return 专题列表
     *   成功则
     *  {
     *      errno: 0,
     *      errmsg: '成功',
     *      data:
     *          {
     *              data: xxx,
     *              count: xxx
     *          }
     *  }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("list")
    public Object list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size) {
        List<LitemallTopic> topicList = topicService.queryList(page, size);
        int total = topicService.queryTotal();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("data", topicList);
        data.put("count", total);
        return ResponseUtil.ok(data);
    }

    /**
     * 专题详情
     *
     * @param id 专题ID
     * @return 专题详情
     *   成功则
     *  {
     *      errno: 0,
     *      errmsg: '成功',
     *      data: xxx
     *  }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("detail")
    public Object detail(@NotNull Integer id) {
        LitemallTopic topic = topicService.findById(id);
        return ResponseUtil.ok(topic);
    }

    /**
     * 相关专题
     *
     * @param id 专题ID
     * @return 相关专题
     *   成功则
     *  {
     *      errno: 0,
     *      errmsg: '成功',
     *      data: xxx
     *  }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("related")
    public Object related(@NotNull Integer id) {
        List<LitemallTopic> topicRelatedList = topicService.queryRelatedList(id, 0, 4);
        return ResponseUtil.ok(topicRelatedList);
    }
}