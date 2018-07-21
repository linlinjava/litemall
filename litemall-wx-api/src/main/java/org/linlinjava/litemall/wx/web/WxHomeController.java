package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.wx.service.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/home")
public class WxHomeController {
    private final Log logger = LogFactory.getLog(WxHomeController.class);

    @Autowired
    private LitemallAdService adService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallBrandService brandService;
    @Autowired
    private LitemallTopicService topicService;
    @Autowired
    private LitemallCategoryService categoryService;

    /**
     * app首页
     *
     * @return app首页相关信息
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * banner: xxx,
     * channel: xxx,
     * newGoodsList: xxx,
     * hotGoodsList: xxx,
     * topicList: xxx,
     * floorGoodsList: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("/index")
    public Object index() {
        Map<String, Object> data = new HashMap<>();

        List<LitemallAd> banner = adService.queryIndex();
        data.put("banner", banner);

        List<LitemallCategory> channel = categoryService.queryChannel();
        data.put("channel", channel);

        List<LitemallGoods> newGoods = goodsService.queryByNew(0, SystemConfig.getSystemConfig().getIndexLimit(SystemConfig.LIMIT_NEW));
        data.put("newGoodsList", newGoods);

        List<LitemallGoods> hotGoods = goodsService.queryByHot(0, SystemConfig.getSystemConfig().getIndexLimit(SystemConfig.LIMIT_HOT));
        data.put("hotGoodsList", hotGoods);

        List<LitemallBrand> brandList = brandService.query(0, SystemConfig.getSystemConfig().getIndexLimit(SystemConfig.LIMIT_BRAND));
        data.put("brandList", brandList);

        List<LitemallTopic> topicList = topicService.queryList(0, SystemConfig.getSystemConfig().getIndexLimit(SystemConfig.LIMIT_TOPIC));
        data.put("topicList", topicList);

        List<Map> categoryList = new ArrayList<>();
        List<LitemallCategory> catL1List = categoryService.queryL1WithoutRecommend(0, SystemConfig.getSystemConfig().getIndexLimit(SystemConfig.LIMIT_CALLIST));
        for (LitemallCategory catL1 : catL1List) {
            List<LitemallCategory> catL2List = categoryService.queryByPid(catL1.getId());
            List<Integer> l2List = new ArrayList<>();
            for (LitemallCategory catL2 : catL2List) {
                l2List.add(catL2.getId());
            }

            List<LitemallGoods> categoryGoods = null;
            if (l2List.size() == 0) {
                categoryGoods = new ArrayList<>();
            } else {
                categoryGoods = goodsService.queryByCategory(l2List, 0, SystemConfig.getSystemConfig().getIndexLimit(SystemConfig.LIMIT_CALGOOD));
            }

            Map catGoods = new HashMap();
            catGoods.put("id", catL1.getId());
            catGoods.put("name", catL1.getName());
            catGoods.put("goodsList", categoryGoods);
            categoryList.add(catGoods);
        }
        data.put("floorGoodsList", categoryList);

        return ResponseUtil.ok(data);
    }
}