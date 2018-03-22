package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.db.domain.LitemallAd;
import org.linlinjava.litemall.db.service.LitemallAdService;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/ad")
public class AdController {
    private final Log logger = LogFactory.getLog(AdController.class);

    @Autowired
    private LitemallAdService adService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String name, String content,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        List<LitemallAd> adList = adService.querySelective(name, content, page, limit, sort, order);
        int total = adService.countSelective(name, content, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", adList);

        return ResponseUtil.ok(data);
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallAd ad){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        adService.add(ad);
        return ResponseUtil.ok(ad);
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        if(id == null){
            return ResponseUtil.badArgument();
        }

        LitemallAd brand = adService.findById(id);
        return ResponseUtil.ok(brand);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallAd ad){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        adService.updateById(ad);
        return ResponseUtil.ok(ad);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallAd ad){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        adService.deleteById(ad.getId());
        return ResponseUtil.ok();
    }

}
