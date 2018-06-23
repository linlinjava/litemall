package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallProduct;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.linlinjava.litemall.db.service.LitemallGoodsSpecificationService;
import org.linlinjava.litemall.db.service.LitemallProductService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/product")
public class AdminProductController {
    private final Log logger = LogFactory.getLog(AdminProductController.class);

    @Autowired
    private LitemallProductService productService;
    @Autowired
    private LitemallGoodsService goodsService;
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

        List<LitemallProduct> productList = productService.querySelective(goodsId, page, limit, sort, order);
        int total = productService.countSelective(goodsId, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", productList);

        return ResponseUtil.ok(data);
    }

    /**
     *
     * @param adminId
     * @param litemallProduct
     * @return
     */
    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallProduct litemallProduct){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        Integer goodsId = litemallProduct.getGoodsId();
        if(goodsId == null){
            return ResponseUtil.badArgument();
        }

        LitemallGoods goods = goodsService.findById(goodsId);
        if(goods == null){
            return ResponseUtil.badArgumentValue();
        }

        List<LitemallProduct> productList = productService.queryByGid(goodsId);
        if(productList.size() != 0){
            return ResponseUtil.badArgumentValue();
        }

        Integer[] goodsSpecificationIds = goodsSpecificationService.queryIdsByGid(goodsId);
        if(goodsSpecificationIds.length == 0) {
            return ResponseUtil.serious();
        }

        LitemallProduct product = new LitemallProduct();
        product.setGoodsId(goodsId);
        product.setGoodsNumber(0);
        product.setRetailPrice(new BigDecimal(0.00));
        product.setGoodsSpecificationIds(goodsSpecificationIds);
        product.setAddTime(LocalDateTime.now());
        productService.add(product);

        return ResponseUtil.ok();
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        if(id == null){
            return ResponseUtil.badArgument();
        }

        LitemallProduct product = productService.findById(id);
        return ResponseUtil.ok(product);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallProduct product){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        productService.updateById(product);
        return ResponseUtil.ok(product);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallProduct product){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        productService.deleteById(product.getId());
        return ResponseUtil.ok();
    }

}
