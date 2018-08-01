package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallAd;
import org.linlinjava.litemall.db.service.LitemallAdService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/ad")
@Validated
public class AdminAdController {
    private final Log logger = LogFactory.getLog(AdminAdController.class);

    @Autowired
    private LitemallAdService adService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String name, String content,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order){
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
        ad.setAddTime(LocalDateTime.now());
        adService.add(ad);
        return ResponseUtil.ok(ad);
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, @NotNull Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
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
