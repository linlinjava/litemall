package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallSystemMapper;
import org.linlinjava.litemall.db.domain.LitemallSystem;
import org.linlinjava.litemall.db.domain.LitemallSystemExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallSystemCfgService {
    @Resource
    private LitemallSystemMapper systemMapper;

    public void add(LitemallSystem litemallSystem) {
        systemMapper.insert(litemallSystem);
    }

    public List<LitemallSystem> queryAll() {
        LitemallSystemExample example = new LitemallSystemExample();
        example.or();
        return systemMapper.selectByExample(example);
    }

    public LitemallSystem queryByKeyName(String keyName, String groupName) {
        LitemallSystemExample example = new LitemallSystemExample();
        example.or().andGroupEqualTo(groupName).andKeyEqualTo(keyName);
        return systemMapper.selectOneByExample(example);
    }

    public List<LitemallSystem> queryByGroup(String groupName) {
        LitemallSystemExample example = new LitemallSystemExample();
        example.or().andGroupEqualTo(groupName);
        return systemMapper.selectByExample(example);
    }

    public void deleteById(Integer id) {
        systemMapper.deleteByPrimaryKey(id);
    }
}
