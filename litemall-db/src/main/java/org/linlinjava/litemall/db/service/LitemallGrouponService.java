package org.linlinjava.litemall.db.service;

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
     * 查询用户所有参与的团购
     *
     * @param userId
     * @return
     */
    public List<LitemallGroupon> queryByUserId(Integer userId) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    public List<LitemallGroupon> queryMyGroupon(Integer userId) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        example.or().andUserIdEqualTo(userId).andCreatorUserIdEqualTo(userId).andGrouponIdEqualTo(0).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    public List<LitemallGroupon> queryMyJoinGroupon(Integer userId) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        example.or().andUserIdEqualTo(userId).andGrouponIdNotEqualTo(0).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    public LitemallGroupon queryByOrderId(Integer orderId) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 根据ID查询记录
     *
     * @param id
     * @return
     */
    public LitemallGroupon queryById(Integer id) {
        return mapper.selectByPrimaryKey(id);
    }

    /**
     * 返回某个发起的团购参与人数
     *
     * @param grouponId
     * @return
     */
    public int countGroupon(Integer grouponId) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        example.or().andGrouponIdEqualTo(grouponId).andDeletedEqualTo(false);
        return (int) mapper.countByExample(example);
    }

    /**
     * 返回某个团购活动参与人数
     *
     * @param rulesId
     * @return
     */
    public int countRules(Integer rulesId) {
        LitemallGrouponExample example = new LitemallGrouponExample();
        example.or().andRulesIdEqualTo(rulesId).andDeletedEqualTo(false);
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
}
