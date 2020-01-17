package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.domain.LitemallUserFormid;
import org.linlinjava.litemall.db.service.LitemallUserFormIdService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/wx/formid")
@Validated
public class WxUserFormId {
    private final Log logger = LogFactory.getLog(WxUserFormId.class);

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LitemallUserFormIdService formIdService;

    @GetMapping("create")
    public Object create(@LoginUser Integer userId, @NotNull String formId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        LitemallUser user = userService.findById(userId);
        LitemallUserFormid userFormid = new LitemallUserFormid();
        userFormid.setOpenid(user.getWeixinOpenid());
        userFormid.setFormid(formId);
        userFormid.setIsprepay(false);
        userFormid.setUseamount(1);
        userFormid.setExpireTime(LocalDateTime.now().plusDays(7));
        formIdService.addUserFormid(userFormid);

        return ResponseUtil.ok();
    }
}
