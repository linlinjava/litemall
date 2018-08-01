package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.domain.LitemallBrand;
import org.linlinjava.litemall.db.service.LitemallBrandService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/brand")
@Validated
public class WxBrandController {
    private final Log logger = LogFactory.getLog(WxBrandController.class);

    @Autowired
    private LitemallBrandService brandService;

    /**
     * 品牌列表
     *
     * @param page 分页页数
     * @param size 分页大小
     * @return 品牌列表
     *   成功则
     *  {
     *      errno: 0,
     *      errmsg: '成功',
     *      data:
     *          {
     *              brandList: xxx,
     *              totalPages: xxx
     *          }
     *  }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("list")
    public Object list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size) {

        List<LitemallBrand> brandList = brandService.query(page, size);
        int total = brandService.queryTotalCount();
        int totalPages = (int) Math.ceil((double) total / size);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("brandList", brandList);
        data.put("totalPages", totalPages);
        return ResponseUtil.ok(data);
    }

    /**
     * 品牌详情
     *
     * @param id 品牌ID
     * @return 品牌详情
     *   成功则
     *  {
     *      errno: 0,
     *      errmsg: '成功',
     *      data:
     *          {
     *              brand: xxx
     *          }
     *  }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("detail")
    public Object detail(@NotNull Integer id) {
        LitemallBrand entity = brandService.findById(id);
        if(entity == null){
            return ResponseUtil.badArgumentValue();
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("brand",entity);
        return ResponseUtil.ok(data);
    }
}