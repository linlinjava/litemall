package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.linlinjava.litemall.core.notify.NotifyType;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/order")
@Validated
public class AdminOrderController {
    private final Log logger = LogFactory.getLog(AdminOrderController.class);

    @Autowired
    private PlatformTransactionManager txManager;

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallGoodsProductService productService;
    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallCommentService commentService;

    @Autowired
    private NotifyService notifyService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       Integer userId, String orderSn,
                       @RequestParam(required = false) List<Short> orderStatusArray,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }
        List<LitemallOrder> orderList = orderService.querySelective(userId, orderSn, orderStatusArray, page, limit, sort, order);
        int total = orderService.countSelective(userId, orderSn, orderStatusArray, page, limit, sort, order);

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", orderList);

        return ResponseUtil.ok(data);
    }

    @GetMapping("/detail")
    public Object detail(@LoginAdmin Integer adminId, @NotNull Integer id) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        LitemallOrder order = orderService.findById(id);
        List<LitemallOrderGoods> orderGoods = orderGoodsService.queryByOid(id);
        UserVo user = userService.findUserVoById(order.getUserId());
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("orderGoods", orderGoods);
        data.put("user", user);

        return ResponseUtil.ok(data);
    }

    /**
     * 订单退款确认
     * 1. 检测当前订单是否能够退款确认
     * 2. 设置订单退款确认状态
     * 3. 订单商品恢复
     *
     * @param adminId 管理员ID
     * @param body    订单信息，{ orderId：xxx }
     * @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("refund")
    public Object refund(@LoginAdmin Integer adminId, @RequestBody String body) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        String refundMoney = JacksonUtil.parseString(body, "refundMoney");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }

        if (order.getActualPrice().compareTo(new BigDecimal(refundMoney)) != 0) {
            return ResponseUtil.badArgumentValue();
        }

        // 如果订单不是退款状态，则不能退款
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_REFUND)) {
            return ResponseUtil.fail(403, "订单不能确认收货");
        }

        // 开启事务管理
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        try {
            // 设置订单取消状态
            order.setOrderStatus(OrderUtil.STATUS_REFUND_CONFIRM);
            if (orderService.updateWithOptimisticLocker(order) == 0) {
                throw new Exception("跟新数据已失效");
            }

            // 商品货品数量增加
            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                Integer productId = orderGoods.getProductId();
                Short number = orderGoods.getNumber();
                if (productService.addStock(productId, number) == 0) {
                    throw new Exception("商品货品库存增加失败");
                }
            }
        } catch (Exception ex) {
            txManager.rollback(status);
            logger.error("系统内部错误", ex);
            return ResponseUtil.fail(403, "订单退款失败");
        }

        //TODO 发送邮件和短信通知，这里采用异步发送
        // 退款成功通知用户
        /**
         *
         * 您申请的订单退款 [ 单号:{1} ] 已成功，请耐心等待到账。
         * 注意订单号只发后6位
         *
         */
        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND, new String[]{order.getOrderSn().substring(8, 14)});


        txManager.commit(status);

        return ResponseUtil.ok();
    }

    /**
     * 发货
     * 1. 检测当前订单是否能够发货
     * 2. 设置订单发货状态
     *
     * @param adminId 管理员ID
     * @param body    订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
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

        // 如果订单不是已付款状态，则不能发货
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_PAY)) {
            return ResponseUtil.fail(403, "订单不能确认收货");
        }

        order.setOrderStatus(OrderUtil.STATUS_SHIP);
        order.setShipSn(shipSn);
        order.setShipChannel(shipChannel);
        order.setShipTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }

        //TODO 发送邮件和短信通知，这里采用异步发送
        // 发货会发送通知短信给用户:          *
        // "您的订单已经发货，快递公司 {1}，快递单 {2} ，请注意查收"
        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.SHIP, new String[]{shipChannel, shipSn});

        return ResponseUtil.ok();
    }


    /**
     * 回复订单商品
     *
     * @param adminId 管理员ID
     * @param body    订单信息，{ orderId：xxx }
     * @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("reply")
    public Object reply(@LoginAdmin Integer adminId, @RequestBody String body) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        Integer commentId = JacksonUtil.parseInteger(body, "commentId");
        if (commentId == null || commentId == 0) {
            return ResponseUtil.badArgument();
        }
        // 目前只支持回复一次
        if (commentService.findById(commentId) != null) {
            return ResponseUtil.fail(404, "订单商品已回复！");
        }
        String content = JacksonUtil.parseString(body, "content");
        if (StringUtils.isEmpty(content)) {
            return ResponseUtil.badArgument();
        }
        // 创建评价回复
        LitemallComment comment = new LitemallComment();
        comment.setType((byte) 2);
        comment.setValueId(commentId);
        comment.setContent(content);
        comment.setUserId(0);                 // 评价回复没有用
        comment.setStar((short) 0);           // 评价回复没有用
        comment.setHasPicture(false);        // 评价回复没有用
        comment.setPicUrls(new String[]{});  // 评价回复没有用
        commentService.save(comment);

        return ResponseUtil.ok();
    }

    /**
     * 自动取消订单
     * <p>
     * 定时检查订单未付款情况，如果超时半个小时则自动取消订单
     * 定时时间是每次相隔半个小时。
     * <p>
     * 注意，因为是相隔半小时检查，因此导致有订单是超时一个小时以后才设置取消状态。
     * TODO
     * 这里可以进一步地配合用户订单查询时订单未付款检查，如果订单超时半小时则取消。
     */
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void checkOrderUnpaid() {
        logger.info("系统开启任务检查订单是否已经超期自动取消订单");

        List<LitemallOrder> orderList = orderService.queryUnpaid();
        for (LitemallOrder order : orderList) {
            LocalDateTime add = order.getAddTime();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expired = add.plusMinutes(30);
            if (expired.isAfter(now)) {
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
                if (orderService.updateWithOptimisticLocker(order) == 0) {
                    throw new Exception("跟新数据已失效");
                }

                // 商品货品数量增加
                Integer orderId = order.getId();
                List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
                for (LitemallOrderGoods orderGoods : orderGoodsList) {
                    Integer productId = orderGoods.getProductId();
                    LitemallGoodsProduct product = productService.findById(productId);
                    Short number = orderGoods.getNumber();
                    if (productService.addStock(productId, number) == 0) {
                        throw new Exception("商品货品库存增加失败");
                    }
                }
            } catch (Exception ex) {
                txManager.rollback(status);
                logger.info("订单 ID=" + order.getId() + " 数据更新失败，放弃自动确认收货");
                return;
            }
            txManager.commit(status);
            logger.info("订单 ID=" + order.getId() + " 已经超期自动取消订单");
        }
    }

    /**
     * 自动确认订单
     * <p>
     * 定时检查订单未确认情况，如果超时七天则自动确认订单
     * 定时时间是每天凌晨3点。
     * <p>
     * 注意，因为是相隔一天检查，因此导致有订单是超时八天以后才设置自动确认。
     * 这里可以进一步地配合用户订单查询时订单未确认检查，如果订单超时7天则自动确认。
     * 但是，这里可能不是非常必要。相比订单未付款检查中存在商品资源有限所以应该
     * 早点清理未付款情况，这里八天再确认是可以的。
     * <p>
     * TODO
     * 目前自动确认是基于管理后台管理员所设置的商品快递时间，见orderService.queryUnconfirm。
     * 那么在实际业务上有可能存在商品寄出以后商品因为一些原因快递最终没有到达，
     * 也就是商品快递失败而shipEndTime一直是空的情况，因此这里业务可能需要扩展，以防止订单一直
     * 处于发货状态。
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void checkOrderUnconfirm() {
        logger.info("系统开启任务检查订单是否已经超期自动确认收货");

        List<LitemallOrder> orderList = orderService.queryUnconfirm();
        for (LitemallOrder order : orderList) {
            LocalDateTime ship = order.getShipTime();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expired = ship.plusDays(7);
            if (expired.isAfter(now)) {
                continue;
            }

            // 设置订单已取消状态
            order.setOrderStatus(OrderUtil.STATUS_AUTO_CONFIRM);
            order.setConfirmTime(now);
            if (orderService.updateWithOptimisticLocker(order) == 0) {
                logger.info("订单 ID=" + order.getId() + " 数据已经更新，放弃自动确认收货");
            } else {
                logger.info("订单 ID=" + order.getId() + " 已经超期自动确认收货");
            }
        }
    }

    /**
     * 可评价订单商品超期
     * <p>
     * 定时检查订单商品评价情况，如果确认商品超时七天则取消可评价状态
     * 定时时间是每天凌晨4点。
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void checkOrderComment() {
        logger.info("系统开启任务检查订单是否已经超期未评价");

        LocalDateTime now = LocalDateTime.now();
        List<LitemallOrder> orderList = orderService.queryComment();
        for (LitemallOrder order : orderList) {
            LocalDateTime confirm = order.getConfirmTime();
            LocalDateTime expired = confirm.plusDays(7);
            if (expired.isAfter(now)) {
                continue;
            }

            order.setComments((short) 0);
            orderService.updateWithOptimisticLocker(order);

            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                orderGoods.setComment(-1);
                orderGoodsService.updateById(orderGoods);
            }
        }
    }
}
