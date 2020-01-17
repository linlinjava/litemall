package org.linlinjava.litemall.db.service;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallCouponMapper;
import org.linlinjava.litemall.db.dao.LitemallCouponUserMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.domain.LitemallCoupon.Column;
import org.linlinjava.litemall.db.util.CouponConstant;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class LitemallCouponService {
    @Resource
    private LitemallCouponMapper couponMapper;
    @Resource
    private LitemallCouponUserMapper couponUserMapper;

    private Column[] result = new Column[]{Column.id, Column.name, Column.desc, Column.tag,
                                            Column.days, Column.startTime, Column.endTime,
                                            Column.discount, Column.min};

    /**
     * 查询，空参数
     *
     * @param offset
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    public List<LitemallCoupon> queryList(int offset, int limit, String sort, String order) {
        return queryList(LitemallCouponExample.newAndCreateCriteria(), offset, limit, sort, order);
    }

    /**
     * 查询
     *
     * @param criteria 可扩展的条件
     * @param offset
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    public List<LitemallCoupon> queryList(LitemallCouponExample.Criteria criteria, int offset, int limit, String sort, String order) {
        criteria.andTypeEqualTo(CouponConstant.TYPE_COMMON).andStatusEqualTo(CouponConstant.STATUS_NORMAL).andDeletedEqualTo(false);
        criteria.example().setOrderByClause(sort + " " + order);
        PageHelper.startPage(offset, limit);
        return couponMapper.selectByExampleSelective(criteria.example(), result);
    }

    public List<LitemallCoupon> queryAvailableList(Integer userId, int offset, int limit) {
        assert userId != null;
        // 过滤掉登录账号已经领取过的coupon
        LitemallCouponExample.Criteria c = LitemallCouponExample.newAndCreateCriteria();
        List<LitemallCouponUser> used = couponUserMapper.selectByExample(
                LitemallCouponUserExample.newAndCreateCriteria().andUserIdEqualTo(userId).example()
        );
        if(used!=null && !used.isEmpty()){
            c.andIdNotIn(used.stream().map(LitemallCouponUser::getCouponId).collect(Collectors.toList()));
        }
        return queryList(c, offset, limit, "add_time", "desc");
    }

    public List<LitemallCoupon> queryList(int offset, int limit) {
        return queryList(offset, limit, "add_time", "desc");
    }

    public LitemallCoupon findById(Integer id) {
        return couponMapper.selectByPrimaryKey(id);
    }


    public LitemallCoupon findByCode(String code) {
        LitemallCouponExample example = new LitemallCouponExample();
        example.or().andCodeEqualTo(code).andTypeEqualTo(CouponConstant.TYPE_CODE).andStatusEqualTo(CouponConstant.STATUS_NORMAL).andDeletedEqualTo(false);
        List<LitemallCoupon> couponList =  couponMapper.selectByExample(example);
        if(couponList.size() > 1){
            throw new RuntimeException("");
        }
        else if(couponList.size() == 0){
            return null;
        }
        else {
            return couponList.get(0);
        }
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

    public List<LitemallCoupon> querySelective(String name, Short type, Short status, Integer page, Integer limit, String sort, String order) {
        LitemallCouponExample example = new LitemallCouponExample();
        LitemallCouponExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (type != null) {
            criteria.andTypeEqualTo(type);
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return couponMapper.selectByExample(example);
    }

    public void add(LitemallCoupon coupon) {
        coupon.setAddTime(LocalDateTime.now());
        coupon.setUpdateTime(LocalDateTime.now());
        couponMapper.insertSelective(coupon);
    }

    public int updateById(LitemallCoupon coupon) {
        coupon.setUpdateTime(LocalDateTime.now());
        return couponMapper.updateByPrimaryKeySelective(coupon);
    }

    public void deleteById(Integer id) {
        couponMapper.logicalDeleteByPrimaryKey(id);
    }

    private String getRandomNum(Integer num) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        base += "0123456789";

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成优惠码
     *
     * @return 可使用优惠码
     */
    public String generateCode() {
        String code = getRandomNum(8);
        while(findByCode(code) != null){
            code = getRandomNum(8);
        }
        return code;
    }

    /**
     * 查询过期的优惠券:
     * 注意：如果timeType=0, 即基于领取时间有效期的优惠券，则优惠券不会过期
     *
     * @return
     */
    public List<LitemallCoupon> queryExpired() {
        LitemallCouponExample example = new LitemallCouponExample();
        example.or().andStatusEqualTo(CouponConstant.STATUS_NORMAL).andTimeTypeEqualTo(CouponConstant.TIME_TYPE_TIME).andEndTimeLessThan(LocalDateTime.now()).andDeletedEqualTo(false);
        return couponMapper.selectByExample(example);
    }
}
