package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.db.domain.LitemallFootprint;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.service.LitemallFootprintService;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.linlinjava.litemall.db.util.JacksonUtil;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/footprint")
public class WxFootprintController {
    @Autowired
    private LitemallFootprintService footprintService;
    @Autowired
    private LitemallGoodsService goodsService;

    /**
     */
    @RequestMapping("delete")
    public Object delete(@LoginUser Integer userId, @RequestBody String body) {
        if(userId == null){
            return ResponseUtil.fail401();
        }
        if(body == null){
            return ResponseUtil.fail402();
        }

        Integer footprintId = JacksonUtil.parseInteger(body, "footprintId");
        if(footprintId == null){
            return ResponseUtil.fail403();
        }
        LitemallFootprint footprint = footprintService.findById(footprintId);

        if(footprint == null){
            return ResponseUtil.fail403();
        }
        if(!footprint.getUserId().equals(userId)){
            return ResponseUtil.fail403();
        }

        footprintService.deleteById(footprintId);
        return ResponseUtil.ok();
    }

    /**
     */
    @RequestMapping("list")
    public Object list(@LoginUser Integer userId,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if(userId == null){
            return ResponseUtil.fail401();
        }

        List<LitemallFootprint> footprintList = footprintService.queryByAddTime(userId, page, size);
        int count = footprintService.countByAddTime(userId, page, size);
        int totalPages = (int) Math.ceil((double) count / size);

        List<Object> footprintVoList = new ArrayList<>(footprintList.size());
        for(LitemallFootprint footprint : footprintList){
            Map<String, Object> c = new HashMap();
            c.put("id", footprint.getId());
            c.put("goodsId", footprint.getGoodsId());
            c.put("addTime", footprint.getAddTime());

            LitemallGoods goods = goodsService.findById(footprint.getGoodsId());
            c.put("name", goods.getName());
            c.put("goodsBrief", goods.getGoodsBrief());
            c.put("listPicUrl", goods.getListPicUrl());
            c.put("retailPrice", goods.getRetailPrice());

            footprintVoList.add(c);
        }


        Map<String, Object> result = new HashMap();
        result.put("footprintList", footprintVoList);
        result.put("totalPages", totalPages);
        return ResponseUtil.ok(result);
    }

}