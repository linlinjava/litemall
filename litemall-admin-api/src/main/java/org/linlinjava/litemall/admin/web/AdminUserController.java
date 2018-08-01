package org.linlinjava.litemall.admin.web;

import com.github.pagehelper.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.bcrypt.BCryptPasswordEncoder;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {
    private final Log logger = LogFactory.getLog(AdminUserController.class);

    @Autowired
    private LitemallUserService userService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String username, String mobile,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        List<LitemallUser> userList = userService.querySelective(username, mobile, page, limit, sort, order);
        int total = userService.countSeletive(username, mobile, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", userList);

        return ResponseUtil.ok(data);
    }

    @GetMapping("/username")
    public Object username(String username){
        if(StringUtil.isEmpty(username)){
            return ResponseUtil.badArgument();
        }

        int total = userService.countSeletive(username, null, null, null, null, null);
        if(total == 0){
            return ResponseUtil.ok("不存在");
        }
        return ResponseUtil.ok("已存在");
    }


    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallUser user){
        logger.debug(user);

        String username = user.getUsername();
        String mobile = user.getMobile();
        List<LitemallUser> userList = userService.queryByUsername(username);
        if(userList.size() > 0){
            return ResponseUtil.fail(403, "用户名已注册");
        }
        userList = userService.queryByMobile(mobile);
        if(userList.size() > 0){
            return ResponseUtil.fail(403, "手机号已注册");
        }
        if(!RegexUtil.isMobileExact(mobile)){
            return ResponseUtil.fail(403, "手机号格式不正确");
        }

        String password = user.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);

        user.setAddTime(LocalDateTime.now());
        userService.add(user);
        return ResponseUtil.ok(user);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallUser user){
        logger.debug(user);

        // 用户密码加密存储
        String password = user.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);

        userService.update(user);
        return ResponseUtil.ok(user);
    }
}
