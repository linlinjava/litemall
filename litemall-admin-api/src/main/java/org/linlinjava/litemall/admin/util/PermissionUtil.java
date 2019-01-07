package org.linlinjava.litemall.admin.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class PermissionUtil {

    public static Map<RequiresPermissions, RequiresPermissionsDesc> findPermissions(ApplicationContext context, String basicPackage) {
        Map<String, Object> map = context.getBeansWithAnnotation(Controller.class);
        Map<RequiresPermissions, RequiresPermissionsDesc> permissions = new HashMap<>();
        for(Map.Entry<String, Object> entry : map.entrySet()){
            Object bean = entry.getValue();
            if(!StringUtils.contains(ClassUtils.getPackageName(bean.getClass()), basicPackage)){
                continue;
            }

            Class<?> clz = bean.getClass();
            Class controllerClz = clz.getSuperclass();
            List<Method> methods = MethodUtils.getMethodsListWithAnnotation(controllerClz, RequiresPermissions.class);
            for(Method method : methods){
                RequiresPermissions requiresPermissions = AnnotationUtils.getAnnotation(method, RequiresPermissions.class);
                RequiresPermissionsDesc requiresPermissionsDesc = AnnotationUtils.getAnnotation(method, RequiresPermissionsDesc.class);
                if(requiresPermissions == null || requiresPermissionsDesc == null){
                    continue;
                }
                permissions.put(requiresPermissions, requiresPermissionsDesc);
            }
        }
        return permissions;
    }

    public static List<PermVo> listPermissions(ApplicationContext context, String basicPackage) {
        List<PermVo> root = new ArrayList<>();
        Map<RequiresPermissions, RequiresPermissionsDesc> map = findPermissions(context, basicPackage);
        for(Map.Entry<RequiresPermissions, RequiresPermissionsDesc> entry : map.entrySet()) {
            RequiresPermissions requiresPermissions = entry.getKey();
            RequiresPermissionsDesc requiresPermissionsDesc = entry.getValue();

            String[] menus = requiresPermissionsDesc.menu();
            if(menus.length != 2){
                throw new RuntimeException("目前只支持两级菜单");
            }
            String menu1 = menus[0];
            PermVo perm1 = null;
            for(PermVo permVo : root){
                if(permVo.getLabel().equals(menu1)){
                    perm1 = permVo;
                    break;
                }
            }
            if(perm1 == null){
                perm1 = new PermVo();
                perm1.setId(menu1);
                perm1.setLabel(menu1);
                perm1.setChildren(new ArrayList<>());
                root.add(perm1);
            }
            String menu2 = menus[1];
            PermVo perm2 = null;
            for(PermVo permVo : perm1.getChildren()){
                if(permVo.getLabel().equals(menu2)){
                    perm2 = permVo;
                    break;
                }
            }
            if(perm2 == null){
                perm2 = new PermVo();
                perm2.setId(menu2);
                perm2.setLabel(menu2);
                perm2.setChildren(new ArrayList<>());
                perm1.getChildren().add(perm2);
            }

            PermVo leftPerm = new PermVo();
            leftPerm.setId(requiresPermissions.value()[0]);
            leftPerm.setLabel(requiresPermissionsDesc.button());

            perm2.getChildren().add(leftPerm);
        }
        return root;
    }
}
