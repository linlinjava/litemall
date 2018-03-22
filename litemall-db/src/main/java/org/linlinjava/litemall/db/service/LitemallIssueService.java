package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallIssueMapper;
import org.linlinjava.litemall.db.domain.LitemallIssue;
import org.linlinjava.litemall.db.domain.LitemallIssueExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallIssueService {
    @Resource
    private LitemallIssueMapper issueMapper;

    public List<LitemallIssue> query() {
        LitemallIssueExample example = new LitemallIssueExample();
        return issueMapper.selectByExample(example);
    }

    public int deleteById(Integer id) {
        return issueMapper.deleteByPrimaryKey(id);
    }

    public void add(LitemallIssue searchHistory) {
        issueMapper.insertSelective(searchHistory);
    }

    public List<LitemallIssue> querySelective(String question, Integer page, Integer size, String sort, String order) {
        LitemallIssueExample example = new LitemallIssueExample();
        LitemallIssueExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(question)){
            criteria.andQuestionLike("%" + question + "%" );
        }
        PageHelper.startPage(page, size);
        return issueMapper.selectByExample(example);
    }

    public int countSelective(String question, Integer page, Integer size, String sort, String order) {
        LitemallIssueExample example = new LitemallIssueExample();
        LitemallIssueExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(question)){
            criteria.andQuestionLike("%" + question + "%" );
        }
        return (int)issueMapper.countByExample(example);
    }

    public void updateById(LitemallIssue collect) {
        issueMapper.updateByPrimaryKeySelective(collect);
    }

    public LitemallIssue findById(Integer id) {
        return issueMapper.selectByPrimaryKey(id);
    }
}
