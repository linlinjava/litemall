package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallOrderMapper;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class LitemallOrderService {
    @Resource
    private LitemallOrderMapper orderMapper;

    public int add(LitemallOrder order) {
        return orderMapper.insertSelective(order);
    }

    public List<LitemallOrder> query(Integer userId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId);
        return orderMapper.selectByExample(example);
    }

    public int count(Integer userId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId);
        return (int)orderMapper.countByExample(example);
    }

    public LitemallOrder findById(Integer orderId) {
        return orderMapper.selectByPrimaryKey(orderId);
    }

    private String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public LitemallOrder queryByOrderSn(Integer userId, String orderSn){
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn);
        List<LitemallOrder> orderList = orderMapper.selectByExample(example);
        return orderMapper.selectOneByExample(example);
    }

    public int countByOrderSn(Integer userId, String orderSn){
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn);
        return (int)orderMapper.countByExample(example);
    }

    public String generateOrderSn(Integer userId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = df.format(LocalDate.now());
        String orderSn = now + getRandomNum(6);
        while(countByOrderSn(userId, orderSn) != 0){
            orderSn = getRandomNum(6);
        }
        return orderSn;
    }

    public List<LitemallOrder> queryByOrderStatus(Integer userId, List<Short> orderStatus) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.orderBy(LitemallOrder.Column.addTime.desc());
        LitemallOrderExample.Criteria criteria = example.or();
        criteria.andUserIdEqualTo(userId).andIsDeleteEqualTo(false);
        if(orderStatus != null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        return orderMapper.selectByExample(example);
    }

    public int countByOrderStatus(Integer userId, List<Short> orderStatus) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.or();
        criteria.andUserIdEqualTo(userId).andIsDeleteEqualTo(false);
        if(orderStatus != null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        return (int)orderMapper.countByExample(example);
    }

    public int update(LitemallOrder order) {
        return orderMapper.updateByPrimaryKeySelective(order);
    }

    public List<LitemallOrder> querySelective(Integer userId, String orderSn, Integer page, Integer size, String sort, String order) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();

        if(userId != null){
            criteria.andUserIdEqualTo(userId);
        }
        if(!StringUtils.isEmpty(orderSn)){
            criteria.andOrderSnEqualTo(orderSn);
        }
        PageHelper.startPage(page, size);
        return orderMapper.selectByExample(example);
    }

    public int countSelective(Integer userId, String orderSn, Integer page, Integer size, String sort, String order) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();

        if(userId != null){
            criteria.andUserIdEqualTo(userId);
        }
        if(!StringUtils.isEmpty(orderSn)){
            criteria.andOrderSnEqualTo(orderSn);
        }
        return (int)orderMapper.countByExample(example);
    }

    public void updateById(LitemallOrder brand) {
        orderMapper.updateByPrimaryKeySelective(brand);
    }

    public void deleteById(Integer id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    public int count() {
        LitemallOrderExample example = new LitemallOrderExample();
        return (int)orderMapper.countByExample(example);
    }
}
