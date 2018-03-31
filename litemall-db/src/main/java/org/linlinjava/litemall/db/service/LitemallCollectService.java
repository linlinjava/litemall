package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.domain.LitemallCollect;
import org.linlinjava.litemall.db.dao.LitemallCollectMapper;
import org.linlinjava.litemall.db.domain.LitemallCollectExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallCollectService {
    @Resource
    private LitemallCollectMapper collectMapper;

    public int count(int uid, Integer gid) {
        LitemallCollectExample example = new LitemallCollectExample();
        example.or().andUserIdEqualTo(uid).andValueIdEqualTo(gid);
        return (int)collectMapper.countByExample(example);
    }

    public List<LitemallCollect> queryByType(Integer userId, Integer typeId, Integer page, Integer size) {
        LitemallCollectExample example = new LitemallCollectExample();
        example.or().andUserIdEqualTo(userId).andTypeIdEqualTo(typeId);
        example.setOrderByClause(LitemallCollect.Column.addTime.desc());
        PageHelper.startPage(page, size);
        return collectMapper.selectByExample(example);
    }

    public int countByType(Integer userId, Integer typeId) {
        LitemallCollectExample example = new LitemallCollectExample();
        example.or().andUserIdEqualTo(userId).andTypeIdEqualTo(typeId);
        return (int)collectMapper.countByExample(example);
    }

    public LitemallCollect queryByTypeAndValue(Integer userId, Integer typeId, Integer valueId) {
        LitemallCollectExample example = new LitemallCollectExample();
        example.or().andUserIdEqualTo(userId).andValueIdEqualTo(valueId).andTypeIdEqualTo(typeId);
        return collectMapper.selectOneByExample(example);
    }

    public int deleteById(Integer id) {
        return collectMapper.deleteByPrimaryKey(id);
    }

    public int add(LitemallCollect collect) {
        return collectMapper.insertSelective(collect);
    }

    public List<LitemallCollect> querySelective(String userId, String valueId, Integer page, Integer size, String sort, String order) {
        LitemallCollectExample example = new LitemallCollectExample();
        LitemallCollectExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if(!StringUtils.isEmpty(valueId)){
            criteria.andValueIdEqualTo(Integer.valueOf(valueId));
        }
        PageHelper.startPage(page, size);
        return collectMapper.selectByExample(example);
    }

    public int countSelective(String userId, String valueId, Integer page, Integer size, String sort, String order) {
        LitemallCollectExample example = new LitemallCollectExample();
        LitemallCollectExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if(!StringUtils.isEmpty(valueId)){
            criteria.andValueIdEqualTo(Integer.valueOf(valueId));
        }
        return (int)collectMapper.countByExample(example);
    }

    public void updateById(LitemallCollect collect) {
        collectMapper.updateByPrimaryKeySelective(collect);
    }

    public LitemallCollect findById(Integer id) {
        return collectMapper.selectByPrimaryKey(id);
    }
}
