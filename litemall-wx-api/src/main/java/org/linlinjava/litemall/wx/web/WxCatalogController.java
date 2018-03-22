package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.db.domain.LitemallCategory;
import org.linlinjava.litemall.db.service.LitemallCategoryService;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/catalog")
public class WxCatalogController {
    @Autowired
    private LitemallCategoryService categoryService;

    /**
     * 获取分类栏目数据
     * 参数id是可选的，如果没有设置，则选择第一个一级目录
     * 这里的id应该是一级目录的id。
     */
    @RequestMapping("index")
    public Object index(Integer id,
                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "size", defaultValue = "10") Integer size) {

        // 所有一级分类目录
        List<LitemallCategory> l1CatList = categoryService.queryL1();

        // 当前一级分类目录
        LitemallCategory currentCategory = null;
        if(id != null){
            currentCategory = categoryService.findById(id);
        }
        else{
            currentCategory = l1CatList.get(0);
        }

        // 当前一级分类目录对应的二级分类目录
        List<LitemallCategory> currentSubCategory = null;
        if (null != currentCategory) {
            currentSubCategory = categoryService.queryByPid(currentCategory.getId());
        }

        Map<String, Object> data = new HashMap();
        data.put("categoryList", l1CatList);
        data.put("currentCategory", currentCategory);
        data.put("currentSubCategory", currentSubCategory);
        return ResponseUtil.ok(data);
    }

    /**
     * 这里的参数id是一级目录的id。
     */
    @RequestMapping("current")
    public Object current(Integer id) {
        if(id == null){
            return ResponseUtil.fail402();
        }

        // 当前分类
        LitemallCategory currentCategory = categoryService.findById(id);
        List<LitemallCategory> currentSubCategory = categoryService.queryByPid(currentCategory.getId());

        Map<String, Object> data = new HashMap();
        data.put("currentCategory", currentCategory);
        data.put("currentSubCategory", currentSubCategory);
        return ResponseUtil.ok(data);
    }
}