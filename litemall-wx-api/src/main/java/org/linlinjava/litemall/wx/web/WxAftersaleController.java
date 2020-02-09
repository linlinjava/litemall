package org.linlinjava.litemall.wx.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.db.util.AftersaleConstant;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.util.WxResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 售后服务
 *
 * 目前只支持订单整体售后，不支持订单商品单个售后
 */
@RestController
@RequestMapping("/wx/aftersale")
@Validated
public class WxAftersaleController {
    private final Log logger = LogFactory.getLog(WxAftersaleController.class);

    @Autowired
    private LitemallAftersaleService aftersaleService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;

    /**
     * 售后列表
     *
     * @param userId   用户ID
     * @param status   状态类型，如果是空则是全部
     * @param page     分页页数
     * @param limit    分页大小
     * @param sort     排序字段
     * @param order    排序方式
     * @return 售后列表
     */
    @GetMapping("list")
    public Object list(@LoginUser Integer userId,
                       @RequestParam Short status,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        List<LitemallAftersale> aftersaleList = aftersaleService.queryList(userId, status, page, limit, sort, order);

        List<Map<String, Object>> aftersaleVoList = new ArrayList<>(aftersaleList.size());
        for (LitemallAftersale aftersale : aftersaleList) {
            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(aftersale.getOrderId());

            Map<String, Object> aftersaleVo = new HashMap<>();
            aftersaleVo.put("aftersale", aftersale);
            aftersaleVo.put("goodsList", orderGoodsList);

            aftersaleVoList.add(aftersaleVo);
        }

        return ResponseUtil.okList(aftersaleVoList, aftersaleList);
    }

    /**
     * 售后详情
     *
     * @param id 售后ID
     * @return 售后详情
     */
    @GetMapping("detail")
    public Object detail(@LoginUser Integer userId, @NotNull Integer id) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        LitemallAftersale aftersale = aftersaleService.findById(id);
        if(id == null){
            return ResponseUtil.badArgumentValue();
        }
        if(!userId.equals(aftersale.getUserId())){
            return ResponseUtil.badArgumentValue();
        }

        LitemallOrder order = orderService.findById(aftersale.getOrderId());
        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("aftersale", aftersale);
        data.put("order", order);
        data.put("orderGoods", orderGoodsList);
        return ResponseUtil.ok(data);
    }

    /**
     * 申请售后
     *
     * @param userId   用户ID
     * @param aftersale 用户售后信息
     * @return 操作结果
     */
    @PostMapping("submit")
    public Object submit(@LoginUser Integer userId, @RequestBody LitemallAftersale aftersale) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Object error = validate(aftersale);
        if (error != null) {
            return error;
        }
        // 进一步验证
        Integer orderId = aftersale.getOrderId();
        if(orderId == null){
            return ResponseUtil.badArgument();
        }
        LitemallOrder order = orderService.findById(orderId);
        if(order == null){
            return ResponseUtil.badArgumentValue();
        }
        if(!order.getUserId().equals(userId)){
            return ResponseUtil.badArgumentValue();
        }

        if(!OrderUtil.isConfirmStatus(order) && !OrderUtil.isAutoConfirmStatus(order)){
            return ResponseUtil.fail(WxResponseCode.AFTERSALE_UNALLOWED, "不支持售后");
        }
        BigDecimal amount = order.getActualPrice().subtract(order.getFreightPrice());
        if(aftersale.getAmount().compareTo(amount) > 0){
            return ResponseUtil.fail(WxResponseCode.AFTERSALE_INVALID_AMOUNT, "退款金额不正确");
        }

        if(aftersaleService.countByOrderIdWithoutReject(userId, orderId) > 0){
            return ResponseUtil.fail(WxResponseCode.AFTERSALE_UNALLOWED, "已申请售后");
        }

        aftersale.setStatus(AftersaleConstant.STATUS_REQUEST);
        aftersale.setAftersaleSn(aftersaleService.generateAftersaleSn(userId));
        aftersale.setUserId(userId);
        aftersaleService.add(aftersale);

        return ResponseUtil.ok();
    }

    private Object validate(LitemallAftersale aftersale) {
        Short type = aftersale.getType();
        if (type == null) {
            return ResponseUtil.badArgument();
        }
        BigDecimal amount = aftersale.getAmount();
        if (amount == null) {
            return ResponseUtil.badArgument();
        }
        String reason = aftersale.getReason();
        if (StringUtils.isEmpty(reason)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }
}