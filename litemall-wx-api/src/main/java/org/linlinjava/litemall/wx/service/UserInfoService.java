package org.linlinjava.litemall.wx.service;

import lombok.extern.slf4j.Slf4j;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.security.util.JwtTokenUtil;
import org.linlinjava.litemall.wx.domain.LitemallUserDetails;
import org.linlinjava.litemall.wx.dto.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class UserInfoService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private LitemallUserService userService;

    public UserInfo getInfo(Integer userId) {
        LitemallUser user = userService.findById(userId);
        Assert.state(user != null, "用户不存在");
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName(user.getNickname());
        userInfo.setAvatarUrl(user.getAvatar());
        return userInfo;
    }

    public LitemallUserDetails loadUserByUsername(String username){
        List<LitemallUser> memberList = userService.queryByUsername(username);
        if (!CollectionUtils.isEmpty(memberList)) {
            return new LitemallUserDetails(memberList.get(0));
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    public String login(String username, String password) {
        String token = null;
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            log.error("登录异常:{}", e.getMessage());
        }
        return token;
    }

    public LitemallUser getCurrentMember(){
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        LitemallUserDetails memberDetails = (LitemallUserDetails) auth.getPrincipal();
        return memberDetails.getMallUser();
    }
}
