package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallCategoryMapper;
import org.linlinjava.litemall.db.domain.LitemallCategory;
import org.linlinjava.litemall.db.domain.LitemallCategoryExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallCategoryService {
    @Resource
    private LitemallCategoryMapper categoryMapper;

    public List<LitemallCategory> queryL1WithoutRecommend(int offset, int limit) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.or().andLevelEqualTo("L1").andNameNotEqualTo("推荐");
        PageHelper.startPage(offset, limit);
        return categoryMapper.selectByExample(example);
    }

    public List<LitemallCategory> queryL1(int offset, int limit) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.or().andLevelEqualTo("L1");
        PageHelper.startPage(offset, limit);
        return categoryMapper.selectByExample(example);
    }

    public List<LitemallCategory> queryL1() {
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.or().andLevelEqualTo("L1");
        return categoryMapper.selectByExample(example);
    }

    public List<LitemallCategory> queryByPid(Integer pid) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.or().andParentIdEqualTo(pid);
        return categoryMapper.selectByExample(example);
    }

    public List<LitemallCategory> queryL2ByIds(List<Integer> ids) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.or().andIdIn(ids).andLevelEqualTo("L2");
        return categoryMapper.selectByExample(example);
    }

    public LitemallCategory findById(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    public List<LitemallCategory> querySelective(String id, String name, Integer page, Integer size, String sort, String order) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        LitemallCategoryExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(id)){
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        PageHelper.startPage(page, size);
        return categoryMapper.selectByExample(example);
    }

    public int countSelective(String id, String name, Integer page, Integer size, String sort, String order) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        LitemallCategoryExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(id)){
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        return (int)categoryMapper.countByExample(example);
    }

    public void updateById(LitemallCategory collect) {
        categoryMapper.updateByPrimaryKeySelective(collect);
    }

    public void deleteById(Integer id) {
        categoryMapper.deleteByPrimaryKey(id);
    }

    public void add(LitemallCategory category) {
        categoryMapper.insertSelective(category);
    }

    private LitemallCategory.Column[] CHANNEL = {LitemallCategory.Column.id, LitemallCategory.Column.name, LitemallCategory.Column.iconUrl};
    public List<LitemallCategory> queryChannel() {
        LitemallCategoryExample example = new LitemallCategoryExample();
        example.or().andLevelEqualTo("L1");
        return categoryMapper.selectByExampleSelective(example, CHANNEL);
    }
}
