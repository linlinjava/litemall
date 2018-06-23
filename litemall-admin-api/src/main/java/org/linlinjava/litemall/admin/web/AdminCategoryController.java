package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.db.domain.LitemallCategory;
import org.linlinjava.litemall.db.service.LitemallCategoryService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/category")
public class AdminCategoryController {
    private final Log logger = LogFactory.getLog(AdminCategoryController.class);

    @Autowired
    private LitemallCategoryService categoryService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String id, String name,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        List<LitemallCategory> collectList = categoryService.querySelective(id, name, page, limit, sort, order);
        int total = categoryService.countSelective(id, name, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", collectList);

        return ResponseUtil.ok(data);
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallCategory category){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        category.setAddTime(LocalDateTime.now());
        categoryService.add(category);
        return ResponseUtil.ok();
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        if(id == null){
            return ResponseUtil.badArgument();
        }

        LitemallCategory category = categoryService.findById(id);
        return ResponseUtil.ok(category);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallCategory category){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        categoryService.updateById(category);
        return ResponseUtil.ok();
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallCategory category){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        categoryService.deleteById(category.getId());
        return ResponseUtil.ok();
    }

    @GetMapping("/l1")
    public Object catL1(@LoginAdmin Integer adminId) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        // 所有一级分类目录
        List<LitemallCategory> l1CatList = categoryService.queryL1();
        HashMap<Integer, String> data = new HashMap<>(l1CatList.size());
        for(LitemallCategory category : l1CatList){
            data.put(category.getId(), category.getName());
        }
        return ResponseUtil.ok(data);
    }

}
