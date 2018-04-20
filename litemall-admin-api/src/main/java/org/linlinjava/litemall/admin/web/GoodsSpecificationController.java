package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.db.domain.LitemallGoodsSpecification;
import org.linlinjava.litemall.db.service.LitemallGoodsSpecificationService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/goods-specification")
public class GoodsSpecificationController {
    private final Log logger = LogFactory.getLog(GoodsSpecificationController.class);

    @Autowired
    private LitemallGoodsSpecificationService goodsSpecificationService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       Integer goodsId,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        List<LitemallGoodsSpecification> goodsSpecificationList = goodsSpecificationService.querySelective(goodsId, page, limit, sort, order);
        int total = goodsSpecificationService.countSelective(goodsId, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", goodsSpecificationList);

        return ResponseUtil.ok(data);
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallGoodsSpecification goodsSpecification){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        goodsSpecificationService.add(goodsSpecification);
        return ResponseUtil.ok(goodsSpecification);
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        if(id == null){
            return ResponseUtil.badArgument();
        }

        LitemallGoodsSpecification goodsSpecification = goodsSpecificationService.findById(id);
        return ResponseUtil.ok(goodsSpecification);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallGoodsSpecification goodsSpecification){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        goodsSpecificationService.updateById(goodsSpecification);
        return ResponseUtil.ok(goodsSpecification);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallGoodsSpecification goodsSpecification){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        goodsSpecificationService.deleteById(goodsSpecification.getId());
        return ResponseUtil.ok();
    }

    @GetMapping("/volist")
    public Object volist(@LoginAdmin Integer adminId, Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        if(id == null){
            return ResponseUtil.badArgument();
        }

        Object goodsSpecificationVoList = goodsSpecificationService.getSpecificationVoList(id);
        return ResponseUtil.ok(goodsSpecificationVoList);
    }

}
