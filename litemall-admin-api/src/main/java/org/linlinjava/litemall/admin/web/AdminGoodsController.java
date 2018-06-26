package org.linlinjava.litemall.admin.web;

import io.swagger.models.auth.In;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.admin.dao.GoodsAllinone;
import org.linlinjava.litemall.admin.dao.Product;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallGoodsAttribute;
import org.linlinjava.litemall.db.domain.LitemallGoodsSpecification;
import org.linlinjava.litemall.db.domain.LitemallProduct;
import org.linlinjava.litemall.db.service.LitemallGoodsAttributeService;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.service.LitemallGoodsSpecificationService;
import org.linlinjava.litemall.db.service.LitemallProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/goods")
public class AdminGoodsController {
    private final Log logger = LogFactory.getLog(AdminGoodsController.class);

    @Autowired
    private PlatformTransactionManager txManager;

    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallGoodsSpecificationService specificationService;
    @Autowired
    private LitemallGoodsAttributeService attributeService;
    @Autowired
    private LitemallProductService productService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String goodsSn, String name,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        List<LitemallGoods> goodsList = goodsService.querySelective(goodsSn, name, page, limit, sort, order);
        int total = goodsService.countSelective(goodsSn, name, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", goodsList);

        return ResponseUtil.ok(data);
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallGoods goods){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        goodsService.add(goods);
        return ResponseUtil.ok(goods);
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        if(id == null){
            return ResponseUtil.badArgument();
        }

        LitemallGoods goods = goodsService.findById(id);
        return ResponseUtil.ok(goods);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallGoods goods){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        goodsService.updateById(goods);
        return ResponseUtil.ok(goods);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallGoods goods){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        goodsService.deleteById(goods.getId());
        return ResponseUtil.ok();
    }

    @PostMapping("/publish")
    public Object publish(@LoginAdmin Integer adminId, @RequestBody GoodsAllinone goodsAllinone){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        LitemallGoods goods = goodsAllinone.getGoods();
        LitemallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        LitemallGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        Product[] products = goodsAllinone.getProducts();

        String name = goods.getName();
        if(goodsService.checkExistByName(name)){
            return ResponseUtil.fail(403, "商品名已经存在");
        }

        // 开启事务管理
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        try {

            // 商品基本信息表litemall_goods
            goods.setAddTime(LocalDateTime.now());
            goodsService.add(goods);

            // 商品规格表litemall_goods_specification
            Map<String, Integer> specIds = new HashMap<>();
            for(LitemallGoodsSpecification specification : specifications){
                specification.setGoodsId(goods.getId());
                specification.setAddTime(LocalDateTime.now());
                specificationService.add(specification);
                specIds.put(specification.getValue(), specification.getId());
            }

            // 商品参数表litemall_goods_attribute
            for(LitemallGoodsAttribute attribute : attributes){
                attribute.setGoodsId(goods.getId());
                attribute.setAddTime(LocalDateTime.now());
                attributeService.add(attribute);
            }

            // 商品货品表litemall_product
            for(Product product : products){
                LitemallProduct litemallProduct = new LitemallProduct();
                litemallProduct.setRetailPrice(product.getPrice());
                litemallProduct.setGoodsNumber(product.getNumber());
                litemallProduct.setUrl(product.getUrl());
                litemallProduct.setGoodsId(goods.getId());
                litemallProduct.setAddTime(LocalDateTime.now());

                String[] values = product.getSpecifications();
                Integer[] ids = new Integer[values.length];
                for(int i = 0; i < values.length; i++){
                    ids[i] = specIds.get(values[i]);
                }
                Arrays.sort(ids);
                litemallProduct.setGoodsSpecificationIds(ids);

                productService.add(litemallProduct);
            }
        } catch (Exception ex) {
            txManager.rollback(status);
            logger.error("系统内部错误", ex);
        }
        txManager.commit(status);

        return ResponseUtil.ok();
    }
}
