package org.linlinjava.litemall.admin.web;

import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.util.bcrypt.BCryptPasswordEncoder;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.linlinjava.litemall.admin.util.AdminResponseCode.*;

@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {
    private final Log logger = LogFactory.getLog(AdminUserController.class);

    @Autowired
    private LitemallUserService userService;

    @RequiresPermissions("admin:user:list")
    @RequiresPermissionsDesc(menu={"用户管理" , "会员管理"}, button="查询")
    @GetMapping("/list")
    public Object list(String username, String mobile,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallUser> userList = userService.querySelective(username, mobile, page, limit, sort, order);
        long total = PageInfo.of(userList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", userList);

        return ResponseUtil.ok(data);
    }

    @GetMapping("/username")
    public Object username(@NotEmpty String username) {
        boolean exist = userService.checkByUsername(username);
        if (exist) {
            return ResponseUtil.ok("已存在");
        }
        return ResponseUtil.ok("不存在");
    }

    private Object validate(LitemallUser user) {
        String username = user.getUsername();
        if (StringUtils.isEmpty(user)) {
            return ResponseUtil.badArgument();
        }
        if (!RegexUtil.isUsername(username)) {
            return ResponseUtil.fail(USER_INVALID_NAME, "用户名不符合规定");
        }
        String password = user.getPassword();
        if (StringUtils.isEmpty(password) || password.length() < 6) {
            return ResponseUtil.fail(USER_INVALID_PASSWORD, "用户密码长度不能小于6");
        }
        String mobile = user.getMobile();
        if (StringUtils.isEmpty(mobile)) {
            return ResponseUtil.badArgument();
        }
        if (!RegexUtil.isMobileExact(mobile)) {
            return ResponseUtil.fail(USER_INVALID_MOBILE, "用户手机号码格式不正确");
        }
        return null;
    }

    @RequiresPermissions("admin:user:create")
    @RequiresPermissionsDesc(menu={"用户管理" , "会员管理"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallUser user) {
        Object error = validate(user);
        if (error != null) {
            return error;
        }
        String username = user.getUsername();
        String mobile = user.getMobile();
        List<LitemallUser> userList = userService.queryByUsername(username);
        if (userList.size() > 0) {
            return ResponseUtil.fail(USER_NAME_EXIST, "用户名已注册");
        }
        userList = userService.queryByMobile(mobile);
        if (userList.size() > 0) {
            return ResponseUtil.fail(USER_MOBILE_EXIST, "手机号已注册");
        }
        if (!RegexUtil.isMobileExact(mobile)) {
            return ResponseUtil.fail(USER_INVALID_MOBILE, "手机号格式不正确");
        }

        String password = user.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);

        userService.add(user);
        return ResponseUtil.ok(user);
    }

    @RequiresPermissions("admin:user:update")
    @RequiresPermissionsDesc(menu={"用户管理" , "会员管理"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallUser user) {
        Object error = validate(user);
        if (error != null) {
            return error;
        }
        // 用户密码加密存储
        String password = user.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);

        if (userService.updateById(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(user);
    }
}
