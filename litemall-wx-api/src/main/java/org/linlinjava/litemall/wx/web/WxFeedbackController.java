package org.linlinjava.litemall.wx.web;

import com.alibaba.fastjson.JSONObject;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallFeedback;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallFeedbackService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;


/**
 * @author Yogeek
 * @date 2018/8/25 14:10
 */
@RestController
@RequestMapping("/wx/feedback")
@Validated
public class WxFeedbackController {
    private final Log logger = LogFactory.getLog(WxFeedbackController.class);

    @Autowired
    private LitemallFeedbackService feedbackService;
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    private LitemallUserService userService;

    /**
     * 意见反馈
     */
    @PostMapping("submit")
    @ResponseBody
    public Object save(@LoginUser Integer userId){
        if(userId == null){
            return ResponseUtil.unlogin();
        }

        LitemallUser user = userService.findById(userId);
        String username = user.getUsername();
        //获取客户端对象
        JSONObject feedbackJson = this.getJsonRequest();

        if (null != feedbackJson) {
            LitemallFeedback feedback = new LitemallFeedback();

            String mobile = feedbackJson.getString("mobile");
            // 测试手机号码是否正确
            if (!RegexUtil.isMobileExact(mobile)) {
                return ResponseUtil.badArgument();
            }
            String[] feedType = new String [] {"请选择反馈类型", "商品相关", "功能异常", "优化建议", "其他"};
            int index = feedbackJson.getInteger("index");
            String content = feedbackJson.getString("content");

            feedback.setUserId(userId);
            feedback.setUsername(username);
            feedback.setMobile(mobile);
            feedback.setAddTime(LocalDateTime.now());
            feedback.setFeedType(feedType[index]);
            //状态默认是0，1表示状态已发生变化
            feedback.setStatus(1);
            feedback.setContent(content);
            feedbackService.add(feedback);

            return ResponseUtil.ok("感谢您的反馈");
        }
        return ResponseUtil.badArgument();
    }

    private JSONObject getJsonRequest() {
        JSONObject result = null;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader();) {
            char[] buff = new char[1024];
            int len;
            while ((len = reader.read(buff)) != -1) {
                sb.append(buff, 0, len);
            }
            result = JSONObject.parseObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
