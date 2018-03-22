package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallAddressMapper;
import org.linlinjava.litemall.db.domain.LitemallAddress;
import org.linlinjava.litemall.db.domain.LitemallAddressExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallAddressService {
    @Resource
    private LitemallAddressMapper addressMapper;

    public List<LitemallAddress> queryByUid(Integer uid) {
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(uid);
        return addressMapper.selectByExample(example);
    }

    public LitemallAddress findById(Integer id) {
        LitemallAddress address = addressMapper.selectByPrimaryKey(id);
        if(address == null){
            return null;
        }
        return address;
    }

    public int add(LitemallAddress address) {
        return addressMapper.insertSelective(address);
    }

    public int update(LitemallAddress address) {
        return addressMapper.updateByPrimaryKeySelective(address);
    }

    public int delete(Integer id) {
        return addressMapper.deleteByPrimaryKey(id);
    }

    public LitemallAddress findDefault(Integer userId) {
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(userId).andIsDefaultEqualTo(true);
        return addressMapper.selectOneByExample(example);
    }

    public void resetDefault(Integer userId) {
        LitemallAddress address = new LitemallAddress();
        address.setIsDefault(false);
        LitemallAddressExample example = new LitemallAddressExample();
        example.or().andUserIdEqualTo(userId);
        addressMapper.updateByExampleSelective(address, example);
    }

    public List<LitemallAddress> querySelective(Integer userId, String name, Integer page, Integer limit, String sort, String order) {
        LitemallAddressExample example = new LitemallAddressExample();
        LitemallAddressExample.Criteria criteria = example.createCriteria();

        if(userId !=  null){
            criteria.andUserIdEqualTo(userId);
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        PageHelper.startPage(page, limit);
        return addressMapper.selectByExample(example);
    }

    public int countSelective(Integer userId, String name, Integer page, Integer limit, String sort, String order) {
        LitemallAddressExample example = new LitemallAddressExample();
        LitemallAddressExample.Criteria criteria = example.createCriteria();

        if(userId !=  null){
            criteria.andUserIdEqualTo(userId);
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        return (int)addressMapper.countByExample(example);
    }

    public void updateById(LitemallAddress address) {
        addressMapper.updateByPrimaryKeySelective(address);
    }
}
