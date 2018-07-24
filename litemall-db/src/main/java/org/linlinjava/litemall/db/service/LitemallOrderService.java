package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallOrderMapper;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderExample;
import org.linlinjava.litemall.db.util.OrderUtil;
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
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return orderMapper.selectByExample(example);
    }

    public int count(Integer userId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
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
        example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return orderMapper.selectOneByExample(example);
    }

    public int countByOrderSn(Integer userId, String orderSn){
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return (int)orderMapper.countByExample(example);
    }

    // TODO 这里应该产生一个唯一的订单，但是实际上这里仍然存在两个订单相同的可能性
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
        example.setOrderByClause(LitemallOrder.Column.addTime.desc());
        LitemallOrderExample.Criteria criteria = example.or();
        criteria.andUserIdEqualTo(userId);
        if(orderStatus != null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        criteria.andDeletedEqualTo(false);
        return orderMapper.selectByExample(example);
    }

    public int countByOrderStatus(Integer userId, List<Short> orderStatus) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.or();
        criteria.andUserIdEqualTo(userId);
        if(orderStatus != null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        criteria.andDeletedEqualTo(false);
        return (int)orderMapper.countByExample(example);
    }

    public int update(LitemallOrder order) {
        return orderMapper.updateByPrimaryKeySelective(order);
    }

    public List<LitemallOrder> querySelective(Integer userId, String orderSn, List<Short> orderStatusArray, Integer page, Integer size, String sort, String order) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();

        if(userId != null){
            criteria.andUserIdEqualTo(userId);
        }
        if(!StringUtils.isEmpty(orderSn)){
            criteria.andOrderSnEqualTo(orderSn);
        }
        if(orderStatusArray != null && orderStatusArray.size() != 0){
            criteria.andOrderStatusIn(orderStatusArray);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return orderMapper.selectByExample(example);
    }

    public int countSelective(Integer userId, String orderSn, List<Short> orderStatusArray, Integer page, Integer size, String sort, String order) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();

        if(userId != null){
            criteria.andUserIdEqualTo(userId);
        }
        if(!StringUtils.isEmpty(orderSn)){
            criteria.andOrderSnEqualTo(orderSn);
        }
        criteria.andDeletedEqualTo(false);

        return (int)orderMapper.countByExample(example);
    }

    public void updateById(LitemallOrder order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    public void deleteById(Integer id) {
        orderMapper.logicalDeleteByPrimaryKey(id);
    }

    public int count() {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andDeletedEqualTo(false);
        return (int)orderMapper.countByExample(example);
    }

    public List<LitemallOrder> queryUnpaid() {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_CREATE).andDeletedEqualTo(false);
        return orderMapper.selectByExample(example);
    }

    public List<LitemallOrder> queryUnconfirm() {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_SHIP).andShipTimeIsNotNull().andDeletedEqualTo(false);
        return orderMapper.selectByExample(example);
    }

    public LitemallOrder findBySn(String orderSn) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return orderMapper.selectOneByExample(example);
    }
}
