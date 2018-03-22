package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.db.domain.LitemallCollect;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.service.LitemallCollectService;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.linlinjava.litemall.db.util.JacksonUtil;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/collect")
public class WxCollectController {
    @Autowired
    private LitemallCollectService collectService;
    @Autowired
    private LitemallGoodsService goodsService;

    /**
     * 获取用户收藏
     */
    @RequestMapping("list")
    public Object list(@LoginUser Integer userId, Integer typeId) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(typeId == null){
            return ResponseUtil.fail402();
        }


        List<LitemallCollect> collectList = collectService.queryByType(userId, typeId);
        int count = collectService.countByType(userId, typeId);

        List<Object> collects = new ArrayList<>(collectList.size());
        for(LitemallCollect collect : collectList){
            Map<String, Object> c = new HashMap();
            c.put("id", collect.getId());
            c.put("typeId", collect.getTypeId());
            c.put("valueId", collect.getValueId());

            LitemallGoods goods = goodsService.findById(collect.getValueId());
            c.put("name", goods.getName());
            c.put("goodsBrief", goods.getGoodsBrief());
            c.put("listPicUrl", goods.getListPicUrl());
            c.put("retailPrice", goods.getRetailPrice());

            collects.add(c);
        }

        Map<String, Object> result = new HashMap();
        result.put("count", count);
        result.put("data", collects);
        return ResponseUtil.ok(result);
    }

    /**
     * 获取用户收藏
     */
    @RequestMapping("addordelete")
    public Object addordelete(@LoginUser Integer userId, @RequestBody String body) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(body == null){
            return ResponseUtil.fail402();
        }

        Integer typeId = JacksonUtil.parseInteger(body, "typeId");
        Integer valueId = JacksonUtil.parseInteger(body, "valueId");
        LitemallCollect collect = collectService.queryByTypeAndValue(userId, typeId, valueId);

        String handleType = null;
        if(collect != null){
            handleType = "delete";
            collectService.deleteById(collect.getId());
        }
        else{
            handleType = "add";
            collect = new LitemallCollect();
            collect.setUserId(userId);
            collect.setValueId(valueId);
            collect.setTypeId(typeId);
            collect.setAddTime(LocalDate.now());
            collectService.add(collect);
        }

        Map<String, Object> data = new HashMap();
        data.put("type", handleType);
        return ResponseUtil.ok(data);
    }
}