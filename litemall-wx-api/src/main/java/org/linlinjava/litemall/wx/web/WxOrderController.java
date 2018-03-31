package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.domain.LitemallAddress;
import org.linlinjava.litemall.db.domain.LitemallCart;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.db.util.JacksonUtil;
import org.linlinjava.litemall.db.util.OrderHandleOption;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 订单设计
 *
 * 订单状态：
 * 101 订单生成，未支付；102，订单生产，但是未支付就取消；
 * 201 支付完成，商家未发货；202，订单生产，已付款未发货，却取消
 * 301 商家发货，用户未确认；
 * 401 用户确认收货，订单结束； 402 用户没有确认收货，但是快递反馈已收获后，超过一定时间，系统自动确认收货，订单结束。
 *
 * 当101用户未付款时，此时用户可以进行的操作是取消订单，或者付款操作
 * 当201支付完成而商家未发货时，此时用户可以取消订单并申请退款
 * 当301商家已发货时，此时用户可以有确认收货的操作
 * 当401用户确认收货以后，此时用户可以进行的操作是删除订单，评价商品，或者再次购买
 * 当402系统自动确认收货以后，此时用户可以删除订单，评价商品，或者再次购买
 *
 * 目前不支持订单退货
 */
@RestController
@RequestMapping("/wx/order")
public class WxOrderController {
    private final Log logger = LogFactory.getLog(WxOrderController.class);

    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallAddressService addressService;
    @Autowired
    private LitemallCartService cartService;
    @Autowired
    private LitemallRegionService regionService;

    public WxOrderController() {
    }

    private String detailedAddress(LitemallAddress zmallAddress) {
        Integer provinceId = zmallAddress.getProvinceId();
        Integer cityId = zmallAddress.getCityId();
        Integer areaId = zmallAddress.getAreaId();
        String provinceName = regionService.findById(provinceId).getName();
        String cityName = regionService.findById(cityId).getName();
        String areaName = regionService.findById(areaId).getName();
        String fullRegion = provinceName + " " + cityName + " " + areaName;
        return fullRegion + " " + zmallAddress.getAddress();
    }

