package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallCouponMapper;
import org.linlinjava.litemall.db.domain.LitemallCoupon;
import org.linlinjava.litemall.db.domain.LitemallCoupon.Column;
import org.linlinjava.litemall.db.domain.LitemallCouponExample;
import org.linlinjava.litemall.db.domain.LitemallCouponUser;
import org.linlinjava.litemall.db.util.CouponConstant;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallCouponService {
    @Resource
    private LitemallCouponMapper couponMapper;

    private Column[] result = new Column[]{Column.id, Column.name, Column.desc, Column.tag,
                                            Column.days, Column.startTime, Column.endTime,
                                            Column.discount, Column.min};

    /**
     * 查询
     *
     * @param offset
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    public List<LitemallCoupon> queryList(int offset, int limit, String sort, String order) {
        LitemallCouponExample example = new LitemallCouponExample();
        example.or().andTypeEqualTo(CouponConstant.TYPE_COMMON).andStatusEqualTo(CouponConstant.STATUS_NORMAL).andDeletedEqualTo(false);
        example.setOrderByClause(sort + " " + order);
        PageHelper.startPage(offset, limit);
        return couponMapper.selectByExampleSelective(example, result);
    }

    public int queryTotal() {
        LitemallCouponExample example = new LitemallCouponExample();
        example.or().andTypeEqualTo(CouponConstant.TYPE_COMMON).andStatusEqualTo(CouponConstant.STATUS_NORMAL).andDeletedEqualTo(false);
        return (int) couponMapper.countByExample(example);
    }

    public List<LitemallCoupon> queryList(int offset, int limit) {
        return queryList(offset, limit, "add_time", "desc");
    }

    public LitemallCoupon findById(Integer id) {
        return couponMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询新用户注册优惠券
     *
     * @return
     */
    public List<LitemallCoupon> queryRegister() {
        LitemallCouponExample example = new LitemallCouponExample();
        example.or().andTypeEqualTo(CouponConstant.TYPE_REGISTER).andStatusEqualTo(CouponConstant.STATUS_NORMAL).andDeletedEqualTo(false);
        return couponMapper.selectByExample(example);
    }
}
