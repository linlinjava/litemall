package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallUserFormidMapper;
import org.linlinjava.litemall.db.domain.LitemallUserFormid;
import org.linlinjava.litemall.db.domain.LitemallUserFormidExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class LitemallUserFormIdService {
    @Resource
    private LitemallUserFormidMapper formidMapper;

    /**
     * 查找是否有可用的FormId
     *
     * @param openId
     * @return
     */
    public LitemallUserFormid queryByOpenId(String openId) {
        LitemallUserFormidExample example = new LitemallUserFormidExample();
        //符合找到该用户记录，且可用次数大于1，且还未过期
        example.or().andOpenidEqualTo(openId).andExpireTimeGreaterThan(LocalDateTime.now());
        return formidMapper.selectOneByExample(example);
    }

    /**
     * 添加一个 FormId
     *
     * @param userFormid
     */
    public void addUserFormid(LitemallUserFormid userFormid) {
        formidMapper.insertSelective(userFormid);
    }

    /**
     * 删除一个 FormId
     *
     * @param userFormidId
     */
    public void delUserFormid(Integer userFormidId) {
        formidMapper.deleteByPrimaryKey(userFormidId);
    }
}
