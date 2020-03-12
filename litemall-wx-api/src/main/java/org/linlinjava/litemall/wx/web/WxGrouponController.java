package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.express.ExpressService;
import org.linlinjava.litemall.core.express.dao.ExpressInfo;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.WxGrouponRuleService;
import org.linlinjava.litemall.wx.vo.GrouponRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.linlinjava.litemall.wx.util.WxResponseCode.*;

/**
 * 团购服务
 * <p>
 * 需要注意这里团购规则和团购活动的关系和区别。
 */
@RestController
@RequestMapping("/wx/groupon")
@Validated
public class WxGrouponController {
    private final Log logger = LogFactory.getLog(WxGrouponController.class);

    @Autowired
    private LitemallGrouponRulesService rulesService;
    @Autowired
    private WxGrouponRuleService wxGrouponRuleService;
    @Autowired
    private LitemallGrouponService grouponService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallUserService userService;
    @Autowired
    private ExpressService expressService;
    @Autowired
    private LitemallGrouponRulesService grouponRulesService;

    /**
     * 团购规则列表
     *
     * @param page 分页页数
     * @param limit 分页大小
     * @return 团购规则列表
     */
    @GetMapping("list")
    public Object list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<GrouponRuleVo> grouponRuleVoList = wxGrouponRuleService.queryList(page, limit, sort, order);
        return ResponseUtil.okList(grouponRuleVoList);
    }

    /**
     * 团购活动详情
     *
     * @param userId    用户ID
     * @param grouponId 团购活动ID
     * @return 团购活动详情
     */
    @GetMapping("detail")
    public Object detail(@LoginUser Integer userId, @NotNull Integer grouponId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        LitemallGroupon groupon = grouponService.queryById(userId, grouponId);
        if (groupon == null) {
            return ResponseUtil.badArgumentValue();
        }

        LitemallGrouponRules rules = rulesService.findById(groupon.getRulesId());
        if (rules == null) {
            return ResponseUtil.badArgumentValue();
        }

        // 订单信息
        LitemallOrder order = orderService.findById(userId, groupon.getOrderId());
        if (null == order) {
            return ResponseUtil.fail(ORDER_UNKNOWN, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.fail(ORDER_INVALID, "不是当前用户的订单");
        }
        Map<String, Object> orderVo = new HashMap<String, Object>();
        orderVo.put("id", order.getId());
        orderVo.put("orderSn", order.getOrderSn());
        orderVo.put("addTime", order.getAddTime());
        orderVo.put("consignee", order.getConsignee());
        orderVo.put("mobile", order.getMobile());
        orderVo.put("address", order.getAddress());
        orderVo.put("goodsPrice", order.getGoodsPrice());
        orderVo.put("freightPrice", order.getFreightPrice());
        orderVo.put("actualPrice", order.getActualPrice());
        orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
        orderVo.put("handleOption", OrderUtil.build(order));
        orderVo.put("expCode", order.getShipChannel());
        orderVo.put("expNo", order.getShipSn());

        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
        List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
        for (LitemallOrderGoods orderGoods : orderGoodsList) {
            Map<String, Object> orderGoodsVo = new HashMap<>();
            orderGoodsVo.put("id", orderGoods.getId());
            orderGoodsVo.put("orderId", orderGoods.getOrderId());
            orderGoodsVo.put("goodsId", orderGoods.getGoodsId());
            orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
            orderGoodsVo.put("number", orderGoods.getNumber());
            orderGoodsVo.put("retailPrice", orderGoods.getPrice());
            orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
            orderGoodsVo.put("goodsSpecificationValues", orderGoods.getSpecifications());
            orderGoodsVoList.add(orderGoodsVo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderVo);
        result.put("orderGoods", orderGoodsVoList);

        // 订单状态为已发货且物流信息不为空
        //"YTO", "800669400640887922"
        if (order.getOrderStatus().equals(OrderUtil.STATUS_SHIP)) {
            ExpressInfo ei = expressService.getExpressInfo(order.getShipChannel(), order.getShipSn());
            result.put("expressInfo", ei);
        }

        UserVo creator = userService.findUserVoById(groupon.getCreatorUserId());
        List<UserVo> joiners = new ArrayList<>();
        joiners.add(creator);
        int linkGrouponId;
        // 这是一个团购发起记录
        if (groupon.getGrouponId() == 0) {
            linkGrouponId = groupon.getId();
        } else {
            linkGrouponId = groupon.getGrouponId();

        }
        List<LitemallGroupon> groupons = grouponService.queryJoinRecord(linkGrouponId);

        UserVo joiner;
        for (LitemallGroupon grouponItem : groupons) {
            joiner = userService.findUserVoById(grouponItem.getUserId());
            joiners.add(joiner);
        }

        result.put("linkGrouponId", linkGrouponId);
        result.put("creator", creator);
        result.put("joiners", joiners);
        result.put("groupon", groupon);
        result.put("rules", rules);
        return ResponseUtil.ok(result);
    }

    /**
     * 参加团购
     *
     * @param grouponId 团购活动ID
     * @return 操作结果
     */
    @GetMapping("join")
    public Object join(@NotNull Integer grouponId) {
        LitemallGroupon groupon = grouponService.queryById(grouponId);
        if (groupon == null) {
            return ResponseUtil.badArgumentValue();
        }

        LitemallGrouponRules rules = rulesService.findById(groupon.getRulesId());
        if (rules == null) {
            return ResponseUtil.badArgumentValue();
        }

        LitemallGoods goods = goodsService.findById(rules.getGoodsId());
        if (goods == null) {
            return ResponseUtil.badArgumentValue();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("groupon", groupon);
        result.put("goods", goods);

        return ResponseUtil.ok(result);
    }

    /**
     * 用户开团或入团情况
     *
     * @param userId 用户ID
     * @param showType 显示类型，如果是0，则是当前用户开的团购；否则，则是当前用户参加的团购
     * @return 用户开团或入团情况
     */
    @GetMapping("my")
    public Object my(@LoginUser Integer userId, @RequestParam(defaultValue = "0") Integer showType) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        List<LitemallGroupon> myGroupons;
        if (showType == 0) {
            myGroupons = grouponService.queryMyGroupon(userId);
        } else {
            myGroupons = grouponService.queryMyJoinGroupon(userId);
        }

        List<Map<String, Object>> grouponVoList = new ArrayList<>(myGroupons.size());

        LitemallOrder order;
        LitemallGrouponRules rules;
        LitemallUser creator;
        for (LitemallGroupon groupon : myGroupons) {
            order = orderService.findById(userId, groupon.getOrderId());
            rules = rulesService.findById(groupon.getRulesId());
            creator = userService.findById(groupon.getCreatorUserId());

            Map<String, Object> grouponVo = new HashMap<>();
            //填充团购信息
            grouponVo.put("id", groupon.getId());
            grouponVo.put("groupon", groupon);
            grouponVo.put("rules", rules);
            grouponVo.put("creator", creator.getNickname());

            int linkGrouponId;
            // 这是一个团购发起记录
            if (groupon.getGrouponId() == 0) {
                linkGrouponId = groupon.getId();
                grouponVo.put("isCreator", creator.getId() == userId);
            } else {
                linkGrouponId = groupon.getGrouponId();
                grouponVo.put("isCreator", false);
            }
            int joinerCount = grouponService.countGroupon(linkGrouponId);
            grouponVo.put("joinerCount", joinerCount + 1);

            //填充订单信息
            grouponVo.put("orderId", order.getId());
            grouponVo.put("orderSn", order.getOrderSn());
            grouponVo.put("actualPrice", order.getActualPrice());
            grouponVo.put("orderStatusText", OrderUtil.orderStatusText(order));

            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
            List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                Map<String, Object> orderGoodsVo = new HashMap<>();
                orderGoodsVo.put("id", orderGoods.getId());
                orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
                orderGoodsVo.put("number", orderGoods.getNumber());
                orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
                orderGoodsVoList.add(orderGoodsVo);
            }
            grouponVo.put("goodsList", orderGoodsVoList);
            grouponVoList.add(grouponVo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", grouponVoList.size());
        result.put("list", grouponVoList);

        return ResponseUtil.ok(result);
    }

}
