package org.linlinjava.litemall.admin.web;

import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.admin.service.AdminTokenManager;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.util.bcrypt.BCryptPasswordEncoder;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallAdmin;
import org.linlinjava.litemall.db.service.LitemallAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/admin")
@Validated
public class AdminAdminController {
    @Autowired
    private LitemallAdminService adminService;

    @GetMapping("/info")
    public Object info(String token){
        Integer adminId = AdminTokenManager.getUserId(token);
        if(adminId == null){
            return ResponseUtil.badArgumentValue();
        }
        LitemallAdmin admin = adminService.findById(adminId);
        if(admin == null){
            return ResponseUtil.badArgumentValue();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", admin.getUsername());
        data.put("avatar", admin.getAvatar());

        // 目前roles不支持，这里简单设置admin
        List<String> roles = new ArrayList<>();
        roles.add("admin");
        data.put("roles", roles);
        data.put("introduction", "admin introduction");
        return ResponseUtil.ok(data);
    }

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String username,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        List<LitemallAdmin> adminList = adminService.querySelective(username, page, limit, sort, order);
        int total = adminService.countSelective(username, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", adminList);

        return ResponseUtil.ok(data);
    }

    private Object validate(LitemallAdmin admin) {
        String name = admin.getUsername();
        if(StringUtils.isEmpty(name)){
            return ResponseUtil.badArgument();
        }
        if(RegexUtil.isUsername(name)){
            return ResponseUtil.fail(402, "管理员名称不符合规定");
        }
        String password = admin.getPassword();
        if(StringUtils.isEmpty(password) || password.length() < 6){
            return ResponseUtil.fail(402, "管理员密码长度不能小于6");
        }
        return null;
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallAdmin admin){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        Object error = validate(admin);
        if(error != null){
            return error;
        }

        String username = admin.getUsername();
        List<LitemallAdmin> adminList = adminService.findAdmin(username);
        if(adminList.size() > 0){
            return ResponseUtil.fail(402, "管理员已经存在");
        }

        String rawPassword = admin.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        admin.setPassword(encodedPassword);
        admin.setAddTime(LocalDateTime.now());

        adminService.add(admin);
        return ResponseUtil.ok(admin);
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, @NotNull Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        LitemallAdmin admin = adminService.findById(id);
        return ResponseUtil.ok(admin);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallAdmin admin){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        Object error = validate(admin);
        if(error != null){
            return error;
        }

        Integer anotherAdminId = admin.getId();
        if(anotherAdminId == null){
            return ResponseUtil.badArgument();
        }
        // TODO 这里开发者需要删除以下检验代码
        // 目前这里不允许修改超级管理员是防止演示平台上他人修改管理员密码而导致登录失败
        if(anotherAdminId == 1){
            return ResponseUtil.fail(403, "超级管理员不能修改");
        }

        String rawPassword = admin.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        admin.setPassword(encodedPassword);

        if(adminService.updateById(admin) == 0){
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(admin);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallAdmin admin){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        Integer anotherAdminId = admin.getId();
        if(anotherAdminId == null){
            return ResponseUtil.badArgument();
        }
        // TODO 这里开发者需要删除以下检验代码
        // 目前这里不允许删除超级管理员是防止演示平台上他人删除管理员账号而导致登录失败
        if(anotherAdminId == 1){
            return ResponseUtil.fail(403, "超级管理员不能删除");
        }

        adminService.deleteById(anotherAdminId);
        return ResponseUtil.ok();
    }
}
