package org.linlinjava.litemall.wx.vo;

import lombok.Data;
import org.linlinjava.litemall.db.util.OrderHandleOption;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by yoogo on 2020-01-31
 */
@Data
public class OrderVo {
    private Integer id;
    private String orderSn;
    private BigDecimal actualPrice;
    private String orderStatusText;
    private OrderHandleOption handleOption;
    private Boolean isGroupin;
    private List<OrderGoodsVo> orderGoodsVo;
}
