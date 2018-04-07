package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallSearchHistoryMapper;
import org.linlinjava.litemall.db.domain.LitemallSearchHistory;
import org.linlinjava.litemall.db.domain.LitemallSearchHistoryExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallSearchHistoryService {
    @Resource
    private LitemallSearchHistoryMapper searchHistoryMapper;

    public void save(LitemallSearchHistory searchHistory) {
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
        LitemallSearchHistory searchHistory = new LitemallSearchHistory();
        searchHistory.setDeleted(true);
        searchHistoryMapper.updateByExampleSelective(searchHistory, example);
    }

    public void deleteById(Integer id) {
        LitemallSearchHistory searchHistory = searchHistoryMapper.selectByPrimaryKey(id);
        if(searchHistory == null){
            return;
        }
        searchHistory.setDeleted(true);
        searchHistoryMapper.updateByPrimaryKey(searchHistory);
    }

    public void add(LitemallSearchHistory searchHistory) {
        searchHistoryMapper.insertSelective(searchHistory);
    }

    public List<LitemallSearchHistory> querySelective(String userId, String keyword, Integer page, Integer size, String sort, String order) {
        LitemallSearchHistoryExample example = new LitemallSearchHistoryExample();
        LitemallSearchHistoryExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if(!StringUtils.isEmpty(keyword)){
            criteria.andKeywordLike("%" + keyword + "%" );
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return searchHistoryMapper.selectByExample(example);
    }

    public int countSelective(String userId, String keyword, Integer page, Integer size, String sort, String order) {
        LitemallSearchHistoryExample example = new LitemallSearchHistoryExample();
        LitemallSearchHistoryExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if(!StringUtils.isEmpty(keyword)){
            criteria.andKeywordLike("%" + keyword + "%" );
        }
        criteria.andDeletedEqualTo(false);

        return (int)searchHistoryMapper.countByExample(example);
    }

    public void updateById(LitemallSearchHistory collect) {
        searchHistoryMapper.updateByPrimaryKeySelective(collect);
    }

    public LitemallSearchHistory findById(Integer id) {
        return searchHistoryMapper.selectByPrimaryKey(id);
    }
}
