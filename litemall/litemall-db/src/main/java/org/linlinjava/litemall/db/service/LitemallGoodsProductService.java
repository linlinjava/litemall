package org.linlinjava.litemall.db.service;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.dao.GoodsProductMapper;
import org.linlinjava.litemall.db.dao.LitemallGoodsProductMapper;
import org.linlinjava.litemall.db.domain.LitemallGoodsProduct;
import org.linlinjava.litemall.db.domain.LitemallGoodsProductExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallGoodsProductService {
    @Resource
    private LitemallGoodsProductMapper litemallGoodsProductMapper;
    @Resource
    private GoodsProductMapper goodsProductMapper;

    public List<LitemallGoodsProduct> queryByGid(Integer gid) {
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        example.or().andGoodsIdEqualTo(gid).andDeletedEqualTo(false);
        return litemallGoodsProductMapper.selectByExample(example);
    }

    public LitemallGoodsProduct findById(Integer id) {
        return litemallGoodsProductMapper.selectByPrimaryKey(id);
    }

    public void deleteById(Integer id) {
        litemallGoodsProductMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallGoodsProduct goodsProduct) {
        goodsProduct.setAddTime(LocalDateTime.now());
        goodsProduct.setUpdateTime(LocalDateTime.now());
        litemallGoodsProductMapper.insertSelective(goodsProduct);
    }

    public int count() {
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        example.or().andDeletedEqualTo(false);
        return (int) litemallGoodsProductMapper.countByExample(example);
    }

    public void deleteByGid(Integer gid) {
        LitemallGoodsProductExample example = new LitemallGoodsProductExample();
        example.or().andGoodsIdEqualTo(gid);
        litemallGoodsProductMapper.logicalDeleteByExample(example);
    }

    public int addStock(Integer id, Short num){
        return goodsProductMapper.addStock(id, num);
    }

    public int reduceStock(Integer id, Short num){
        return goodsProductMapper.reduceStock(id, num);
    }

    public void updateById(LitemallGoodsProduct product) {
        product.setUpdateTime(LocalDateTime.now());
        litemallGoodsProductMapper.updateByPrimaryKeySelective(product);
    }
}