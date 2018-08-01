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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
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
                       @Order @RequestParam(defaultValue = "desc") String order){
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
            qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());
            goods.setShareUrl(qCodeService.getShareImageUrl(goods.getId().toString()));

            // 商品基本信息表litemall_goods
            goodsService.updateById(goods);

            Integer gid = goods.getId();
            specificationService.deleteByGid(gid);
            attributeService.deleteByGid(gid);
            productService.deleteByGid(gid);

            // 商品规格表litemall_goods_specification
            Map<String, Integer> specIds = new HashMap<>();
            for (LitemallGoodsSpecification specification : specifications) {
                specification.setGoodsId(goods.getId());
                specification.setAddTime(LocalDateTime.now());
                specificationService.add(specification);
                specIds.put(specification.getValue(), specification.getId());
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
        }
        txManager.commit(status);
        return ResponseUtil.ok();
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody GoodsAllinone goodsAllinone) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
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

            //将生成的分享图片地址写入数据库
            qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());
            goods.setShareUrl(qCodeService.getShareImageUrl(goods.getId().toString()));

            goodsService.add(goods);

            // 商品规格表litemall_goods_specification
            Map<String, Integer> specIds = new HashMap<>();
            for (LitemallGoodsSpecification specification : specifications) {
                specification.setGoodsId(goods.getId());
                specification.setAddTime(LocalDateTime.now());
                specificationService.add(specification);
                specIds.put(specification.getValue(), specification.getId());
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
    public Object detail(@LoginAdmin Integer adminId,  @NotNull Integer id){
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
