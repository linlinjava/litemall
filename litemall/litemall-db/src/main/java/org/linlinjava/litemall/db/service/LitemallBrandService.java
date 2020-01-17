package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallBrandMapper;
import org.linlinjava.litemall.db.domain.LitemallBrand;
import org.linlinjava.litemall.db.domain.LitemallBrand.Column;
import org.linlinjava.litemall.db.domain.LitemallBrandExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallBrandService {
    @Resource
    private LitemallBrandMapper brandMapper;
    private Column[] columns = new Column[]{Column.id, Column.name, Column.desc, Column.picUrl, Column.floorPrice};

    public List<LitemallBrand> query(Integer page, Integer limit, String sort, String order) {
        LitemallBrandExample example = new LitemallBrandExample();
        example.or().andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }
        PageHelper.startPage(page, limit);
        return brandMapper.selectByExampleSelective(example, columns);
    }

    public List<LitemallBrand> query(Integer page, Integer limit) {
        return query(page, limit, null, null);
    }

    public LitemallBrand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    public List<LitemallBrand> querySelective(String id, String name, Integer page, Integer size, String sort, String order) {
        LitemallBrandExample example = new LitemallBrandExample();
        LitemallBrandExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return brandMapper.selectByExample(example);
    }

    public int updateById(LitemallBrand brand) {
        brand.setUpdateTime(LocalDateTime.now());
        return brandMapper.updateByPrimaryKeySelective(brand);
    }

    public void deleteById(Integer id) {
        brandMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallBrand brand) {
        brand.setAddTime(LocalDateTime.now());
        brand.setUpdateTime(LocalDateTime.now());
        brandMapper.insertSelective(brand);
    }

    public List<LitemallBrand> all() {
        LitemallBrandExample example = new LitemallBrandExample();
        example.or().andDeletedEqualTo(false);
        return brandMapper.selectByExample(example);
    }
}
