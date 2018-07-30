package org.linlinjava.litemall.core.notify;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import org.linlinjava.litemall.db.domain.LitemallUserFormid;
import org.linlinjava.litemall.db.service.LitemallUserFormIdService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信模版消息通知
 */
public class WxTemplateSender {
    @Autowired
    WxMaService wxMaService;

    @Autowired
    LitemallUserFormIdService formIdService;

    /**
     * 发送微信消息(模板消息),不带跳转
     *
     * @param touser    用户 OpenID
     * @param templatId 模板消息ID
     * @param parms     详细内容
     */
    public void sendWechatMsg(String touser, String templatId, String[] parms) {
        sendMsg(touser, templatId, parms, "", "", "");
    }

    /**
     * 发送微信消息(模板消息),带跳转
     *
     * @param touser    用户 OpenID
     * @param templatId 模板消息ID
     * @param parms     详细内容
     * @param page      跳转页面
     */
    public void sendWechatMsg(String touser, String templatId, String[] parms, String page) {
        sendMsg(touser, templatId, parms, page, "", "");
    }

    private void sendMsg(String touser, String templatId, String[] parms, String page, String color, String emphasisKeyword) {
        LitemallUserFormid userFormid = formIdService.queryByOpenId(touser);
        if (userFormid == null)
            return;


        WxMaTemplateMessage msg = new WxMaTemplateMessage();
        msg.setTemplateId(templatId);
        msg.setToUser(touser);
        msg.setFormId(userFormid.getFormid());
        msg.setPage(page);
        msg.setColor(color);
        msg.setEmphasisKeyword(emphasisKeyword);
        msg.setData(createMsgData(parms));

        try {
            wxMaService.getMsgService().sendTemplateMsg(msg);
            formIdService.updateUserFormId(userFormid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<WxMaTemplateMessage.Data> createMsgData(String[] parms) {
        List<WxMaTemplateMessage.Data> dataList = new ArrayList<>();
        for (int i = 1; i <= parms.length; i++) {
            dataList.add(new WxMaTemplateMessage.Data("keyword" + i, parms[i - 1]));
        }

        return dataList;
    }
}
