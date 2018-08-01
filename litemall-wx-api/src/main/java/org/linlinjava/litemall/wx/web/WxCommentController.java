package org.linlinjava.litemall.wx.web;

import org.apache.commons.lang3.ObjectUtils;
import org.linlinjava.litemall.db.domain.LitemallComment;
import org.linlinjava.litemall.db.service.LitemallCommentService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.UserInfoService;
import org.linlinjava.litemall.wx.dao.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/comment")
@Validated
public class WxCommentController {
    @Autowired
    private LitemallCommentService commentService;
    @Autowired
    private LitemallUserService userService;
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 发表评论
     *
     * TODO, 对于评论，应该检测用户是否有权限评论。
     * 1. 如果用户没有购买过商品，则不能发表对该商品的评论
     * 2. 如果用户购买商品后规定时间内没有评论，则过期也不能再评论
     *
     * @param userId 用户ID
     * @param comment 评论内容
     * @return 发表评论操作结果
     *   成功则
     *  {
     *      errno: 0,
     *      errmsg: '成功',
     *      data: xxx
     *  }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("post")
    public Object post(@LoginUser Integer userId, @RequestBody LitemallComment comment) {
        if(userId == null){
            return ResponseUtil.unlogin();
        }
        if(comment == null){
            return ResponseUtil.badArgument();
        }

        comment.setAddTime(LocalDateTime.now());
        comment.setUserId(userId);
        commentService.save(comment);
        return ResponseUtil.ok(comment);
    }

    /**
     * 评论数量
     *
     * @param type 类型ID。 如果是0，则查询商品评论；如果是1，则查询专题评论。
     * @param valueId 商品或专题ID。如果type是0，则是商品ID；如果type是1，则是专题ID。
     * @return 评论数量
     *   成功则
     *  {
     *      errno: 0,
     *      errmsg: '成功',
     *      data:
     *          {
     *              allCount: xxx,
     *              hasPicCount: xxx
     *          }
     *  }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("count")
    public Object count(@NotNull Byte type, @NotNull Integer valueId) {
        int allCount = commentService.count(type, valueId, 0, 0, 0);
        int hasPicCount = commentService.count(type, valueId, 1, 0, 0);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("allCount", allCount);
        data.put("hasPicCount", hasPicCount);
        return ResponseUtil.ok(data);
    }

    /**
     * 评论列表
     *
     * @param type 类型ID。 如果是0，则查询商品评论；如果是1，则查询专题评论。
     * @param valueId 商品或专题ID。如果type是0，则是商品ID；如果type是1，则是专题ID。
     * @param showType 显示类型。如果是0，则查询全部；如果是1，则查询有图片的评论。
     * @param page 分页页数
     * @param size 分页大小
     * @return 评论列表
     *   成功则
     *  {
     *      errno: 0,
     *      errmsg: '成功',
     *      data:
     *          {
     *              data: xxx,
     *              count: xxx，
     *              currentPage: xxx
     *          }
     *  }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("list")
    public Object list(@NotNull Byte type,
                       @NotNull Integer valueId,
                       @NotNull Integer showType,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size) {
        List<LitemallComment> commentList = commentService.query(type, valueId, showType, page, size);
        int count = commentService.count(type, valueId, showType, page, size);

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
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("data", commentVoList);
        data.put("count", count);
        data.put("currentPage", page);
        return ResponseUtil.ok(data);
    }
}