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
        example.or().andIsHotEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time  desc");
        PageHelper.startPage(offset, limit);
        return goodsMapper.selectByExample(example);
    }

    public List<LitemallGoods> queryByNew(int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsNewEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time  desc");
        PageHelper.startPage(offset, limit);
        return goodsMapper.selectByExample(example);
    }

    public List<LitemallGoods> queryByCategory(List<Integer> catList, int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdIn(catList).andDeletedEqualTo(false);
        example.setOrderByClause("add_time  desc");
        PageHelper.startPage(offset, limit);
        return goodsMapper.selectByExample(example);
    }

    public int countByCategory(List<Integer> catList, int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdIn(catList).andDeletedEqualTo(false);
        return (int)goodsMapper.countByExample(example);
    }

    public List<LitemallGoods> queryByCategory(Integer catId, int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdEqualTo(catId).andDeletedEqualTo(false);
        example.setOrderByClause("add_time  desc");
        PageHelper.startPage(offset, limit);
        return goodsMapper.selectByExample(example);
    }

    public int countByCategory(Integer catId, Integer page, Integer size) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdEqualTo(catId).andDeletedEqualTo(false);
        return (int)goodsMapper.countByExample(example);
    }

    public List<LitemallGoods> querySelective(Integer catId, Integer brandId, String keyword, Boolean isHot, Boolean isNew, Integer offset, Integer limit, String sort, String order) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(catId) && catId != 0){
            criteria.andCategoryIdEqualTo(catId);
        }
        if(!StringUtils.isEmpty(brandId)){
            criteria.andBrandIdEqualTo(brandId);
        }
        if(!StringUtils.isEmpty(isNew)){
            criteria.andIsNewEqualTo(isNew);
        }
        if(!StringUtils.isEmpty(isHot)){
            criteria.andIsHotEqualTo(isHot);
        }
        if(!StringUtils.isEmpty(keyword)){
            criteria.andKeywordsLike("%" + keyword + "%");

            LitemallGoodsExample.Criteria criteria2 = example.createCriteria();
            criteria2.andNameLike("%" + keyword + "%");
            criteria2.andDeletedEqualTo(false);
            example.or(criteria2);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        if(!StringUtils.isEmpty(limit) && !StringUtils.isEmpty(offset)) {
            PageHelper.startPage(offset, limit);
        }

        Column[] columns = new Column[]{Column.id, Column.name, Column.picUrl, Column.retailPrice};
        return goodsMapper.selectByExampleSelective(example ,columns);
    }

    public int countSelective(Integer catId, Integer brandId, String keyword, Boolean isHot, Boolean isNew, Integer offset, Integer limit, String sort, String order) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.createCriteria();

        if(catId != null){
            criteria.andCategoryIdEqualTo(catId);
        }
        if(brandId != null){
            criteria.andBrandIdEqualTo(brandId);
        }
        if(isNew != null){
            criteria.andIsNewEqualTo(isNew);
        }
        if(isHot != null){
            criteria.andIsHotEqualTo(isHot);
        }
        if(keyword != null){
            criteria.andKeywordsLike("%" + keyword + "%");
        }
        criteria.andDeletedEqualTo(false);

        return (int)goodsMapper.countByExample(example);
    }

    public LitemallGoods findById(Integer id) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return goodsMapper.selectOneByExampleWithBLOBs(example);
    }


    public List<LitemallGoods> queryByIds(List<Integer> relatedGoodsIds) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdIn(relatedGoodsIds).andDeletedEqualTo(false);
        return goodsMapper.selectByExampleWithBLOBs(example);
    }

    public Integer queryOnSale() {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return (int)goodsMapper.countByExample(example);
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
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return goodsMapper.selectByExampleWithBLOBs(example);
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
        criteria.andDeletedEqualTo(false);

        return (int)goodsMapper.countByExample(example);
    }

    public void updateById(LitemallGoods goods) {
        goodsMapper.updateByPrimaryKeySelective(goods);
    }

    public void deleteById(Integer id) {
        goodsMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallGoods goods) {
        goodsMapper.insertSelective(goods);
    }

    public int count() {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andDeletedEqualTo(false);
        return (int)goodsMapper.countByExample(example);
    }

    public List<Integer> getCatIds(Integer brandId, String keyword, Boolean isHot, Boolean isNew) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.createCriteria();

        if(brandId != null){
            criteria.andBrandIdEqualTo(brandId);
        }
        if(isNew != null){
            criteria.andIsNewEqualTo(isNew);
        }
        if(isHot != null){
            criteria.andIsHotEqualTo(isHot);
        }
        if(keyword != null){
            criteria.andKeywordsLike("%" + keyword + "%");
        }
        criteria.andDeletedEqualTo(false);

        List<LitemallGoods> goodsList = goodsMapper.selectByExampleSelective(example, Column.categoryId);
        List<Integer> cats = new ArrayList<Integer>();
        for(LitemallGoods goods : goodsList){
            cats.add(goods.getCategoryId());
        }
        return cats;
    }

    public boolean checkExistByName(String name) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andNameEqualTo(name).andDeletedEqualTo(false);
        return goodsMapper.countByExample(example) != 0;
    }
}
