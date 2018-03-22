package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallKeywordMapper;
import org.linlinjava.litemall.db.domain.LitemallKeyword;
import org.linlinjava.litemall.db.domain.LitemallKeywordExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallKeywordService {
    @Resource
    private LitemallKeywordMapper keywordsMapper;

    public List<LitemallKeyword> queryDefaults() {
        LitemallKeywordExample example = new LitemallKeywordExample();
        example.or().andIsDefaultEqualTo(true);
        return keywordsMapper.selectByExample(example);
    }

    public LitemallKeyword queryDefault() {
        LitemallKeywordExample example = new LitemallKeywordExample();
        example.or().andIsDefaultEqualTo(true);
        return keywordsMapper.selectOneByExample(example);
    }

    public List<LitemallKeyword> queryHots() {
        LitemallKeywordExample example = new LitemallKeywordExample();
        example.or().andIsHotEqualTo(true);
        return keywordsMapper.selectByExample(example);
    }

    public List<LitemallKeyword> queryByKeyword(String keyword) {
        LitemallKeywordExample example = new LitemallKeywordExample();
        example.or().andKeywordLike(keyword);
        return keywordsMapper.selectByExample(example);
    }

    public List<LitemallKeyword> querySelective(String keyword, String url, Integer page, Integer limit, String sort, String order) {
        LitemallKeywordExample example = new LitemallKeywordExample();
        LitemallKeywordExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(keyword)) {
            criteria.andKeywordLike("%" + keyword + "%");
        }
        if (!StringUtils.isEmpty(url)) {
            criteria.andUrlLike("%" + url + "%");
        }
        PageHelper.startPage(page, limit);
        return keywordsMapper.selectByExample(example);
    }

    public int countSelective(String keyword, String url, Integer page, Integer limit, String sort, String order) {
        LitemallKeywordExample example = new LitemallKeywordExample();
        LitemallKeywordExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(keyword)) {
            criteria.andKeywordLike("%" + keyword + "%");
        }
        if (!StringUtils.isEmpty(url)) {
            criteria.andUrlLike("%" + url + "%");
        }
        PageHelper.startPage(page, limit);
        return (int)keywordsMapper.countByExample(example);
    }

    public void add(LitemallKeyword keywords) {
        keywordsMapper.insertSelective(keywords);
    }

    public LitemallKeyword findById(Integer id) {
        return keywordsMapper.selectByPrimaryKey(id);
    }

    public void updateById(LitemallKeyword keywords) {
        keywordsMapper.updateByPrimaryKeySelective(keywords);
    }

    public void deleteById(Integer id) {
        keywordsMapper.deleteByPrimaryKey(id);
    }
}
