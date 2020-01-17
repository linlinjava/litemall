package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallStorageMapper;
import org.linlinjava.litemall.db.domain.LitemallStorage;
import org.linlinjava.litemall.db.domain.LitemallStorageExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallStorageService {
    @Autowired
    private LitemallStorageMapper storageMapper;

    public void deleteByKey(String key) {
        LitemallStorageExample example = new LitemallStorageExample();
        example.or().andKeyEqualTo(key);
        storageMapper.logicalDeleteByExample(example);
    }

    public void add(LitemallStorage storageInfo) {
        storageInfo.setAddTime(LocalDateTime.now());
        storageInfo.setUpdateTime(LocalDateTime.now());
        storageMapper.insertSelective(storageInfo);
    }

    public LitemallStorage findByKey(String key) {
        LitemallStorageExample example = new LitemallStorageExample();
        example.or().andKeyEqualTo(key).andDeletedEqualTo(false);
        return storageMapper.selectOneByExample(example);
    }

    public int update(LitemallStorage storageInfo) {
        storageInfo.setUpdateTime(LocalDateTime.now());
        return storageMapper.updateByPrimaryKeySelective(storageInfo);
    }

    public LitemallStorage findById(Integer id) {
        return storageMapper.selectByPrimaryKey(id);
    }

    public List<LitemallStorage> querySelective(String key, String name, Integer page, Integer limit, String sort, String order) {
        LitemallStorageExample example = new LitemallStorageExample();
        LitemallStorageExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(key)) {
            criteria.andKeyEqualTo(key);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return storageMapper.selectByExample(example);
    }
}
