package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.core.qcode.QCodeService;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallGroupon;
import org.linlinjava.litemall.db.domain.LitemallGrouponRules;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.linlinjava.litemall.db.service.LitemallGrouponRulesService;
import org.linlinjava.litemall.db.service.LitemallGrouponService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/groupon")
@Validated
public class WxGrouponController {
    @Autowired
    private LitemallGrouponRulesService rulesService;
    @Autowired
    private LitemallGrouponService grouponService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private QCodeService qCodeService;

    @GetMapping("detail")
    public Object detail(@LoginUser Integer userId, @NotNull Integer grouponId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        LitemallGroupon groupon = grouponService.queryById(grouponId);
        if (groupon == null) {
            return ResponseUtil.badArgumentValue();
        }

        LitemallGrouponRules rules = rulesService.queryById(groupon.getRulesId());
        if (rules == null) {
            return ResponseUtil.badArgumentValue();
        }

        LitemallGoods goods = goodsService.findById(rules.getGoodsId());

        int count = grouponService.countGroupon(grouponId);

        Map<String, Object> groupInfo = new HashMap<>();
        groupInfo.put("groupon", groupon);
        groupInfo.put("rules", rules);
        groupInfo.put("goods", goods);
        groupInfo.put("count", count);

        return ResponseUtil.ok(groupInfo);
    }

    @GetMapping("query")
    public Object query(@NotNull Integer goodsId) {
        LitemallGoods goods = goodsService.findById(goodsId);
        if (goods == null) {
            return ResponseUtil.fail(-1, "未找到对应的商品");
        }

        List<LitemallGrouponRules> rules = rulesService.queryByGoodsId(goodsId);

        return ResponseUtil.ok(rules);
    }

    @RequestMapping("join")
    public Object join(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        Integer rulesId = JacksonUtil.parseInteger(body, "rulesId");
        LitemallGrouponRules rules = rulesService.queryById(rulesId);
        if (rules == null) {
            return ResponseUtil.fail(-1, "未找到对应的团购规则");
        }

        Integer grouponId = JacksonUtil.parseInteger(body, "grouponId");
        LitemallGroupon groupon = grouponService.queryById(grouponId);
        if (groupon == null) {
            return ResponseUtil.fail(-1, "未找到对应的团购活动");
        }

        LitemallGroupon groupon2 = new LitemallGroupon();
        groupon2.setUserId(userId);
        groupon2.setRulesId(rulesId);
        groupon2.setShareUrl("");
        //参与者
        groupon2.setUserType(false);
        groupon2.setGrouponId(grouponId);

        groupon2.setAddTime(LocalDateTime.now());
        //过期时间与创建一致
        groupon2.setExpireTime(groupon.getExpireTime());

        grouponService.createGroupon(groupon);

        return ResponseUtil.ok();
    }


    @GetMapping("create")
    public Object create(@LoginUser Integer userId, @NotNull Integer rulesId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        LitemallGrouponRules rules = rulesService.queryById(rulesId);
        if (rules == null) {
            return ResponseUtil.fail(-1, "未找到对应的团购规则");
        }

        LitemallGroupon groupon = new LitemallGroupon();
        groupon.setUserId(userId);
        groupon.setRulesId(rulesId);
        //发起者
        groupon.setUserType(true);
        groupon.setGrouponId(0);

        groupon.setAddTime(LocalDateTime.now());
        groupon.setExpireTime(LocalDateTime.now().plusDays(2));

        grouponService.createGroupon(groupon);

        qCodeService.createGrouponShareImage(rules.getGoodsName(), rules.getPicUrl(), groupon);
        groupon.setShareUrl(qCodeService.getShareImageUrl(groupon.getId().toString()));
        grouponService.update(groupon);


        return ResponseUtil.ok();
    }

    @RequestMapping("list")
    public Object list(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        List<LitemallGroupon> myGroupOn = grouponService.queryByUserId(userId);

        return ResponseUtil.ok(myGroupOn);
    }
}
