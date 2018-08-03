package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallGrouponRulesMapper;
import org.linlinjava.litemall.db.domain.LitemallGrouponRules;
import org.linlinjava.litemall.db.domain.LitemallGrouponRulesExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallGrouponRulesService {
    @Resource
    LitemallGrouponRulesMapper mapper;

    public int createRules(LitemallGrouponRules rules) {
        return mapper.insertSelective(rules);
    }

    public LitemallGrouponRules queryById(Integer id) {
        return mapper.selectByPrimaryKey(id);
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
}
