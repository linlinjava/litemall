package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.db.domain.LitemallFootprint;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.service.LitemallFootprintService;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/footprint")
@Validated
public class WxFootprintController {
    @Autowired
    private LitemallFootprintService footprintService;
    @Autowired
    private LitemallGoodsService goodsService;

    /**
     * 删除用户足迹
     *
     * @param userId 用户ID
     * @param body 请求内容， { footprintId: xxx }
     * @return 删除操作结果
     *   成功则 { errno: 0, errmsg: '成功' }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("delete")
    public Object delete(@LoginUser Integer userId, @RequestBody String body) {
        if(userId == null){
            return ResponseUtil.unlogin();
        }
        if(body == null){
            return ResponseUtil.badArgument();
        }

        Integer footprintId = JacksonUtil.parseInteger(body, "footprintId");
        if(footprintId == null){
            return ResponseUtil.badArgument();
        }
        LitemallFootprint footprint = footprintService.findById(footprintId);

        if(footprint == null){
            return ResponseUtil.badArgumentValue();
        }
        if(!footprint.getUserId().equals(userId)){
            return ResponseUtil.badArgumentValue();
        }

        footprintService.deleteById(footprintId);
        return ResponseUtil.ok();
    }

    /**
     * 用户足迹列表
     *
     * @param page 分页页数
     * @param size 分页大小
     * @return 用户足迹列表
     *   成功则
     *  {
     *      errno: 0,
     *      errmsg: '成功',
     *      data:
     *          {
     *              footprintList: xxx,
     *              totalPages: xxx
     *          }
     *  }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("list")
    public Object list(@LoginUser Integer userId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size) {
        if(userId == null){
            return ResponseUtil.unlogin();
        }

        List<LitemallFootprint> footprintList = footprintService.queryByAddTime(userId, page, size);
        int count = footprintService.countByAddTime(userId, page, size);
        int totalPages = (int) Math.ceil((double) count / size);

        List<Object> footprintVoList = new ArrayList<>(footprintList.size());
        for(LitemallFootprint footprint : footprintList){
            Map<String, Object> c = new HashMap<String, Object>();
            c.put("id", footprint.getId());
            c.put("goodsId", footprint.getGoodsId());
            c.put("addTime", footprint.getAddTime());

            LitemallGoods goods = goodsService.findById(footprint.getGoodsId());
            c.put("name", goods.getName());
            c.put("brief", goods.getBrief());
            c.put("picUrl", goods.getPicUrl());
            c.put("retailPrice", goods.getRetailPrice());

            footprintVoList.add(c);
        }


        Map<String, Object> result = new HashMap<>();
        result.put("footprintList", footprintVoList);
        result.put("totalPages", totalPages);
        return ResponseUtil.ok(result);
    }

}