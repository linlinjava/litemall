package org.linlinjava.litemall.wx.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by yoogo on 2020-01-31
 */
@Data
public class OrderGoodsVo {
    private Integer id;
    private String goodsName;
    private Short number;
    private String picUrl;
    private String[] specifications;
    private BigDecimal price;
}
