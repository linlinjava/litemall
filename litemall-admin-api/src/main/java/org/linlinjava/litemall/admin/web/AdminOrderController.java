package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.domain.LitemallProduct;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.service.LitemallProductService;
import org.linlinjava.litemall.db.util.OrderHandleOption;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {
    private final Log logger = LogFactory.getLog(AdminOrderController.class);

    @Autowired
    private PlatformTransactionManager txManager;

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallProductService productService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       Integer userId, String orderSn,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        if(adminId == null){
            return ResponseUtil.fail401();
        }
        List<LitemallOrder> orderList = orderService.querySelective(userId, orderSn, page, limit, sort, order);
        int total = orderService.countSelective(userId, orderSn, page, limit, sort, order);

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", orderList);

        return ResponseUtil.ok(data);
    }

    /*
     * 目前的逻辑不支持管理员创建
     */
    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallOrder order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        return ResponseUtil.unsupport();
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, Integer id){
        if(adminId == null){
            return ResponseUtil.fail401();
        }

        LitemallOrder order = orderService.findById(id);
        return ResponseUtil.ok(order);
    }

    /*
     * 目前仅仅支持管理员设置发货相关的信息
     */
    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallOrder order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        Integer orderId = order.getId();
        if(orderId == null){
            return ResponseUtil.badArgument();
        }

        LitemallOrder litemallOrder = orderService.findById(orderId);
        if(litemallOrder == null){
            return ResponseUtil.badArgumentValue();
        }

        if(OrderUtil.isPayStatus(litemallOrder) || OrderUtil.isShipStatus(litemallOrder)){
            LitemallOrder newOrder = new LitemallOrder();
            newOrder.setId(orderId);
            newOrder.setShipChannel(order.getShipChannel());
            newOrder.setShipSn(order.getOrderSn());
            newOrder.setShipStartTime(order.getShipStartTime());
            newOrder.setShipEndTime(order.getShipEndTime());
            newOrder.setOrderStatus(OrderUtil.STATUS_SHIP);
            orderService.update(newOrder);
        }
        else {
            return ResponseUtil.badArgumentValue();
        }

        litemallOrder = orderService.findById(orderId);
        return ResponseUtil.ok(litemallOrder);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallOrder order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        return ResponseUtil.unsupport();
    }


    /**
     * 订单退款确认
     * 1. 检测当前订单是否能够退款确认
     * 2. 设置订单退款确认状态
     * 3. 订单商品恢复
     *
     * @param adminId 管理员ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("refundConfirm")
    public Object refundConfirm(@LoginAdmin Integer adminId, @RequestBody String body) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(adminId)) {
            return ResponseUtil.badArgumentValue();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isRefund()) {
            return ResponseUtil.fail(403, "订单不能取消");
        }

        // 开启事务管理
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        try {
            // 设置订单取消状态
            order.setOrderStatus(OrderUtil.STATUS_REFUND_CONFIRM);
            orderService.update(order);

            // 商品货品数量增加
            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                Integer productId = orderGoods.getProductId();
                LitemallProduct product = productService.findById(productId);
                Integer number = product.getGoodsNumber() + orderGoods.getNumber();
                product.setGoodsNumber(number);
                productService.updateById(product);
            }
        } catch (Exception ex) {
            txManager.rollback(status);
            logger.error("系统内部错误", ex);
            return ResponseUtil.fail(403, "订单退款失败");
        }
        txManager.commit(status);

        return ResponseUtil.ok();
    }

    /**
     * 发货
     * 1. 检测当前订单是否能够发货
     * 2. 设置订单发货状态
     *
     * @param adminId 管理员ID
     * @param body   订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("ship")
    public Object ship(@LoginAdmin Integer adminId, @RequestBody String body) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        String shipSn = JacksonUtil.parseString(body, "shipSn");
        String shipChannel = JacksonUtil.parseString(body, "shipChannel");
        if (orderId == null || shipSn == null || shipChannel == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(adminId)) {
            return ResponseUtil.badArgumentValue();
        }

        // 如果订单不是已付款状态，则不能发货
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_PAY)) {
            return ResponseUtil.fail(403, "订单不能确认收货");
        }

        order.setOrderStatus(OrderUtil.STATUS_SHIP);
        order.setShipSn(shipSn);
        order.setShipChannel(shipChannel);
        order.setShipStartTime(LocalDateTime.now());
        orderService.update(order);

        return ResponseUtil.ok();
    }

    /**
     * 自动取消订单
     *
     * 定时检查订单未付款情况，如果超时半个小时则自动取消订单
     * 定时时间是每次相隔半个小时。
     *
     * 注意，因为是相隔半小时检查，因此导致有订单是超时一个小时以后才设置取消状态。
     * TODO
     * 这里可以进一步地配合用户订单查询时订单未付款检查，如果订单超时半小时则取消。
     */
    @Scheduled(fixedDelay = 30*60*1000)
    public void checkOrderUnpaid() {
        logger.debug(LocalDateTime.now());

        List<LitemallOrder> orderList = orderService.queryUnpaid();
        for(LitemallOrder order : orderList){
            LocalDateTime add = order.getAddTime();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expired = add.plusMinutes(30);
            if(expired.isAfter(now)){
                continue;
            }

            // 开启事务管理
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus status = txManager.getTransaction(def);
            try {
                // 设置订单已取消状态
                order.setOrderStatus(OrderUtil.STATUS_AUTO_CANCEL);
                order.setEndTime(LocalDateTime.now());
                orderService.updateById(order);

                // 商品货品数量增加
                Integer orderId = order.getId();
                List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
                for (LitemallOrderGoods orderGoods : orderGoodsList) {
                    Integer productId = orderGoods.getProductId();
                    LitemallProduct product = productService.findById(productId);
                    Integer number = product.getGoodsNumber() + orderGoods.getNumber();
                    product.setGoodsNumber(number);
                    productService.updateById(product);
                }
            } catch (Exception ex) {
                txManager.rollback(status);
                logger.error("系统内部错误", ex);
            }
            txManager.commit(status);
        }
    }

    /**
     * 自动确认订单
     *
     * 定时检查订单未确认情况，如果超时七天则自动确认订单
     * 定时时间是每天凌晨3点。
     *
     * 注意，因为是相隔一天检查，因此导致有订单是超时八天以后才设置自动确认。
     * 这里可以进一步地配合用户订单查询时订单未确认检查，如果订单超时7天则自动确认。
     * 但是，这里可能不是非常必要。相比订单未付款检查中存在商品资源有限所以应该
     * 早点清理未付款情况，这里八天再确认是可以的。
     *
     * TODO
     * 目前自动确认是基于管理后台管理员所设置的商品快递到达时间，见orderService.queryUnconfirm。
     * 那么在实际业务上有可能存在商品寄出以后商品因为一些原因快递最终没有到达，
     * 也就是商品快递失败而shipEndTime一直是空的情况，因此这里业务可能需要扩展，以防止订单一直
     * 处于发货状态。
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void checkOrderUnconfirm() {
        logger.debug(LocalDateTime.now());

        List<LitemallOrder> orderList = orderService.queryUnconfirm();
        for(LitemallOrder order : orderList){
            LocalDateTime shipEnd = order.getShipEndTime();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expired = shipEnd.plusDays(7);
            if(expired.isAfter(now)){
                continue;
            }
            // 设置订单已取消状态
            order.setOrderStatus(OrderUtil.STATUS_AUTO_CONFIRM);
            order.setConfirmTime(now);
            orderService.updateById(order);
        }
    }
}
