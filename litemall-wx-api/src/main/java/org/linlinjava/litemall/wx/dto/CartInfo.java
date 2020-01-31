package org.linlinjava.litemall.wx.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by yoogo on 2020-01-30
 */
@Data
public class CartInfo {
    @NotNull
    private Integer cartId;
    @NotNull
    private Integer addressId;
    @NotNull
    private Integer couponId;

    private Integer userCouponId;

    private String message;

    private Integer grouponRulesId;

    private Integer grouponLinkId;
}
