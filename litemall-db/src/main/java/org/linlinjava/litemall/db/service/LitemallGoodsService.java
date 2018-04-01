package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallGoods.Column;
import org.linlinjava.litemall.db.dao.LitemallGoodsMapper;
import org.linlinjava.litemall.db.domain.LitemallGoodsExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class LitemallGoodsService {
    @Resource
    private LitemallGoodsMapper goodsMapper;

    public List<LitemallGoods> queryByHot(int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsHotEqualTo(true);
        PageHelper.startPage(offset, limit);
        return goodsMapper.selectByExample(example);
    }

    public List<LitemallGoods> queryByNew(int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsNewEqualTo(true);
        PageHelper.startPage(offset, limit);
        return goodsMapper.selectByExample(example);
    }

    public List<LitemallGoods> queryByCategory(List<Integer> catList, int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdIn(catList);
        PageHelper.startPage(offset, limit);
        return goodsMapper.selectByExample(example);
    }

    public int countByCategory(List<Integer> catList, int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdIn(catList);
        return (int)goodsMapper.countByExample(example);
    }

    public List<LitemallGoods> queryByCategory(Integer catId, int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdEqualTo(catId);
        PageHelper.startPage(offset, limit);
        return goodsMapper.selectByExample(example);
    }

    public int countByCategory(Integer catId, Integer page, Integer size) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdEqualTo(catId);
        return (int)goodsMapper.countByExample(example);
    }

    public List<LitemallGoods> querySelective(Integer catId, Integer brandId, String keyword, Integer isHot, Integer isNew, Integer offset, Integer limit, String sort) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.createCriteria();

        if(catId != null && catId != 0){
            criteria.andCategoryIdEqualTo(catId);
        }

        if(brandId != null){
            criteria.andBrandIdEqualTo(brandId);
        }

        if(isNew != null){
            criteria.andIsNewEqualTo(isNew.intValue() == 1);
        }

        if(isHot != null){
            criteria.andIsHotEqualTo(isHot.intValue() == 1);
        }

        if(keyword != null){
            criteria.andKeywordsLike("%" + keyword + "%");
        }

        if(sort != null){
            example.setOrderByClause(sort);
        }

        if(limit != null && offset != null) {
            PageHelper.startPage(offset, limit);
        }

        Column[] columns = new Column[]{Column.id, Column.name, Column.listPicUrl, Column.retailPrice};
        return goodsMapper.selectByExampleSelective(example ,columns);
    }

    public int countSelective(Integer catId, Integer brandId, String keyword, Integer isHot, Integer isNew, Integer offset, Integer limit, String sort) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.createCriteria();

        if(catId != null){
            criteria.andCategoryIdEqualTo(catId);
        }

        if(brandId != null){
            criteria.andBrandIdEqualTo(brandId);
        }

        if(isNew != null){
            criteria.andIsNewEqualTo(isNew.intValue() == 1);
        }

        if(isHot != null){
            criteria.andIsHotEqualTo(isHot.intValue() == 1);
        }

        if(keyword != null){
            criteria.andKeywordsLike("%" + keyword + "%");
        }

        return (int)goodsMapper.countByExample(example);
    }

    public LitemallGoods findById(Integer id) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdEqualTo(id);
        return goodsMapper.selectOneByExampleWithBLOBs(example);
    }


    public List<LitemallGoods> queryByIds(List<Integer> relatedGoodsIds) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdIn(relatedGoodsIds);
        return goodsMapper.selectByExample(example);
    }

    public Integer queryOnSale() {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsOnSaleEqualTo(true);
        return (int)goodsMapper.countByExample(example);
    }

    public String generateGoodsSn() {
        // TODO
        return String.valueOf(new Random().nextInt());
    }

    public List<LitemallGoods> querySelective(String goodsSn, String name, Integer page, Integer size, String sort, String order) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(goodsSn)){
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        PageHelper.startPage(page, size);
        return goodsMapper.selectByExample(example);
    }

    public int countSelective(String goodsSn, String name, Integer page, Integer size, String sort, String order) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(goodsSn)){
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        return (int)goodsMapper.countByExample(example);
    }

    public void updateById(LitemallGoods goods) {
        goodsMapper.updateByPrimaryKeySelective(goods);
    }

    public void deleteById(Integer id) {
        goodsMapper.deleteByPrimaryKey(id);
    }

    public void add(LitemallGoods goods) {
        goodsMapper.insertSelective(goods);
    }

    public int count() {
        LitemallGoodsExample example = new LitemallGoodsExample();
        return (int)goodsMapper.countByExample(example);
    }

    public List<Integer> getCatIds(Integer brandId, String keyword, Integer isHot, Integer isNew) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.createCriteria();

        if(brandId != null){
            criteria.andBrandIdEqualTo(brandId);
        }

        if(isNew != null){
            criteria.andIsNewEqualTo(isNew.intValue() == 1);
        }

        if(isHot != null){
            criteria.andIsHotEqualTo(isHot.intValue() == 1);
        }

        if(keyword != null){
            criteria.andKeywordsLike("%" + keyword + "%");
        }

        List<LitemallGoods> goodsList = goodsMapper.selectByExampleSelective(example, Column.categoryId);
        List<Integer> cats = new ArrayList<Integer>();
        for(LitemallGoods goods : goodsList){
            cats.add(goods.getCategoryId());
        }
        return cats;
    }
}