    /**
     * 订单列表
     * showType:
     *  0， 全部订单
     *  1，待付款
     *  2，待发货
     *  3，待收货
     *  4，待评价
     */
    @RequestMapping("list")
    public Object list(@LoginUser Integer userId, Integer showType,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(showType == null){
            showType = 0;
        }

        List<Short> orderStatus = OrderUtil.orderStatus(showType);
        List<LitemallOrder> orderList = orderService.queryByOrderStatus(userId, orderStatus);
        int count = orderService.countByOrderStatus(userId, orderStatus);

        List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
        for(LitemallOrder order : orderList){
            Map<String, Object> orderVo = new HashMap<>();
            orderVo.put("id", order.getId());
            orderVo.put("orderSn", order.getOrderSn());
            orderVo.put("actualPrice", order.getActualPrice());
            orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
            orderVo.put("handleOption", OrderUtil.build(order));

            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
            List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
            for(LitemallOrderGoods orderGoods : orderGoodsList){
                Map<String, Object> orderGoodsVo = new HashMap<>();
                orderGoodsVo.put("id", orderGoods.getId());
                orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
                orderGoodsVo.put("number", orderGoods.getNumber());
                orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
                orderGoodsVoList.add(orderGoodsVo);
            }
            orderVo.put("goodsList", orderGoodsVoList);

            orderVoList.add(orderVo);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("data", orderVoList);

        return ResponseUtil.ok(result);
    }

    /**
     * 订单详情
     */
    @RequestMapping("detail")
    public Object detail(@LoginUser Integer userId, Integer orderId) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(orderId == null){
            return ResponseUtil.fail402();
        }

        // 订单信息
        LitemallOrder order = orderService.findById(orderId);
        if (null == order) {
            return ResponseUtil.fail(403, "订单不存在");
        }
        if(!order.getUserId().equals(userId)){
            return ResponseUtil.fail(403, "不是当前用户的订单");
        }
        Map<String, Object> orderVo = new HashMap<String, Object>();
        orderVo.put("id", order.getId());
        orderVo.put("orderSn", order.getOrderSn());
        orderVo.put("addTime", LocalDate.now());
        orderVo.put("consignee", order.getConsignee());
        orderVo.put("mobile", order.getMobile());
        orderVo.put("address", order.getAddress());
        orderVo.put("goodsPrice", order.getGoodsPrice());
        orderVo.put("freightPrice", order.getFreightPrice());
        orderVo.put("actualPrice", order.getActualPrice());
        orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
        orderVo.put("handleOption", OrderUtil.build(order));

        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
        List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
        for(LitemallOrderGoods orderGoods : orderGoodsList){
            Map<String, Object> orderGoodsVo = new HashMap<>();
            orderGoodsVo.put("id", orderGoods.getId());
            orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
            orderGoodsVo.put("number", orderGoods.getNumber());
            orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
            orderGoodsVo.put("goodsSpecifitionValues", orderGoods.getGoodsSpecificationValues());
            orderGoodsVoList.add(orderGoodsVo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderVo);
        result.put("orderGoods", orderGoodsList);
        return ResponseUtil.ok(result);

    }

    /**
     * 提交订单
     */
    @PostMapping("submit")
    public Object submit(@LoginUser Integer userId, @RequestBody String body) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(body == null){
            return ResponseUtil.fail402();
        }
        Integer cartId = JacksonUtil.parseInteger(body, "cartId");
        Integer addressId = JacksonUtil.parseInteger(body, "addressId");
        Integer couponId = JacksonUtil.parseInteger(body, "couponId");
        if(cartId == null || addressId == null || couponId == null){
            return ResponseUtil.badArgument();
        }

        // 收货地址
        LitemallAddress checkedAddress = addressService.findById(addressId);

        // 获取可用的优惠券信息
        // 使用优惠券减免的金额
        BigDecimal couponPrice = new BigDecimal(0.00);

        // 商品价格
        List<LitemallCart> checkedGoodsList = null;
        if (cartId.equals(0)) {
            checkedGoodsList = cartService.queryByUidAndChecked(userId);
        }
        else {
            LitemallCart cart = cartService.findById(cartId);
            checkedGoodsList = new ArrayList<>(1);
            checkedGoodsList.add(cart);
        }
        if(checkedGoodsList.size() == 0){
            return ResponseUtil.badArgumentValue();
        }
        BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
        for (LitemallCart cart : checkedGoodsList) {
            checkedGoodsPrice = checkedGoodsPrice.add(cart.getRetailPrice().multiply(new BigDecimal(cart.getNumber())));
        }

        // 根据订单商品总价计算运费，满88则免运费，否则8元；
        BigDecimal freightPrice = new BigDecimal(0.00);
        if(checkedGoodsPrice.compareTo(new BigDecimal(88.00)) == -1){
            freightPrice = new BigDecimal(8.00);
        }

        // 可以使用的其他钱，例如用户积分
        BigDecimal integralPrice = new BigDecimal(0.00);


        // 订单费用
        BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice).subtract(couponPrice);
        BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);

        // 添加订单表
        LitemallOrder order = new LitemallOrder();
        order.setUserId(userId);
        order.setOrderSn(orderService.generateOrderSn(userId));
        order.setAddTime(LocalDateTime.now());
        order.setOrderStatus(OrderUtil.STATUS_PAY);
        order.setConsignee(checkedAddress.getName());
        order.setMobile(checkedAddress.getMobile());
        String detailedAddress = detailedAddress(checkedAddress);
        order.setAddress(detailedAddress);
        order.setGoodsPrice(checkedGoodsPrice);
        order.setFreightPrice(freightPrice);
        order.setCouponPrice(couponPrice);
        order.setIntegralPrice(integralPrice);
        order.setOrderPrice(orderTotalPrice);
        order.setActualPrice(actualPrice);
        orderService.add(order);

        // 添加订单商品表
        for(LitemallCart cartGoods : checkedGoodsList){
            LitemallOrderGoods orderGoods = new LitemallOrderGoods();
            orderGoods.setOrderId(order.getId());
            orderGoods.setGoodsId(cartGoods.getGoodsId());
            orderGoods.setGoodsSn(cartGoods.getGoodsSn());
            orderGoods.setProductId(cartGoods.getProductId());
            orderGoods.setGoodsName(cartGoods.getGoodsName());
            orderGoods.setPicUrl(cartGoods.getPicUrl());
            orderGoods.setRetailPrice(cartGoods.getRetailPrice());
            orderGoods.setNumber(cartGoods.getNumber());
            orderGoods.setGoodsSpecificationIds(cartGoods.getGoodsSpecificationIds());
            orderGoods.setGoodsSpecificationValues(cartGoods.getGoodsSpecificationValues());
            orderGoodsService.add(orderGoods);
        }

        // 删除购物车里面的商品信息
        cartService.clearGoods(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("orderInfo", order);
        return ResponseUtil.ok(data);

    }

    /**
     * 取消订单
     */
    @RequestMapping("cancel")
    public Object cancel(@LoginUser Integer userId, Integer orderId) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(orderId == null){
            return ResponseUtil.fail402();
        }

        LitemallOrder order = orderService.findById(orderId);
        if(order == null){
            return ResponseUtil.fail403();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if(!handleOption.isCancel()){
            return ResponseUtil.fail(403, "订单不能取消");
        }

        order.setOrderStatus((short)102);
        orderService.update(order);
        return ResponseUtil.ok();
    }

    @RequestMapping("refund")
    public Object refund(@LoginUser Integer userId, Integer orderId) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(orderId == null){
            return ResponseUtil.fail402();
        }

        LitemallOrder order = orderService.findById(orderId);
        if(order == null){
            return ResponseUtil.fail403();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if(!handleOption.isRefund()){
            return ResponseUtil.fail(403, "订单不能取消");
        }

        // 退款操作

        // 设置订单取消状态
        order.setOrderStatus((short)202);

        orderService.update(order);
        return ResponseUtil.ok();
    }

    @RequestMapping("delete")
    public Object delete(@LoginUser Integer userId, Integer orderId) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(orderId == null){
            return ResponseUtil.fail402();
        }

        LitemallOrder order = orderService.findById(orderId);
        if(order == null){
            return ResponseUtil.fail403();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if(!handleOption.isDelete()){
            return ResponseUtil.fail(403, "订单不能删除");
        }

        // 订单order_status没有字段用于标识删除
        // 而是存在专门的is_delete字段表示是否删除
        order.setIsDelete(true);

        orderService.update(order);
        return ResponseUtil.ok();
    }

    /**
     * 用户确认收货
     */
    @RequestMapping("confirm")
    public Object confirm(@LoginUser Integer userId, Integer orderId) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(orderId == null){
            return ResponseUtil.fail402();
        }

        LitemallOrder order = orderService.findById(orderId);
        if(order == null){
            return ResponseUtil.fail403();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if(!handleOption.isConfirm()){
            return ResponseUtil.fail(403, "订单不能确认收货");
        }

        order.setOrderStatus((short)401);
        order.setConfirmTime(LocalDateTime.now());
        orderService.update(order);
        return ResponseUtil.ok();
    }

    /**
     * 用户评价的商品信息
     */
    @RequestMapping("comment")
    public Object comment(@LoginUser Integer userId, Integer orderId, Integer goodsId) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(orderId == null || goodsId == null){
            return ResponseUtil.fail402();
        }

        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.findByOidAndGid(orderId, goodsId);
        int size = orderGoodsList.size();

        Assert.state(size < 2, "存在多个符合条件的订单商品");

        if(size == 0){
            return ResponseUtil.badArgumentValue();
        }

        LitemallOrderGoods orderGoods = orderGoodsList.get(0);
        return ResponseUtil.ok(orderGoods);
    }
}