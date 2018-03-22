package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallFootprintMapper;
import org.linlinjava.litemall.db.domain.LitemallFootprint;
import org.linlinjava.litemall.db.domain.LitemallFootprintExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallFootprintService {
    @Resource
    private LitemallFootprintMapper footprintMapper;

    public List<LitemallFootprint> queryByAddTime(Integer userId, Integer page, Integer size) {
        LitemallFootprintExample example = new LitemallFootprintExample();
        example.or().andUserIdEqualTo(userId);
        example.setOrderByClause(LitemallFootprint.Column.addTime.desc());
        PageHelper.startPage(page, size);
        return footprintMapper.selectByExample(example);
    }

    public int countByAddTime(Integer userId,Integer page, Integer size) {
        LitemallFootprintExample example = new LitemallFootprintExample();
        example.or().andUserIdEqualTo(userId);
        return (int)footprintMapper.countByExample(example);
    }

    public LitemallFootprint findById(Integer id) {
        return footprintMapper.selectByPrimaryKey(id);
    }

    public int deleteById(Integer footprintId) {
        return footprintMapper.deleteByPrimaryKey(footprintId);
    }

    public void add(LitemallFootprint footprint) {
        footprintMapper.insertSelective(footprint);
    }

    public List<LitemallFootprint> querySelective(String userId, String goodsId, Integer page, Integer size, String sort, String order) {
        LitemallFootprintExample example = new LitemallFootprintExample();
        LitemallFootprintExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if(!StringUtils.isEmpty(goodsId)){
            criteria.andGoodsIdEqualTo(Integer.valueOf(goodsId));
        }
        PageHelper.startPage(page, size);
        return footprintMapper.selectByExample(example);
    }

    public int countSelective(String userId, String goodsId, Integer page, Integer size, String sort, String order) {
        LitemallFootprintExample example = new LitemallFootprintExample();
        LitemallFootprintExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if(!StringUtils.isEmpty(goodsId)){
            criteria.andGoodsIdEqualTo(Integer.valueOf(goodsId));
        }
        return (int)footprintMapper.countByExample(example);
    }

    public void updateById(LitemallFootprint collect) {
        footprintMapper.updateByPrimaryKeySelective(collect);
    }

}
