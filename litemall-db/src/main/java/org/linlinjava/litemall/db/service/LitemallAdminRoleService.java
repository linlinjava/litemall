package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallAdminRoleMapper;
import org.linlinjava.litemall.db.domain.LitemallAdminRole.Column;
import org.linlinjava.litemall.db.domain.LitemallAdminRole;
import org.linlinjava.litemall.db.domain.LitemallAdminRoleExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ulongx
 */
@Service
public class LitemallAdminRoleService {
    private final Column[] result = new Column[]{Column.id, Column.roleId, Column.adminId};
    @Resource
    private LitemallAdminRoleMapper adminRoleMapper;

    public List<LitemallAdminRole> findAdminRole(Integer adminId) {
        LitemallAdminRoleExample example = new LitemallAdminRoleExample();
        example.or().andAdminIdEqualTo(adminId);
        return adminRoleMapper.selectByExample(example);
    }

    public LitemallAdminRole findAdminRoleByKey(Integer id) {
        return adminRoleMapper.selectByPrimaryKey(id);
    }

    public List<LitemallAdminRole> querySelective(Integer adminId, Integer page, Integer limit, String sort, String order) {
        LitemallAdminRoleExample example = new LitemallAdminRoleExample();
        LitemallAdminRoleExample.Criteria criteria = example.createCriteria();

        if (adminId != null) {
            criteria.andAdminIdEqualTo(adminId);
        }

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return adminRoleMapper.selectByExampleSelective(example, result);
    }

    public int countSelective(Integer adminId, Integer page, Integer size, String sort, String order) {
        LitemallAdminRoleExample example = new LitemallAdminRoleExample();
        LitemallAdminRoleExample.Criteria criteria = example.createCriteria();

        if (adminId != null) {
            criteria.andAdminIdEqualTo(adminId);
        }

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        return (int) adminRoleMapper.countByExample(example);
    }

}
