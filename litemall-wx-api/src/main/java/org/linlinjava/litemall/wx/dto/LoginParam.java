package org.linlinjava.litemall.wx.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by yoogo on 2020-02-01
 */
@Data
public class LoginParam {
    @NotNull
    private String username;
    @NotNull
    private String password;
}
