package org.linlinjava.litemall.admin.util;

import org.linlinjava.litemall.db.domain.LitemallRole;

/**
 * @author ulongx
 * @date 2018/12/23
 */
public class RoleVo extends LitemallRole {

   private String[] resources;

    public String[] getResources() {
        return resources;
    }

    public void setResources(String[] resources) {
        this.resources = resources;
    }
}
