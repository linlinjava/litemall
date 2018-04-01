package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.db.domain.LitemallComment;
import org.linlinjava.litemall.db.service.LitemallCommentService;
import org.linlinjava.litemall.db.service.LitemallCouponService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.UserInfoService;
import org.linlinjava.litemall.wx.dao.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/comment")
public class WxCommentController {
    @Autowired
    private LitemallCommentService commentService;
    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallCouponService couponService;
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 发表评论
     */
    @RequestMapping("post")
    public Object post(@LoginUser Integer userId, @RequestBody LitemallComment comment) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(comment == null){
            return ResponseUtil.fail402();
        }

        comment.setAddTime(LocalDate.now());
        comment.setUserId(userId);
        commentService.save(comment);
        return ResponseUtil.ok(comment);
    }

    /**
     */
    @RequestMapping("count")
    public Object count(Byte typeId, Integer valueId) {
        int allCount = commentService.count(typeId, valueId, 0, 0, 0);
        int hasPicCount = commentService.count(typeId, valueId, 1, 0, 0);
        Map<String, Object> data = new HashMap();
        data.put("allCount", allCount);
        data.put("hasPicCount", hasPicCount);
        return ResponseUtil.ok(data);
    }

    /**
     * @param typeId
     * @param valueId
     * @param showType 选择评论的类型 0 全部， 1 只显示图片
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("list")
    public Object list(Byte typeId, Integer valueId, Integer showType,
                       @RequestParam(value = "page", defaultValue = "0") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if(typeId == null || valueId == null || showType == null){
            return ResponseUtil.fail401();
        }

        List<LitemallComment> commentList = commentService.query(typeId, valueId, showType, page, size);
        int count = commentService.count(typeId, valueId, showType, page, size);

        List<Map<String, Object>> commentVoList = new ArrayList<>(commentList.size());
        for(LitemallComment comment : commentList){
            Map<String, Object> commentVo = new HashMap<>();
            UserInfo userInfo = userInfoService.getInfo(comment.getUserId());
            commentVo.put("userInfo", userInfo);
            commentVo.put("addTime", comment.getAddTime());
            commentVo.put("content",comment.getContent());
            commentVo.put("picList", comment.getPicUrls());

            commentVoList.add(commentVo);
        }
        Map<String, Object> data = new HashMap();
        data.put("data", commentVoList);
        data.put("currentPage", page);
        return ResponseUtil.ok(data);
    }
}