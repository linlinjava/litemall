package org.linlinjava.litemall.db.service;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallGrouponRulesMapper;
import org.linlinjava.litemall.db.domain.LitemallGrouponRules;
import org.linlinjava.litemall.db.domain.LitemallGrouponRulesExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallGrouponRulesService {
    @Resource
    LitemallGrouponRulesMapper mapper;

    public int createRules(LitemallGrouponRules rules) {
        return mapper.insertSelective(rules);
    }

    /**
     * 根据ID查找对应团购项
     *
     * @param id
     * @return
     */
    public LitemallGrouponRules queryById(Integer id) {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return mapper.selectOneByExample(example);
    }

    /**
     * 查询某个商品关联的团购规则
     *
     * @param goodsId
     * @return
     */
    public List<LitemallGrouponRules> queryByGoodsId(Integer goodsId) {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return mapper.selectByExample(example);
    }

    /**
     * 获取首页团购活动列表
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallGrouponRules> queryByIndex(int offset, int limit) {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        example.or().andDeletedEqualTo(false);
        example.orderBy("add_time desc");
        PageHelper.startPage(offset, limit);
        return mapper.selectByExample(example);
    }

    /**
     * 判断某个团购活动是否已经过期
     *
     * @return
     */
    public boolean isExpired(LitemallGrouponRules rules) {
        if (rules == null || rules.getExpireTime().isBefore(LocalDateTime.now()))
            return false;
        return true;
    }

    /**
     * 获取团购活动列表
     *
     * @param goodsId
     * @param page
     * @param size
     * @param sort
     * @param order
     * @return
     */
    public List<LitemallGrouponRules> querySelective(String goodsId, Integer page, Integer size, String sort, String order) {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        LitemallGrouponRulesExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andGoodsIdEqualTo(Integer.parseInt(goodsId));
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return mapper.selectByExample(example);
    }

    public int countSelective(String goodsId, Integer page, Integer limit, String sort, String order) {
        LitemallGrouponRulesExample example = new LitemallGrouponRulesExample();
        LitemallGrouponRulesExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andGoodsIdEqualTo(Integer.parseInt(goodsId));
        }
        criteria.andDeletedEqualTo(false);

        return (int) mapper.countByExample(example);
    }

    public void delete(Integer id) {
        mapper.logicalDeleteByPrimaryKey(id);
    }

    public void update(LitemallGrouponRules grouponRules) {
        mapper.updateByPrimaryKeySelective(grouponRules);
    }
}
