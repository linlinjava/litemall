package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallAdminMapper;
import org.linlinjava.litemall.db.dao.LitemallAdminRoleMapper;
import org.linlinjava.litemall.db.dao.LitemallResourceMapper;
import org.linlinjava.litemall.db.dao.LitemallRoleResourceMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.domain.LitemallAdmin.Column;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LitemallAdminService {
    private final Column[] result = new Column[]{Column.id, Column.username, Column.avatar};
    @Resource
    private LitemallAdminMapper adminMapper;

    @Resource
    private LitemallAdminRoleMapper adminRoleMapper;

    @Resource
    private LitemallRoleResourceMapper roleResourceMapper;

    @Resource
    private LitemallResourceMapper resourceMapper;

    public List<LitemallAdmin> findAdmin(String username) {
        LitemallAdminExample example = new LitemallAdminExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return adminMapper.selectByExample(example);
    }

    public LitemallAdmin findAdmin(Integer id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    public List<LitemallAdmin> querySelective(String username, Integer page, Integer limit, String sort, String order) {
        LitemallAdminExample example = new LitemallAdminExample();
        LitemallAdminExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return adminMapper.selectByExampleSelective(example, result);
    }

    public int countSelective(String username, Integer page, Integer size, String sort, String order) {
        LitemallAdminExample example = new LitemallAdminExample();
        LitemallAdminExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        criteria.andDeletedEqualTo(false);

        return (int) adminMapper.countByExample(example);
    }

    public int updateById(LitemallAdmin admin) {
        admin.setUpdateTime(LocalDateTime.now());
        return adminMapper.updateByPrimaryKeySelective(admin);
    }

    public void deleteById(Integer id) {
        adminMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallAdmin admin) {
        admin.setAddTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.insertSelective(admin);
    }

    public LitemallAdmin findById(Integer id) {
        return adminMapper.selectByPrimaryKeySelective(id, result);
    }

    public  List<LitemallResource> querySelective(Integer adminId){
//        LitemallAdminRoleExample example = new LitemallAdminRoleExample();
//        example.createCriteria().andAdminIdEqualTo(adminId);
//        List<LitemallAdminRole> litemallAdminRoles = adminRoleMapper.selectByExampleSelective(example);
//
//        List<Integer> integers = new ArrayList<>();
//        for (LitemallAdminRole lar : litemallAdminRoles){
//            integers.add(lar.getRoleId());
//        }
//        LitemallRoleResourceExample example1 = new LitemallRoleResourceExample();
//        example1.createCriteria().andRoleIdIn(integers);
//        example1.setDistinct(true);
//        List<LitemallRoleResource> roleResources = roleResourceMapper.selectByExampleSelective(example1,new LitemallRoleResource.Column[]{ LitemallRoleResource.Column.resourceId });
//        integers.clear();
//        for (LitemallRoleResource roleRes : roleResources){
//            integers.add(roleRes.getResourceId());
//        }
//        LitemallResourceExample resourceExample = new LitemallResourceExample();
//        resourceExample.createCriteria().andIdIn(integers);
//        List<LitemallResource>  resourceList = resourceMapper.selectByExampleSelective(resourceExample,new LitemallResource.Column[]{LitemallResource.Column.routerText});


        List<LitemallResource>  resourceList = resourceMapper.getUserRoles(adminId);


        return  resourceList;
    }

}
