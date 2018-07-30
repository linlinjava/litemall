package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallCategory;
import org.linlinjava.litemall.db.service.LitemallCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
     * 分类栏目
     *
     * @param id   分类类目ID
     *             如果分类类目ID是空，则选择第一个分类类目。
     *             需要注意，这里分类类目是一级类目
     * @param page 分页页数
     * @param size 分页大小
     * @return 分类栏目
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * categoryList: xxx,
     * currentCategory: xxx,
     * currentSubCategory: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("index")
    public Object index(Integer id,
                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "size", defaultValue = "10") Integer size) {

        // 所有一级分类目录
        List<LitemallCategory> l1CatList = categoryService.queryL1();

        // 当前一级分类目录
        LitemallCategory currentCategory = null;
        if (id != null) {
            currentCategory = categoryService.findById(id);
        } else {
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
     * 一次性获取全部分类数据
     *
     * @return
     */
    @GetMapping("all")
    public Object queryAll() {
        // 所有一级分类目录
        List<LitemallCategory> l1CatList = categoryService.queryL1();

        //所有子分类列表
        Map<Integer, List<LitemallCategory>> allList = new HashMap<>();
        List<LitemallCategory> sub;
        for (LitemallCategory category : l1CatList) {
            sub = categoryService.queryByPid(category.getId());
            allList.put(category.getId(), sub);
        }

        // 当前一级分类目录
        LitemallCategory currentCategory = l1CatList.get(0);

        // 当前一级分类目录对应的二级分类目录
        List<LitemallCategory> currentSubCategory = null;
        if (null != currentCategory) {
            currentSubCategory = categoryService.queryByPid(currentCategory.getId());
        }

        Map<String, Object> data = new HashMap();
        data.put("categoryList", l1CatList);
        data.put("allList", allList);
        data.put("currentCategory", currentCategory);
        data.put("currentSubCategory", currentSubCategory);
        return ResponseUtil.ok(data);
    }

    /**
     * 当前分类栏目
     *
     * @param id 分类类目ID
     * @return 当前分类栏目
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * currentCategory: xxx,
     * currentSubCategory: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("current")
    public Object current(Integer id) {
        if (id == null) {
            return ResponseUtil.badArgument();
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