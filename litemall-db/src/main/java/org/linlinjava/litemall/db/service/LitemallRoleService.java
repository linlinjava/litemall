package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallRoleMapper;
import org.linlinjava.litemall.db.domain.LitemallAdmin;
import org.linlinjava.litemall.db.domain.LitemallAdminExample;
import org.linlinjava.litemall.db.domain.LitemallRole;
import org.linlinjava.litemall.db.domain.LitemallRole.Column;
import org.linlinjava.litemall.db.domain.LitemallRoleExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ulongx
 * @date 2018/12/21
 */
@Service
public class LitemallRoleService {

    private final Column[] result = new Column[]{Column.id, Column.roleName, Column.description, Column.isBuiltin};

    @Resource
    private LitemallRoleMapper roleMapper;

    /**
     * 根据id 查找role信息
     * @param id
     * @return
     */
    public LitemallRole findById(Integer id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    public List<LitemallRole> querySelective(String roleName, Integer page, Integer limit, String sort, String order) {
        LitemallRoleExample example = new LitemallRoleExample();
        LitemallRoleExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(roleName)) {
            criteria.andRoleNameLike("%" + roleName + "%");
        }
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return roleMapper.selectByExampleSelective(example, result);
    }

    public int countSelective(String roleName, Integer page, Integer size, String sort, String order) {
        LitemallRoleExample example = new LitemallRoleExample();
        LitemallRoleExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(roleName)) {
            criteria.andRoleNameLike("%" + roleName + "%");
        }
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        return (int) roleMapper.countByExample(example);
    }

    /**
     * 根据角色找到对应角色
     * @param roleName
     * @return
     */
    public List<LitemallRole> findByRoleName(String roleName) {
        LitemallRoleExample roleExample = new LitemallRoleExample();
        roleExample.or().andRoleNameEqualTo(roleName);
        return roleMapper.selectByExample(roleExample);
    }

    /**
     * 新增角色, 并保持权限
     * @param role
     */
    public void add(LitemallRole role, String[] resources) {
        roleMapper.insert(role);

        if (resources != null && resources.length > 0){

        }
    }
}
