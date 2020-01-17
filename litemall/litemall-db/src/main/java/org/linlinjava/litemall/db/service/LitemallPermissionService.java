package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallPermissionMapper;
import org.linlinjava.litemall.db.dao.LitemallRoleMapper;
import org.linlinjava.litemall.db.domain.LitemallPermission;
import org.linlinjava.litemall.db.domain.LitemallPermissionExample;
import org.linlinjava.litemall.db.domain.LitemallRole;
import org.linlinjava.litemall.db.domain.LitemallRoleExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LitemallPermissionService {
    @Resource
    private LitemallPermissionMapper permissionMapper;

    public Set<String> queryByRoleIds(Integer[] roleIds) {
        Set<String> permissions = new HashSet<String>();
        if(roleIds.length == 0){
            return permissions;
        }

        LitemallPermissionExample example = new LitemallPermissionExample();
        example.or().andRoleIdIn(Arrays.asList(roleIds)).andDeletedEqualTo(false);
        List<LitemallPermission> permissionList = permissionMapper.selectByExample(example);

        for(LitemallPermission permission : permissionList){
            permissions.add(permission.getPermission());
        }

        return permissions;
    }


    public Set<String> queryByRoleId(Integer roleId) {
        Set<String> permissions = new HashSet<String>();
        if(roleId == null){
            return permissions;
        }

        LitemallPermissionExample example = new LitemallPermissionExample();
        example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false);
        List<LitemallPermission> permissionList = permissionMapper.selectByExample(example);

        for(LitemallPermission permission : permissionList){
            permissions.add(permission.getPermission());
        }

        return permissions;
    }

    public boolean checkSuperPermission(Integer roleId) {
        if(roleId == null){
            return false;
        }

        LitemallPermissionExample example = new LitemallPermissionExample();
        example.or().andRoleIdEqualTo(roleId).andPermissionEqualTo("*").andDeletedEqualTo(false);
        return permissionMapper.countByExample(example) != 0;
    }

    public void deleteByRoleId(Integer roleId) {
        LitemallPermissionExample example = new LitemallPermissionExample();
        example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false);
        permissionMapper.logicalDeleteByExample(example);
    }

    public void add(LitemallPermission litemallPermission) {
        litemallPermission.setAddTime(LocalDateTime.now());
        litemallPermission.setUpdateTime(LocalDateTime.now());
        permissionMapper.insertSelective(litemallPermission);
    }
}
