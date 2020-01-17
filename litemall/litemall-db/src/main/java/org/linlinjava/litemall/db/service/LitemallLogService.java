package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallLogMapper;
import org.linlinjava.litemall.db.domain.LitemallAd;
import org.linlinjava.litemall.db.domain.LitemallLog;
import org.linlinjava.litemall.db.domain.LitemallLogExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallLogService {
    @Resource
    private LitemallLogMapper logMapper;

    public void deleteById(Integer id) {
        logMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallLog log) {
        log.setAddTime(LocalDateTime.now());
        log.setUpdateTime(LocalDateTime.now());
        logMapper.insertSelective(log);
    }

    public List<LitemallLog> querySelective(String name, Integer page, Integer size, String sort, String order) {
        LitemallLogExample example = new LitemallLogExample();
        LitemallLogExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andAdminLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return logMapper.selectByExample(example);
    }
}
