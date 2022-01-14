package org.linlinjava.litemall.wx.web;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaKefuMessage;
import cn.binarywang.wx.miniapp.bean.WxMaMessage;
import cn.binarywang.wx.miniapp.message.WxMaXmlOutMessage;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author liyang
 * @Description 微信消息推送配置
 * @date 2021-11-10 17:32:25
 */
@RestController
@RequestMapping(value = "/wx/msg")
public class WxMsgController {

    private final Log logger = LogFactory.getLog(WxMsgController.class);

    @Autowired
    private WxMaService wxMaService;

    /**
     * token校验
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @GetMapping(value = "/config", produces = "text/plain;charset=utf-8")
    public String config(@RequestParam(required = false) String signature,
                         @RequestParam(required = false) String timestamp,
                         @RequestParam(required = false) String nonce,
                         @RequestParam(required = false) String echostr) {
        return !wxMaService.checkSignature(timestamp, nonce, signature) ? "fail" : echostr;
    }

    /**
     * 消息回复
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/config", produces = "application/xml;charset=utf-8")
    public String config(HttpServletRequest request, HttpServletResponse response) {
        WxMaMessage wxMaMessage = null;
        try {
            wxMaMessage = WxMaMessage.fromXml(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (wxMaMessage != null) {
            String msgType = wxMaMessage.getMsgType();
            if ("text".equals(msgType)) {
                try {
                    wxMaService.getMsgService().sendKefuMsg(WxMaKefuMessage.newTextBuilder().content(wxMaMessage.getContent()).toUser(wxMaMessage.getFromUser()).build());
                } catch (WxErrorException e) {
                    logger.error("消息自动回复失败");
                }
            }
            WxMaXmlOutMessage wxMaXmlOutMessage = new WxMaXmlOutMessage();
            wxMaXmlOutMessage.setMsgType("transfer_customer_service");
            wxMaXmlOutMessage.setToUserName(wxMaMessage.getFromUser());
            wxMaXmlOutMessage.setFromUserName(wxMaMessage.getToUser());
            wxMaXmlOutMessage.setCreateTime(wxMaMessage.getCreateTime().longValue());
            final String xml = wxMaXmlOutMessage.toXml();
            logger.info(xml);
            return xml;
        }
        return null;
    }

}
