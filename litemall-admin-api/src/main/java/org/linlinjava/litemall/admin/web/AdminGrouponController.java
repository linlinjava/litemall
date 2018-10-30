package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallGroupon;
import org.linlinjava.litemall.db.domain.LitemallGrouponRules;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.linlinjava.litemall.db.service.LitemallGrouponRulesService;
import org.linlinjava.litemall.db.service.LitemallGrouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/groupon")
@Validated
public class AdminGrouponController {
    private final Log logger = LogFactory.getLog(AdminGrouponController.class);

    @Autowired
    private LitemallGrouponRulesService rulesService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallGrouponService grouponService;

    @GetMapping("/listRecord")
    public Object listRecord(@LoginAdmin Integer adminId,
                             String grouponId,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @Sort @RequestParam(defaultValue = "add_time") String sort,
                             @Order @RequestParam(defaultValue = "desc") String order) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        List<LitemallGroupon> grouponList = grouponService.querySelective(grouponId, page, limit, sort, order);
        int total = grouponService.countSelective(grouponId, page, limit, sort, order);

        List<Map<String, Object>> records = new ArrayList<>();
        for (LitemallGroupon groupon : grouponList) {
            try {
                Map<String, Object> RecordData = new HashMap<>();
                List<LitemallGroupon> subGrouponList = grouponService.queryJoinRecord(groupon.getId());
                LitemallGrouponRules rules = rulesService.queryById(groupon.getRulesId());
                LitemallGoods goods = goodsService.findById(rules.getGoodsId());

                RecordData.put("groupon", groupon);
                RecordData.put("subGroupons", subGrouponList);
                RecordData.put("rules", rules);
                RecordData.put("goods", goods);

                records.add(RecordData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", records);

        return ResponseUtil.ok(data);
    }

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String goodsId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        List<LitemallGrouponRules> rulesList = rulesService.querySelective(goodsId, page, limit, sort, order);
        int total = rulesService.countSelective(goodsId, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", rulesList);

        return ResponseUtil.ok(data);
    }

    private Object validate(LitemallGrouponRules grouponRules) {
        Integer goodsId = grouponRules.getGoodsId();
        if(goodsId == null){
            return ResponseUtil.badArgument();
        }
        BigDecimal discount = grouponRules.getDiscount();
        if(discount == null){
            return ResponseUtil.badArgument();
        }
        Integer discountMember = grouponRules.getDiscountMember();
        if(discountMember == null){
            return ResponseUtil.badArgument();
        }
        LocalDateTime expireTime = grouponRules.getExpireTime();
        if(expireTime == null){
            return ResponseUtil.badArgument();
        }

        return null;
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallGrouponRules grouponRules) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        Object error = validate(grouponRules);
        if(error != null){
            return error;
        }

        Integer goodsId = grouponRules.getGoodsId();
        LitemallGoods goods = goodsService.findById(goodsId);
        if (goods == null) {
            return ResponseUtil.badArgumentValue();
        }

        grouponRules.setGoodsName(goods.getName());
        grouponRules.setPicUrl(goods.getPicUrl());

        if(rulesService.updateById(grouponRules) == 0){
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok();
    }


    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallGrouponRules grouponRules) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        Object error = validate(grouponRules);
        if(error != null){
            return error;
        }

        Integer goodsId = grouponRules.getGoodsId();
        LitemallGoods goods = goodsService.findById(goodsId);
        if (goods == null) {
            return ResponseUtil.badArgumentValue();
        }

        grouponRules.setAddTime(LocalDateTime.now());
        grouponRules.setGoodsName(goods.getName());
        grouponRules.setPicUrl(goods.getPicUrl());

        rulesService.createRules(grouponRules);

        return ResponseUtil.ok(grouponRules);
    }


    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallGrouponRules grouponRules) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        Integer id = grouponRules.getId();
        if(id == null){
            return ResponseUtil.badArgument();
        }

        rulesService.delete(id);
        return ResponseUtil.ok();
    }
}
