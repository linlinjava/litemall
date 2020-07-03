package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.util.bcrypt.BCryptPasswordEncoder;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {
    private final Log logger = LogFactory.getLog(AdminUserController.class);

    @Autowired
    private LitemallUserService userService;

    @RequiresPermissions("admin:user:list")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String username, String mobile,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallUser> userList = userService.querySelective(username, mobile, page, limit, sort, order);
        return ResponseUtil.okList(userList);
    }

    @RequiresPermissions("admin:user:edit")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "编辑")
    @PostMapping("/edit")
    public Object edit(LitemallUser user) {
        Object error = validate(user);
        if (error != null) {
            return error;
        }
        LitemallUser oldUser = userService.findById(user.getId());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String newPassword = user.getPassword();

        if (newPassword != null && !"".equals(newPassword)) {
            String encodePassword = encoder.encode(newPassword);
            user.setPassword(encodePassword);
        } else {
            user.setPassword(oldUser.getPassword());
        }

        user.setUpdateTime(LocalDateTime.now());

        if (userService.updateById(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok();
    }

    private Object validate(LitemallUser user) {
        if (user == null) {
            return ResponseUtil.badArgument();
        }

        return null;
    }
}
