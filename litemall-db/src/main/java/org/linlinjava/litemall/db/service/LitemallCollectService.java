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
        example.or().andUserIdEqualTo(uid).andValueIdEqualTo(gid).andDeletedEqualTo(false);
        return (int)collectMapper.countByExample(example);
    }

    public List<LitemallCollect> queryByType(Integer userId, Byte type, Integer page, Integer size) {
        LitemallCollectExample example = new LitemallCollectExample();
        example.or().andUserIdEqualTo(userId).andTypeEqualTo(type).andDeletedEqualTo(false);
        example.setOrderByClause(LitemallCollect.Column.addTime.desc());
        PageHelper.startPage(page, size);
        return collectMapper.selectByExample(example);
    }

    public int countByType(Integer userId, Byte type) {
        LitemallCollectExample example = new LitemallCollectExample();
        example.or().andUserIdEqualTo(userId).andTypeEqualTo(type).andDeletedEqualTo(false);
        return (int)collectMapper.countByExample(example);
    }

    public LitemallCollect queryByTypeAndValue(Integer userId, Byte type, Integer valueId) {
        LitemallCollectExample example = new LitemallCollectExample();
        example.or().andUserIdEqualTo(userId).andValueIdEqualTo(valueId).andTypeEqualTo(type).andDeletedEqualTo(false);
        return collectMapper.selectOneByExample(example);
    }

    public void deleteById(Integer id) {
        collectMapper.logicalDeleteByPrimaryKey(id);
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
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
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
        criteria.andDeletedEqualTo(false);

        return (int)collectMapper.countByExample(example);
    }

    public void updateById(LitemallCollect collect) {
        collectMapper.updateByPrimaryKeySelective(collect);
    }

    public LitemallCollect findById(Integer id) {
        return collectMapper.selectByPrimaryKey(id);
    }
}
