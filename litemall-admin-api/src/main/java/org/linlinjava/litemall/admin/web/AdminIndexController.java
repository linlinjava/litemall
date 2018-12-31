package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.*;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/index")
public class AdminIndexController {
    private final Log logger = LogFactory.getLog(AdminIndexController.class);

    @RequestMapping("/index")
    public Object index() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

    @RequiresGuest
    @RequestMapping("/guest")
    public Object guest() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

    @RequiresAuthentication
    @RequestMapping("/authn")
    public Object authn() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

    @RequiresUser
    @RequestMapping("/user")
    public Object user() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

    @RequiresRoles("admin")
    @RequestMapping("/admin")
    public Object admin() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

    @RequiresRoles("admin2")
    @RequestMapping("/admin2")
    public Object admin2() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

    @RequiresPermissions("index:permission:read")
    @RequestMapping("/read")
    public Object read() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

    @RequiresPermissions("index:permission:write")
    @RequestMapping("/write")
    public Object write() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

}
