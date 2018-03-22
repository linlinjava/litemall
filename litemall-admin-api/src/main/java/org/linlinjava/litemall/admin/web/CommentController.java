package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.db.domain.LitemallComment;
import org.linlinjava.litemall.db.service.LitemallCommentService;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/comment")
public class CommentController {
    private final Log logger = LogFactory.getLog(CommentController.class);

    @Autowired
    private LitemallCommentService commentService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String userId, String valueId,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        List<LitemallComment> brandList = commentService.querySelective(userId, valueId, page, limit, sort, order);
        int total = commentService.countSelective(userId, valueId, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", brandList);

        return ResponseUtil.ok(data);
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallComment comment){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        commentService.add(comment);
        return ResponseUtil.ok(comment);
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        if(id == null){
            return ResponseUtil.badArgument();
        }

        LitemallComment comment = commentService.findById(id);
        return ResponseUtil.ok(comment);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallComment comment){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        commentService.updateById(comment);
        return ResponseUtil.ok(comment);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallComment comment){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        commentService.deleteById(comment.getId());
        return ResponseUtil.ok();
    }

}
