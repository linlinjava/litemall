package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallSearchHistoryMapper;
import org.linlinjava.litemall.db.domain.LitemallSearchHistory;
import org.linlinjava.litemall.db.domain.LitemallSearchHistoryExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallSearchHistoryService {
    @Resource
    private LitemallSearchHistoryMapper searchHistoryMapper;

    public void save(LitemallSearchHistory searchHistory) {
        searchHistory.setAddTime(LocalDateTime.now());
        searchHistory.setUpdateTime(LocalDateTime.now());
        searchHistoryMapper.insertSelective(searchHistory);
    }

    public List<LitemallSearchHistory> queryByUid(int uid) {
        LitemallSearchHistoryExample example = new LitemallSearchHistoryExample();
        example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false);
        example.setDistinct(true);
        return searchHistoryMapper.selectByExampleSelective(example, LitemallSearchHistory.Column.keyword);
    }

    public void deleteByUid(int uid) {
        LitemallSearchHistoryExample example = new LitemallSearchHistoryExample();
        example.or().andUserIdEqualTo(uid);
        searchHistoryMapper.logicalDeleteByExample(example);
    }

    public List<LitemallSearchHistory> querySelective(String userId, String keyword, Integer page, Integer size, String sort, String order) {
        LitemallSearchHistoryExample example = new LitemallSearchHistoryExample();
        LitemallSearchHistoryExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andKeywordLike("%" + keyword + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return searchHistoryMapper.selectByExample(example);
    }
}
