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
        example.or().andDeletedEqualTo(false);
        return issueMapper.selectByExample(example);
    }

    public void deleteById(Integer id) {
        issueMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallIssue issue) {
        issueMapper.insertSelective(issue);
    }

    public List<LitemallIssue> querySelective(String question, Integer page, Integer size, String sort, String order) {
        LitemallIssueExample example = new LitemallIssueExample();
        LitemallIssueExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(question)){
            criteria.andQuestionLike("%" + question + "%" );
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
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
        criteria.andDeletedEqualTo(false);

        return (int)issueMapper.countByExample(example);
    }

    public void updateById(LitemallIssue issue) {
        issueMapper.updateByPrimaryKeySelective(issue);
    }

    public LitemallIssue findById(Integer id) {
        return issueMapper.selectByPrimaryKey(id);
    }
}
