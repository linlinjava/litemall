package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallGoodsMapper;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallGoods.Column;
import org.linlinjava.litemall.db.domain.LitemallGoodsExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LitemallGoodsService {
    Column[] columns = new Column[]{Column.id, Column.name, Column.brief, Column.picUrl, Column.isHot, Column.isNew, Column.counterPrice, Column.retailPrice};
    @Resource
    private LitemallGoodsMapper goodsMapper;

    /**
     * 获取热卖商品
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryByHot(int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsHotEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return goodsMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取新品上市
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryByNew(int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsNewEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return goodsMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取分类下的商品
     *
     * @param catList
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryByCategory(List<Integer> catList, int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdIn(catList).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time  desc");
        PageHelper.startPage(offset, limit);

        return goodsMapper.selectByExampleSelective(example, columns);
    }


    /**
     * 获取分类下的商品
     *
     * @param catId
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallGoods> queryByCategory(Integer catId, int offset, int limit) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andCategoryIdEqualTo(catId).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return goodsMapper.selectByExampleSelective(example, columns);
    }


    public List<LitemallGoods> querySelective(Integer catId, Integer brandId, String keywords, Boolean isHot, Boolean isNew, Integer offset, Integer limit, String sort, String order) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria1 = example.or();
        LitemallGoodsExample.Criteria criteria2 = example.or();

        if (!StringUtils.isEmpty(catId) && catId != 0) {
            criteria1.andCategoryIdEqualTo(catId);
            criteria2.andCategoryIdEqualTo(catId);
        }
        if (!StringUtils.isEmpty(brandId)) {
            criteria1.andBrandIdEqualTo(brandId);
            criteria2.andBrandIdEqualTo(brandId);
        }
        if (!StringUtils.isEmpty(isNew)) {
            criteria1.andIsNewEqualTo(isNew);
            criteria2.andIsNewEqualTo(isNew);
        }
        if (!StringUtils.isEmpty(isHot)) {
            criteria1.andIsHotEqualTo(isHot);
            criteria2.andIsHotEqualTo(isHot);
        }
        if (!StringUtils.isEmpty(keywords)) {
            criteria1.andKeywordsLike("%" + keywords + "%");
            criteria2.andNameLike("%" + keywords + "%");
        }
        criteria1.andIsOnSaleEqualTo(true);
        criteria2.andIsOnSaleEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(offset, limit);

        return goodsMapper.selectByExampleSelective(example, columns);
    }

    public List<LitemallGoods> querySelective(Integer goodsId, String goodsSn, String name, Integer page, Integer size, String sort, String order) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria = example.createCriteria();

        if (goodsId != null) {
            criteria.andIdEqualTo(goodsId);
        }
        if (!StringUtils.isEmpty(goodsSn)) {
            criteria.andGoodsSnEqualTo(goodsSn);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return goodsMapper.selectByExampleWithBLOBs(example);
    }

    /**
     * 获取某个商品信息,包含完整信息
     *
     * @param id
     * @return
     */
    public LitemallGoods findById(Integer id) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return goodsMapper.selectOneByExampleWithBLOBs(example);
    }

    /**
     * 获取某个商品信息，仅展示相关内容
     *
     * @param id
     * @return
     */
    public LitemallGoods findByIdVO(Integer id) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdEqualTo(id).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return goodsMapper.selectOneByExampleSelective(example, columns);
    }


    /**
     * 获取所有在售物品总数
     *
     * @return
     */
    public Integer queryOnSale() {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return (int) goodsMapper.countByExample(example);
    }

    public int updateById(LitemallGoods goods) {
        goods.setUpdateTime(LocalDateTime.now());
        return goodsMapper.updateByPrimaryKeySelective(goods);
    }

    public void deleteById(Integer id) {
        goodsMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallGoods goods) {
        goods.setAddTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        goodsMapper.insertSelective(goods);
    }

    /**
     * 获取所有物品总数，包括在售的和下架的，但是不包括已删除的商品
     *
     * @return
     */
    public int count() {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andDeletedEqualTo(false);
        return (int) goodsMapper.countByExample(example);
    }

    public List<Integer> getCatIds(Integer brandId, String keywords, Boolean isHot, Boolean isNew) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        LitemallGoodsExample.Criteria criteria1 = example.or();
        LitemallGoodsExample.Criteria criteria2 = example.or();

        if (!StringUtils.isEmpty(brandId)) {
            criteria1.andBrandIdEqualTo(brandId);
            criteria2.andBrandIdEqualTo(brandId);
        }
        if (!StringUtils.isEmpty(isNew)) {
            criteria1.andIsNewEqualTo(isNew);
            criteria2.andIsNewEqualTo(isNew);
        }
        if (!StringUtils.isEmpty(isHot)) {
            criteria1.andIsHotEqualTo(isHot);
            criteria2.andIsHotEqualTo(isHot);
        }
        if (!StringUtils.isEmpty(keywords)) {
            criteria1.andKeywordsLike("%" + keywords + "%");
            criteria2.andNameLike("%" + keywords + "%");
        }
        criteria1.andIsOnSaleEqualTo(true);
        criteria2.andIsOnSaleEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andDeletedEqualTo(false);

        List<LitemallGoods> goodsList = goodsMapper.selectByExampleSelective(example, Column.categoryId);
        List<Integer> cats = new ArrayList<Integer>();
        for (LitemallGoods goods : goodsList) {
            cats.add(goods.getCategoryId());
        }
        return cats;
    }

    public boolean checkExistByName(String name) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andNameEqualTo(name).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return goodsMapper.countByExample(example) != 0;
    }

    public List<LitemallGoods> queryByIds(Integer[] ids) {
        LitemallGoodsExample example = new LitemallGoodsExample();
        example.or().andIdIn(Arrays.asList(ids)).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return goodsMapper.selectByExampleSelective(example, columns);
    }
}
