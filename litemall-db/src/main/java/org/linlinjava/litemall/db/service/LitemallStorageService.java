package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallStorageMapper;
import org.linlinjava.litemall.db.domain.LitemallStorage;
import org.linlinjava.litemall.db.domain.LitemallStorageExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        storageMapper.insertSelective(storageInfo);
    }

    public LitemallStorage findByName(String filename) {
        LitemallStorageExample example = new LitemallStorageExample();
        example.or().andNameEqualTo(filename).andDeletedEqualTo(false);
        return storageMapper.selectOneByExample(example);
    }

    public LitemallStorage findByKey(String key) {
        LitemallStorageExample example = new LitemallStorageExample();
        example.or().andKeyEqualTo(key).andDeletedEqualTo(false);
        return storageMapper.selectOneByExample(example);
    }

    public void update(LitemallStorage storageInfo) {
        storageMapper.updateByPrimaryKeySelective(storageInfo);
    }


    public LitemallStorage findById(Integer id) {
        return storageMapper.selectByPrimaryKey(id);
    }

    public List<LitemallStorage> querySelective(String key, String name, Integer page, Integer limit, String sort, String order) {
        LitemallStorageExample example = new LitemallStorageExample();
        LitemallStorageExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(key)){
            criteria.andKeyEqualTo(key);
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, limit);
        return storageMapper.selectByExample(example);
    }

    public int countSelective(String key, String name, Integer page, Integer size, String sort, String order) {
        LitemallStorageExample example = new LitemallStorageExample();
        LitemallStorageExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(key)){
            criteria.andKeyEqualTo(key);
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        return (int)storageMapper.countByExample(example);
    }
}
