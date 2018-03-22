package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.domain.LitemallBrand;
import org.linlinjava.litemall.db.service.LitemallBrandService;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/brand")
public class WxBrandController {
    private final Log logger = LogFactory.getLog(WxBrandController.class);

    @Autowired
    private LitemallBrandService brandService;

    /**
     * 分页获取品牌
     */
    @RequestMapping("list")
    public Object list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {

        List<LitemallBrand> brandList = brandService.query(page, size);
        int total = brandService.queryTotalCount();
        int totalPages = (int) Math.ceil((double) total / size);

        Map<String, Object> data = new HashMap();
        data.put("brandList", brandList);
        data.put("totalPages", totalPages);
        return ResponseUtil.ok(data);
    }

    /**
     * 品牌详情
     */
    @RequestMapping("detail")
    public Object detail(Integer id) {
        if(id == null){
            return ResponseUtil.fail402();
        }

        LitemallBrand entity = brandService.findById(id);

        if(entity == null){
            return ResponseUtil.fail403();
        }
        Map<String, Object> data = new HashMap();
        data.put("brand",entity);
        return ResponseUtil.ok(data);
    }
}