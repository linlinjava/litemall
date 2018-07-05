package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallProductMapper;
import org.linlinjava.litemall.db.domain.LitemallProduct;
import org.linlinjava.litemall.db.domain.LitemallProductExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallProductService {
    @Resource
    private LitemallProductMapper productMapper;

    public List<LitemallProduct> queryByGid(Integer gid) {
        LitemallProductExample example = new LitemallProductExample();
        example.or().andGoodsIdEqualTo(gid).andDeletedEqualTo(false);
        return productMapper.selectByExample(example);
    }

    public LitemallProduct findById(Integer id) {
        return productMapper.selectByPrimaryKey(id);
    }

    public List<LitemallProduct> querySelective(Integer goodsId, Integer page, Integer size, String sort, String order) {
        LitemallProductExample example = new LitemallProductExample();
        LitemallProductExample.Criteria criteria = example.createCriteria();

        if(goodsId != null){
            criteria.andGoodsIdEqualTo(goodsId);
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return productMapper.selectByExample(example);
    }

    public int countSelective(Integer goodsId, Integer page, Integer size, String sort, String order) {
        LitemallProductExample example = new LitemallProductExample();
        LitemallProductExample.Criteria criteria = example.createCriteria();

        if(goodsId != null){
            criteria.andGoodsIdEqualTo(goodsId);
        }
        criteria.andDeletedEqualTo(false);

        return (int)productMapper.countByExample(example);
    }

    public void updateById(LitemallProduct product) {
        productMapper.updateByPrimaryKeySelective(product);
    }

    public void deleteById(Integer id) {
        productMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallProduct product) {
        productMapper.insertSelective(product);
    }

    public int count() {
        LitemallProductExample example = new LitemallProductExample();
        example.or().andDeletedEqualTo(false);

        return (int)productMapper.countByExample(example);
    }

    public void deleteByGid(Integer gid) {
        LitemallProductExample example = new LitemallProductExample();
        example.or().andGoodsIdEqualTo(gid);
        productMapper.logicalDeleteByExample(example);
    }
}
