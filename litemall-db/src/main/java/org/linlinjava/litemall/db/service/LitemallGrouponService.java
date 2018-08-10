package org.linlinjava.litemall.db.service;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallGrouponMapper;
import org.linlinjava.litemall.db.domain.LitemallGroupon;
import org.linlinjava.litemall.db.domain.LitemallGrouponExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallGrouponService {
    @Resource
    LitemallGrouponMapper mapper;

    /**
     * 获取用户发起的团购记录
     *
     * @param userId
     * @return
     */
    public List<LitemallGroupon> queryMyGroupon(Integer userId) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        example.or().andUserIdEqualTo(userId).andCreatorUserIdEqualTo(userId).andGrouponIdEqualTo(0).andDeletedEqualTo(false).andPayedEqualTo(true);
        example.orderBy("add_time desc");
        return mapper.selectByExample(example);
    }

    /**
     * 获取用户参与的团购记录
     *
     * @param userId
     * @return
     */
    public List<LitemallGroupon> queryMyJoinGroupon(Integer userId) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        example.or().andUserIdEqualTo(userId).andGrouponIdNotEqualTo(0).andDeletedEqualTo(false).andPayedEqualTo(true);
        example.orderBy("add_time desc");
        return mapper.selectByExample(example);
    }

    /**
     * 根据OrderId查询团购记录
     *
     * @param orderId
     * @return
     */
    public LitemallGroupon queryByOrderId(Integer orderId) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 获取某个团购活动参与的记录
     *
     * @param id
     * @return
     */
    public List<LitemallGroupon> queryJoinRecord(Integer id) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        example.or().andGrouponIdEqualTo(id).andDeletedEqualTo(false).andPayedEqualTo(true);
        example.orderBy("add_time desc");
        return mapper.selectByExample(example);
    }

    /**
     * 根据ID查询记录
     *
     * @param id
     * @return
     */
    public LitemallGroupon queryById(Integer id) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false).andPayedEqualTo(true);
        return mapper.selectOneByExample(example);
    }

    /**
     * 返回某个发起的团购参与人数
     *
     * @param grouponId
     * @return
     */
    public int countGroupon(Integer grouponId) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        example.or().andGrouponIdEqualTo(grouponId).andDeletedEqualTo(false).andPayedEqualTo(true);
        return (int) mapper.countByExample(example);
    }

    public void update(LitemallGroupon groupon) {
        mapper.updateByPrimaryKey(groupon);
    }

    /**
     * 创建或参与一个团购
     *
     * @param groupon
     * @return
     */
    public int createGroupon(LitemallGroupon groupon) {
        return mapper.insertSelective(groupon);
    }


    /**
     * 查询所有发起的团购记录
     *
     * @param rulesId
     * @param page
     * @param size
     * @param sort
     * @param order
     * @return
     */
    public List<LitemallGroupon> querySelective(String rulesId, Integer page, Integer size, String sort, String order) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        LitemallGrouponExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(rulesId)) {
            criteria.andRulesIdEqualTo(Integer.parseInt(rulesId));
        }
        criteria.andDeletedEqualTo(false);
        criteria.andPayedEqualTo(true);
        criteria.andGrouponIdEqualTo(0);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }

    public int countSelective(String rulesId, Integer page, Integer limit, String sort, String order) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        LitemallGrouponExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(rulesId)) {
            criteria.andRulesIdEqualTo(Integer.parseInt(rulesId));
        }
        criteria.andDeletedEqualTo(false);
        criteria.andPayedEqualTo(true);
        criteria.andGrouponIdEqualTo(0);

        return (int) mapper.countByExample(example);
    }
}
