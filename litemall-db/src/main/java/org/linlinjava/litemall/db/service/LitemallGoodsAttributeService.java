package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallGoodsAttributeMapper;
import org.linlinjava.litemall.db.domain.LitemallGoodsAttribute;
import org.linlinjava.litemall.db.domain.LitemallGoodsAttributeExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallGoodsAttributeService {
    @Resource
    private LitemallGoodsAttributeMapper goodsAttributeMapper;

    public List<LitemallGoodsAttribute> queryByGid(Integer goodsId) {
        LitemallGoodsAttributeExample example = new LitemallGoodsAttributeExample();
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return goodsAttributeMapper.selectByExample(example);
    }

    public List<LitemallGoodsAttribute> querySelective(Integer goodsId, Integer page, Integer size, String sort, String order) {
        LitemallGoodsAttributeExample example = new LitemallGoodsAttributeExample();
        LitemallGoodsAttributeExample.Criteria criteria = example.createCriteria();

        if(goodsId != null){
            criteria.andGoodsIdEqualTo(goodsId);
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return goodsAttributeMapper.selectByExample(example);
    }

    public int countSelective(Integer goodsId, Integer page, Integer size, String sort, String order) {
        LitemallGoodsAttributeExample example = new LitemallGoodsAttributeExample();
        LitemallGoodsAttributeExample.Criteria criteria = example.createCriteria();

        if(goodsId != null){
            criteria.andGoodsIdEqualTo(goodsId);
        }
        criteria.andDeletedEqualTo(false);

        return (int)goodsAttributeMapper.countByExample(example);
    }

    public void updateById(LitemallGoodsAttribute goodsAttribute) {
        goodsAttributeMapper.updateByPrimaryKeySelective(goodsAttribute);
    }

    public void deleteById(Integer id) {
        goodsAttributeMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallGoodsAttribute goodsAttribute) {
        goodsAttributeMapper.insertSelective(goodsAttribute);
    }

    public LitemallGoodsAttribute findById(Integer id) {
        return goodsAttributeMapper.selectByPrimaryKey(id);
    }

    public void deleteByGid(Integer gid) {
        LitemallGoodsAttributeExample example = new LitemallGoodsAttributeExample();
        example.or().andGoodsIdEqualTo(gid);
        goodsAttributeMapper.logicalDeleteByExample(example);
    }
}
