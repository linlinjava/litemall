package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.admin.dao.GoodsAllinone;
import org.linlinjava.litemall.admin.util.CatVo;
import org.linlinjava.litemall.core.qcode.QCodeService;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin/goods")
@Validated
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
    @Autowired
    private LitemallCategoryService categoryService;
    @Autowired
    private LitemallBrandService brandService;

    @Autowired
    private QCodeService qCodeService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String goodsSn, String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        List<LitemallGoods> goodsList = goodsService.querySelective(goodsSn, name, page, limit, sort, order);
        int total = goodsService.countSelective(goodsSn, name, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", goodsList);

        return ResponseUtil.ok(data);
    }

    private Object validate(GoodsAllinone goodsAllinone) {
        LitemallGoods goods = goodsAllinone.getGoods();
        String name = goods.getName();
        if(StringUtils.isEmpty(name)){
            return ResponseUtil.badArgument();
        }
        String goodsSn = goods.getGoodsSn();
        if(StringUtils.isEmpty(goodsSn)){
            return ResponseUtil.badArgument();
        }
        Integer brandId = goods.getBrandId();
        if(brandId == null){
            return ResponseUtil.badArgument();
        }
        if(brandService.findById(brandId) == null) {
            return ResponseUtil.badArgumentValue();
        }
        Integer categoryId = goods.getCategoryId();
        if(categoryId == null){
            return ResponseUtil.badArgument();
        }
        if(categoryService.findById(categoryId) == null){
            return ResponseUtil.badArgumentValue();
        }

        LitemallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        for(LitemallGoodsAttribute attribute : attributes){
            String attr = attribute.getAttribute();
            if(StringUtils.isEmpty(attr)){
                return ResponseUtil.badArgument();
            }
            String value = attribute.getValue();
            if(StringUtils.isEmpty(value)){
                return ResponseUtil.badArgument();
            }
        }

        LitemallGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        for(LitemallGoodsSpecification specification : specifications){
            String spec = specification.getSpecification();
            if(StringUtils.isEmpty(spec)){
                return ResponseUtil.badArgument();
            }
            String value = specification.getValue();
            if(StringUtils.isEmpty(value)){
                return ResponseUtil.badArgument();
            }
        }

        LitemallProduct[] products = goodsAllinone.getProducts();
        for(LitemallProduct product : products){
            Integer number = product.getNumber();
            if(number == null || number < 0){
                return ResponseUtil.badArgument();
            }

            BigDecimal price = product.getPrice();
            if(price == null){
                return ResponseUtil.badArgument();
            }

            String[] productSpecifications = product.getSpecifications();
            if(productSpecifications.length == 0){
                return ResponseUtil.badArgument();
            }
        }

        return null;
    }

    /*
     * TODO
     * 目前商品修改的逻辑是
     * 1. 更新litemall_goods表
     * 2. 逻辑删除litemall_goods_specification、litemall_goods_attribute、litemall_product
     * 3. 添加litemall_goods_specification、litemall_goods_attribute、litemall_product
     *
     * 这里商品三个表的数据采用删除再跟新的策略是因为
     * 商品编辑页面，管理员可以添加删除商品规格、添加删除商品属性，因此这里仅仅更新表是不可能的，
     * 因此这里只能删除所有旧的数据，然后添加新的数据
     */
    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody GoodsAllinone goodsAllinone) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        Object error = validate(goodsAllinone);
        if(error != null){
            return error;
        }

        LitemallGoods goods = goodsAllinone.getGoods();
        LitemallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        LitemallGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        LitemallProduct[] products = goodsAllinone.getProducts();

        // 开启事务管理
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        try {

            //将生成的分享图片地址写入数据库
            String url = qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());
            goods.setShareUrl(url);

            // 商品基本信息表litemall_goods
            if(goodsService.updateById(goods) == 0){
                throw new Exception("跟新数据已失效");
            }

            Integer gid = goods.getId();
            specificationService.deleteByGid(gid);
            attributeService.deleteByGid(gid);
            productService.deleteByGid(gid);

            // 商品规格表litemall_goods_specification
            for (LitemallGoodsSpecification specification : specifications) {
                specification.setGoodsId(goods.getId());
                specification.setAddTime(LocalDateTime.now());
                specificationService.add(specification);
            }

            // 商品参数表litemall_goods_attribute
            for (LitemallGoodsAttribute attribute : attributes) {
                attribute.setGoodsId(goods.getId());
                attribute.setAddTime(LocalDateTime.now());
                attributeService.add(attribute);
            }

            // 商品货品表litemall_product
            for (LitemallProduct product : products) {
                product.setGoodsId(goods.getId());
                product.setAddTime(LocalDateTime.now());
                productService.add(product);
            }
        } catch (Exception ex) {
            txManager.rollback(status);
            logger.error("系统内部错误", ex);
            return ResponseUtil.fail();
        }
        txManager.commit(status);

        qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());

        return ResponseUtil.ok();
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallGoods goods) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }
        Integer id = goods.getId();
        if(id == null){
            return ResponseUtil.badArgument();
        }

        // 开启事务管理
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        try {

            Integer gid = goods.getId();
            goodsService.deleteById(gid);
            specificationService.deleteByGid(gid);
            attributeService.deleteByGid(gid);
            productService.deleteByGid(gid);
        } catch (Exception ex) {
            txManager.rollback(status);
            logger.error("系统内部错误", ex);
            return ResponseUtil.fail();
        }
        txManager.commit(status);
        return ResponseUtil.ok();
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody GoodsAllinone goodsAllinone) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        Object error = validate(goodsAllinone);
        if(error != null){
            return error;
        }

        LitemallGoods goods = goodsAllinone.getGoods();
        LitemallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        LitemallGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        LitemallProduct[] products = goodsAllinone.getProducts();

        String name = goods.getName();
        if (goodsService.checkExistByName(name)) {
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

            //将生成的分享图片地址写入数据库
            String url = qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());
            if(!StringUtils.isEmpty(url)) {
                goods.setShareUrl(url);
                if (goodsService.updateById(goods) == 0) {
                    throw new Exception("跟新数据已失效");
                }
            }

            // 商品规格表litemall_goods_specification
            for (LitemallGoodsSpecification specification : specifications) {
                specification.setGoodsId(goods.getId());
                specification.setAddTime(LocalDateTime.now());
                specificationService.add(specification);
            }

            // 商品参数表litemall_goods_attribute
            for (LitemallGoodsAttribute attribute : attributes) {
                attribute.setGoodsId(goods.getId());
                attribute.setAddTime(LocalDateTime.now());
                attributeService.add(attribute);
            }

            // 商品货品表litemall_product
            for (LitemallProduct product : products) {
                product.setGoodsId(goods.getId());
                product.setAddTime(LocalDateTime.now());
                productService.add(product);
            }
        } catch (Exception ex) {
            txManager.rollback(status);
            logger.error("系统内部错误", ex);
            return ResponseUtil.fail();
        }
        txManager.commit(status);

        return ResponseUtil.ok();
    }


    @GetMapping("/catAndBrand")
    public Object list2(@LoginAdmin Integer adminId) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        // http://element-cn.eleme.io/#/zh-CN/component/cascader
        // 管理员设置“所属分类”
        List<LitemallCategory> l1CatList = categoryService.queryL1();
        List<CatVo> categoryList = new ArrayList<>(l1CatList.size());

        for (LitemallCategory l1 : l1CatList) {
            CatVo l1CatVo = new CatVo();
            l1CatVo.setValue(l1.getId());
            l1CatVo.setLabel(l1.getName());

            List<LitemallCategory> l2CatList = categoryService.queryByPid(l1.getId());
            List<CatVo> children = new ArrayList<>(l2CatList.size());
            for (LitemallCategory l2 : l2CatList) {
                CatVo l2CatVo = new CatVo();
                l2CatVo.setValue(l2.getId());
                l2CatVo.setLabel(l2.getName());
                children.add(l2CatVo);
            }
            l1CatVo.setChildren(children);

            categoryList.add(l1CatVo);
        }

        // http://element-cn.eleme.io/#/zh-CN/component/select
        // 管理员设置“所属品牌商”
        List<LitemallBrand> list = brandService.all();
        List<Map<String, Object>> brandList = new ArrayList<>(l1CatList.size());
        for (LitemallBrand brand : list) {
            Map<String, Object> b = new HashMap<>(2);
            b.put("value", brand.getId());
            b.put("label", brand.getName());
            brandList.add(b);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("categoryList", categoryList);
        data.put("brandList", brandList);
        return ResponseUtil.ok(data);
    }

    @GetMapping("/detail")
    public Object detail(@LoginAdmin Integer adminId, @NotNull Integer id) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        LitemallGoods goods = goodsService.findById(id);
        List<LitemallProduct> products = productService.queryByGid(id);
        List<LitemallGoodsSpecification> specifications = specificationService.queryByGid(id);
        List<LitemallGoodsAttribute> attributes = attributeService.queryByGid(id);

        Integer categoryId = goods.getCategoryId();
        LitemallCategory category = categoryService.findById(categoryId);
        Integer[] categoryIds = new Integer[]{};
        if (category != null) {
            Integer parentCategoryId = category.getPid();
            categoryIds = new Integer[]{parentCategoryId, categoryId};
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goods", goods);
        data.put("specifications", specifications);
        data.put("products", products);
        data.put("attributes", attributes);
        data.put("categoryIds", categoryIds);

        return ResponseUtil.ok(data);
    }

}
