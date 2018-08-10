package org.linlinjava.litemall.wx.web;

import com.mysql.jdbc.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.HomeCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/goods")
@Validated
public class WxGoodsController {
    private final Log logger = LogFactory.getLog(WxGoodsController.class);

    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallProductService productService;
    @Autowired
    private LitemallIssueService goodsIssueService;
    @Autowired
    private LitemallGoodsAttributeService goodsAttributeService;
    @Autowired
    private LitemallBrandService brandService;
    @Autowired
    private LitemallCommentService commentService;
    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallCollectService collectService;
    @Autowired
    private LitemallFootprintService footprintService;
    @Autowired
    private LitemallCategoryService categoryService;
    @Autowired
    private LitemallSearchHistoryService searchHistoryService;
    @Autowired
    private LitemallGoodsSpecificationService goodsSpecificationService;
    @Autowired
    private LitemallGrouponRulesService rulesService;


    /**
     * 商品详情
     * <p>
     * 用户可以不登录。
     * 如果用户登录，则记录用户足迹以及返回用户收藏信息。
     *
     * @param userId 用户ID
     * @param id     商品ID
     * @return 商品详情
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * info: xxx,
     * userHasCollect: xxx,
     * issue: xxx,
     * comment: xxx,
     * specificationList: xxx,
     * productList: xxx,
     * attribute: xxx,
     * brand: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("detail")
    public Object detail(@LoginUser Integer userId, @NotNull Integer id) {
        // 商品信息
        LitemallGoods info = goodsService.findById(id);

        // 商品属性
        List<LitemallGoodsAttribute> goodsAttributeList = goodsAttributeService.queryByGid(id);

        // 商品规格
        // 返回的是定制的GoodsSpecificationVo
        Object specificationList = goodsSpecificationService.getSpecificationVoList(id);

        // 商品规格对应的数量和价格
        List<LitemallProduct> productList = productService.queryByGid(id);

        // 商品问题，这里是一些通用问题
        List<LitemallIssue> issue = goodsIssueService.query();

        // 商品品牌商
        LitemallBrand brand = brandService.findById(info.getBrandId());

        // 评论
        List<LitemallComment> comments = commentService.queryGoodsByGid(id, 0, 2);
        List<Map<String, Object>> commentsVo = new ArrayList<>(comments.size());
        int commentCount = commentService.countGoodsByGid(id, 0, 2);
        for (LitemallComment comment : comments) {
            Map<String, Object> c = new HashMap<>();
            c.put("id", comment.getId());
            c.put("addTime", comment.getAddTime());
            c.put("content", comment.getContent());
            LitemallUser user = userService.findById(comment.getUserId());
            c.put("nickname", user.getNickname());
            c.put("avatar", user.getAvatar());
            c.put("picList", comment.getPicUrls());
            commentsVo.add(c);
        }
        Map<String, Object> commentList = new HashMap<>();
        commentList.put("count", commentCount);
        commentList.put("data", commentsVo);

        //团购信息
        List<LitemallGrouponRules> rules = rulesService.queryByGoodsId(id);

        // 用户收藏
        int userHasCollect = 0;
        if (userId != null) {
            userHasCollect = collectService.count(userId, id);
        }

        // 记录用户的足迹
        if (userId != null) {
            LitemallFootprint footprint = new LitemallFootprint();
            footprint.setAddTime(LocalDateTime.now());
            footprint.setUserId(userId);
            footprint.setGoodsId(id);
            footprintService.add(footprint);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("info", info);
        data.put("userHasCollect", userHasCollect);
        data.put("issue", issue);
        data.put("comment", commentList);
        data.put("specificationList", specificationList);
        data.put("productList", productList);
        data.put("attribute", goodsAttributeList);
        data.put("brand", brand);
        data.put("groupon", rules);

        //商品分享图片地址
        data.put("shareImage", info.getShareUrl());
        return ResponseUtil.ok(data);
    }

    /**
     * 商品分类类目
     * <p>
     * TODO 可能应该合并到WxCatalogController中
     *
     * @param id 分类类目ID
     * @return 商品分类类目
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * currentCategory: xxx,
     * parentCategory: xxx,
     * brotherCategory: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("category")
    public Object category(@NotNull Integer id) {
        LitemallCategory cur = categoryService.findById(id);
        LitemallCategory parent = null;
        List<LitemallCategory> children = null;

        if (cur.getPid() == 0) {
            parent = cur;
            children = categoryService.queryByPid(cur.getId());
            cur = children.size()>0?children.get(0):cur;
        } else {
            parent = categoryService.findById(cur.getPid());
            children = categoryService.queryByPid(cur.getPid());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("currentCategory", cur);
        data.put("parentCategory", parent);
        data.put("brotherCategory", children);
        return ResponseUtil.ok(data);
    }

    /**
     * 根据条件搜素商品
     * <p>
     * 1. 这里的前五个参数都是可选的，甚至都是空
     * 2. 用户是可选登录，如果登录，则记录用户的搜索关键字
     *
     * @param categoryId 分类类目ID
     * @param brandId    品牌商ID
     * @param keyword    关键字
     * @param isNew      是否新品
     * @param isHot      是否热买
     * @param userId     用户ID
     * @param page       分页页数
     * @param size       分页大小
     * @param sort       排序方式
     * @param order      排序类型，顺序或者降序
     * @return 根据条件搜素的商品详情
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * goodsList: xxx,
     * filterCategoryList: xxx,
     * count: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("list")
    public Object list(Integer categoryId, Integer brandId, String keyword, Boolean isNew, Boolean isHot,
                       @LoginUser Integer userId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       @Sort(accepts = {"add_time", "retail_price", "name"}) @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {

        //添加到搜索历史
        if (userId != null && !StringUtils.isNullOrEmpty(keyword)) {
            LitemallSearchHistory searchHistoryVo = new LitemallSearchHistory();
            searchHistoryVo.setAddTime(LocalDateTime.now());
            searchHistoryVo.setKeyword(keyword);
            searchHistoryVo.setUserId(userId);
            searchHistoryVo.setFrom("wx");
            searchHistoryService.save(searchHistoryVo);
        }

        //查询列表数据
        List<LitemallGoods> goodsList = goodsService.querySelective(categoryId, brandId, keyword, isHot, isNew, page, size, sort, order);
        int total = goodsService.countSelective(categoryId, brandId, keyword, isHot, isNew, page, size, sort, order);

        // 查询商品所属类目列表。
        List<Integer> goodsCatIds = goodsService.getCatIds(brandId, keyword, isHot, isNew);
        List<LitemallCategory> categoryList = null;
        if (goodsCatIds.size() != 0) {
            categoryList = categoryService.queryL2ByIds(goodsCatIds);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goodsList", goodsList);
        data.put("filterCategoryList", categoryList);
        data.put("count", total);
        return ResponseUtil.ok(data);
    }

    /**
     * 新品首发页面的横幅数据
     * <p>
     * TODO 其实可以删除
     *
     * @return 新品首发页面的栏目数据
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * bannerInfo: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("new")
    public Object newGoods() {
        Map<String, String> bannerInfo = new HashMap<>();
        bannerInfo.put("url", "");
        bannerInfo.put("name", SystemConfig.getNewBannerTitle());
        bannerInfo.put("imgUrl", SystemConfig.getNewImageUrl());

        Map<String, Object> data = new HashMap<>();
        data.put("bannerInfo", bannerInfo);
        return ResponseUtil.ok(data);
    }

    /**
     * 人气推荐页面的横幅数据
     * <p>
     * TODO 其实可以删除
     *
     * @return 人气推荐页面的栏目数据
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * bannerInfo: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("hot")
    public Object hotGoods() {
        Map<String, String> bannerInfo = new HashMap<>();
        bannerInfo.put("url", "");
        bannerInfo.put("name", SystemConfig.getHotBannerTitle());
        bannerInfo.put("imgUrl", SystemConfig.getHotImageUrl());
        Map<String, Object> data = new HashMap<>();
        data.put("bannerInfo", bannerInfo);
        return ResponseUtil.ok(data);
    }

    /**
     * 商品页面推荐商品
     *
     * @return 商品页面推荐商品
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * goodsList: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("related")
    public Object related(@NotNull Integer id) {
        LitemallGoods goods = goodsService.findById(id);
        if (goods == null) {
            return ResponseUtil.badArgumentValue();
        }

        // 目前的商品推荐算法仅仅是推荐同类目的其他商品
        int cid = goods.getCategoryId();

        // 查找六个相关商品
        int related = 6;
        List<LitemallGoods> goodsList = goodsService.queryByCategory(cid, 0, related);
        Map<String, Object> data = new HashMap<>();
        data.put("goodsList", goodsList);
        return ResponseUtil.ok(data);
    }

    /**
     * 在售的商品总数
     *
     * @return 在售的商品总数
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * goodsCount: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("count")
    public Object count() {
        Integer goodsCount = goodsService.queryOnSale();
        Map<String, Object> data = new HashMap<>();
        data.put("goodsCount", goodsCount);
        return ResponseUtil.ok(data);
    }

}