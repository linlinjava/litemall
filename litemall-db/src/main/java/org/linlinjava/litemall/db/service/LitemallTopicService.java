package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallTopicMapper;
import org.linlinjava.litemall.db.domain.LitemallGroupon;
import org.linlinjava.litemall.db.domain.LitemallTopic;
import org.linlinjava.litemall.db.domain.LitemallTopic.Column;
import org.linlinjava.litemall.db.domain.LitemallTopicExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallTopicService {
    @Resource
    private LitemallTopicMapper topicMapper;
    private Column[] columns = new Column[]{Column.id, Column.title, Column.subtitle, Column.price, Column.picUrl, Column.readCount};

    public List<LitemallTopic> queryList(int offset, int limit) {
        return queryList(offset, limit, "add_time", "desc");
    }

    public List<LitemallTopic> queryList(int offset, int limit, String sort, String order) {
        LitemallTopicExample example = new LitemallTopicExample();
        example.or().andDeletedEqualTo(false);
        example.setOrderByClause(sort + " " + order);
        PageHelper.startPage(offset, limit);
        return topicMapper.selectByExampleSelective(example, columns);
    }

    public int queryTotal() {
        LitemallTopicExample example = new LitemallTopicExample();
        example.or().andDeletedEqualTo(false);
        return (int) topicMapper.countByExample(example);
    }

    public LitemallTopic findById(Integer id) {
        LitemallTopicExample example = new LitemallTopicExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return topicMapper.selectOneByExampleWithBLOBs(example);
    }

    public List<LitemallTopic> queryRelatedList(Integer id, int offset, int limit) {
        LitemallTopicExample example = new LitemallTopicExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        List<LitemallTopic> topics = topicMapper.selectByExample(example);
        if (topics.size() == 0) {
            return queryList(offset, limit, "add_time", "desc");
        }
        LitemallTopic topic = topics.get(0);

        example = new LitemallTopicExample();
        example.or().andIdNotEqualTo(topic.getId()).andDeletedEqualTo(false);
        PageHelper.startPage(offset, limit);
        List<LitemallTopic> relateds = topicMapper.selectByExampleWithBLOBs(example);
        if (relateds.size() != 0) {
            return relateds;
        }

        return queryList(offset, limit, "add_time", "desc");
    }

    public List<LitemallTopic> querySelective(String title, String subtitle, Integer page, Integer limit, String sort, String order) {
        LitemallTopicExample example = new LitemallTopicExample();
        LitemallTopicExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(title)) {
            criteria.andTitleLike("%" + title + "%");
        }
        if (!StringUtils.isEmpty(subtitle)) {
            criteria.andSubtitleLike("%" + subtitle + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return topicMapper.selectByExampleWithBLOBs(example);
    }

    public int updateById(LitemallTopic topic) {
        topic.setUpdateTime(LocalDateTime.now());
        LitemallTopicExample example = new LitemallTopicExample();
        example.or().andIdEqualTo(topic.getId());
        return topicMapper.updateByExampleSelective(topic, example);
    }

    public void deleteById(Integer id) {
        topicMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallTopic topic) {
        topic.setAddTime(LocalDateTime.now());
        topic.setUpdateTime(LocalDateTime.now());
        topicMapper.insertSelective(topic);
    }


    public void deleteByIds(List<Integer> ids) {
        LitemallTopicExample example = new LitemallTopicExample();
        example.or().andIdIn(ids).andDeletedEqualTo(false);
        LitemallTopic topic = new LitemallTopic();
        topic.setUpdateTime(LocalDateTime.now());
        topic.setDeleted(true);
        topicMapper.updateByExampleSelective(topic, example);
    }
}
