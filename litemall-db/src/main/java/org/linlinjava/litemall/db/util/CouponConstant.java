package org.linlinjava.litemall.db.util;

/**
 * 优惠券相关枚举值常量。
 *
 * <p>与 litemall_coupon、litemall_coupon_user 表中的 tinyint 列一一对应，
 * 数据库中没有外键约束，取值合法性由业务层保证。
 *
 * @author lvweihao0403
 * @since 1.0.0
 */
public class CouponConstant {
    /** 0 通用券：任意商品可用 */
    public static final Short TYPE_COMMON = 0;
    /** 1 注册赠券：新用户注册时发放 */
    public static final Short TYPE_REGISTER = 1;
    /** 2 口令券：凭口令领取 */
    public static final Short TYPE_CODE = 2;

    /** 0 全场通用 */
    public static final Short GOODS_TYPE_ALL = 0;
    /** 1 限定类目，适用范围见 litemall_coupon.goods_value */
    public static final Short GOODS_TYPE_CATEGORY = 1;
    /** 2 限定商品，适用范围见 litemall_coupon.goods_value */
    public static final Short GOODS_TYPE_ARRAY = 2;

    /** 0 正常可用 */
    public static final Short STATUS_NORMAL = 0;
    /** 1 已过期 */
    public static final Short STATUS_EXPIRED = 1;
    /** 2 已领完 */
    public static final Short STATUS_OUT = 2;

    /** 0 按领取后天数计算有效期，天数见 litemall_coupon.days */
    public static final Short TIME_TYPE_DAYS = 0;
    /** 1 按固定起止时间计算有效期，见 start_time / end_time */
    public static final Short TIME_TYPE_TIME = 1;
}
