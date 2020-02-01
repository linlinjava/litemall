package org.linlinjava.litemall.wx.domain;

import org.linlinjava.litemall.db.domain.LitemallUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Created by yoogo on 2020-02-01
 */
public class LitemallUserDetails implements UserDetails {
    private LitemallUser mallUser;

    public LitemallUserDetails(LitemallUser mallUser) {
        this.mallUser = mallUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return mallUser.getPassword();
    }

    @Override
    public String getUsername() {
        return mallUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public LitemallUser getMallUser() {
        return mallUser;
    }

    public String getMobile() {
        return mallUser.getMobile();
    }
}
