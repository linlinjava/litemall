package org.linlinjava.litemall.wx.web;

import com.github.binarywang.wxpay.service.WxPayService;
import org.apache.log4j.Logger;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;

@RestController
@RequestMapping("/wx/pay")
public class WxPayController {
    private Logger logger = Logger.getLogger(WxPayController.class);

    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallUserService userService;

    @Autowired
    private WxPayService wxService;


    /**
     * 获取支付的请求参数
     */
    @RequestMapping("prepay")
    public Object payPrepay(@LoginUser Integer userId, Integer orderId) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(orderId == null){
            return ResponseUtil.fail402();
        }

        LitemallOrder order = orderService.findById(orderId);
        LitemallUser user = userService.findById(userId);
        if(user.getWeixinOpenid() == null){
            return ResponseUtil.fail(403, "用户openid不存在");
        }
        if(order == null){
            return ResponseUtil.fail(403, "订单不存在");
        }

        return ResponseUtil.ok("模拟支付成功");
//        if(order.getPayStatus() != 0){
//            return ResponseUtil.fail(403, "订单已支付，请不要重复操作");
//        }

//        WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = new WxPayUnifiedOrderRequest();
//        WxPayUnifiedOrderResult wxPayUnifiedOrderResult = null;
//        try {
//            wxPayUnifiedOrderResult = wxService.unifiedOrder(wxPayUnifiedOrderRequest);
//        } catch (WxPayException e) {
//            e.printStackTrace();
//            return ResponseUtil.fail(403, "支付失败");
//        }
//
//            return ResponseUtil.fail(404, "支付未实现");


    }

    /**
     * 微信订单回调接口
     */
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public Object notify(HttpServletRequest request, HttpServletResponse response) {
        return ResponseUtil.fail501();
    }

    /**
     * 订单退款请求
     */
    @RequestMapping("refund")
    public Object refund(@LoginUser Integer userId, Integer orderId) {
        if (userId == null) {
            return ResponseUtil.fail401();
        }
        if (orderId == null) {
            return ResponseUtil.fail402();
        }
        return ResponseUtil.fail501();

    }

}