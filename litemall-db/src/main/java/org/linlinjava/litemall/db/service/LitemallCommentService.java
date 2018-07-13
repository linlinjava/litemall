package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallCommentMapper;
import org.linlinjava.litemall.db.domain.LitemallComment;
import org.linlinjava.litemall.db.domain.LitemallCommentExample;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallCommentService {
    @Resource
    private LitemallCommentMapper commentMapper;

    public List<LitemallComment> queryGoodsByGid(Integer id, int offset, int limit) {
        LitemallCommentExample example = new LitemallCommentExample();
        example.setOrderByClause(LitemallComment.Column.addTime.desc());
        example.or().andValueIdEqualTo(id).andTypeEqualTo((byte)0).andDeletedEqualTo(false);
        PageHelper.startPage(offset, limit);
        return commentMapper.selectByExample(example);
    }

    public int countGoodsByGid(Integer id, int offset, int limit) {
        LitemallCommentExample example = new LitemallCommentExample();
        example.or().andValueIdEqualTo(id).andTypeEqualTo((byte)0).andDeletedEqualTo(false);
        return (int)commentMapper.countByExample(example);
    }

    public List<LitemallComment> query(Byte type, Integer valueId, Integer showType, Integer offset, Integer limit) {
        LitemallCommentExample example = new LitemallCommentExample();
        example.setOrderByClause(LitemallComment.Column.addTime.desc());
        if(showType == 0) {
            example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andDeletedEqualTo(false);
        }
        else if(showType == 1){
            example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andHasPictureEqualTo(true).andDeletedEqualTo(false);
        }
        else{
            Assert.state(false, "showType不支持");
        }
        PageHelper.startPage(offset, limit);
        return commentMapper.selectByExample(example);
    }

    public int count(Byte type, Integer valueId, Integer showType, Integer offset, Integer size){
        LitemallCommentExample example = new LitemallCommentExample();
        if(showType == 0) {
            example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andDeletedEqualTo(false);
        }
        else if(showType == 1){
            example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andHasPictureEqualTo(true).andDeletedEqualTo(false);
        }
        else{
            Assert.state(false, "");
        }
        return (int)commentMapper.countByExample(example);
    }

    public Integer save(LitemallComment comment) {
        return commentMapper.insertSelective(comment);
    }


    public void update(LitemallComment comment) {
        commentMapper.updateByPrimaryKeySelective(comment);
    }


    public List<LitemallComment> querySelective(String userId, String valueId, Integer page, Integer size, String sort, String order) {
        LitemallCommentExample example = new LitemallCommentExample();
        example.setOrderByClause(LitemallComment.Column.addTime.desc());
        LitemallCommentExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if(!StringUtils.isEmpty(valueId)){
            criteria.andValueIdEqualTo(Integer.valueOf(valueId)).andTypeEqualTo((byte)0);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return commentMapper.selectByExample(example);
    }

    public int countSelective(String userId, String valueId, Integer page, Integer size, String sort, String order) {
        LitemallCommentExample example = new LitemallCommentExample();
        LitemallCommentExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if(!StringUtils.isEmpty(valueId)){
            criteria.andValueIdEqualTo(Integer.valueOf(valueId)).andTypeEqualTo((byte)0);
        }
        criteria.andDeletedEqualTo(false);

        return (int)commentMapper.countByExample(example);
    }

    public void updateById(LitemallComment comment) {
        commentMapper.updateByPrimaryKeySelective(comment);
    }

    public void deleteById(Integer id) {
        commentMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallComment comment) {
        commentMapper.insertSelective(comment);
    }

    public LitemallComment findById(Integer id) {
        return commentMapper.selectByPrimaryKey(id);
    }
}
