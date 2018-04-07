package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.domain.LitemallAd;
import org.linlinjava.litemall.db.domain.LitemallAdExample;
import org.linlinjava.litemall.db.dao.LitemallAdMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallAdService {
    @Resource
    private LitemallAdMapper adMapper;

    public List<LitemallAd> queryByApid(Integer i) {
        LitemallAdExample example = new LitemallAdExample();
        example.or().andPositionEqualTo(i).andDeletedEqualTo(false);
        return adMapper.selectByExample(example);
    }

    public List<LitemallAd> querySelective(String name, String content, Integer page, Integer limit, String sort, String order) {
        LitemallAdExample example = new LitemallAdExample();
        LitemallAdExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        if(!StringUtils.isEmpty(content)){
            criteria.andContentLike("%" + content + "%");
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, limit);
        return adMapper.selectByExample(example);
    }

    public int countSelective(String name, String content, Integer page, Integer size, String sort, String order) {
        LitemallAdExample example = new LitemallAdExample();
        LitemallAdExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        if(!StringUtils.isEmpty(content)){
            criteria.andContentLike("%" + content + "%");
        }
        criteria.andDeletedEqualTo(false);

        return (int)adMapper.countByExample(example);
    }

    public void updateById(LitemallAd ad) {
        adMapper.updateByPrimaryKeySelective(ad);
    }

    public void deleteById(Integer id) {
        LitemallAd ad = adMapper.selectByPrimaryKey(id);
        if(ad == null){
            return;
        }
        ad.setDeleted(true);
        adMapper.updateByPrimaryKey(ad);
    }

    public void add(LitemallAd ad) {
        adMapper.insertSelective(ad);
    }

    public LitemallAd findById(Integer id) {
        return adMapper.selectByPrimaryKey(id);
    }
}
