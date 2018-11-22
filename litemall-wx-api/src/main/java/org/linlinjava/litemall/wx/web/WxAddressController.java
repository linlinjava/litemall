package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallAddress;
import org.linlinjava.litemall.db.service.LitemallAddressService;
import org.linlinjava.litemall.db.service.LitemallRegionService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户收货地址服务
 */
@RestController
@RequestMapping("/wx/address")
@Validated
public class WxAddressController {
    private final Log logger = LogFactory.getLog(WxAddressController.class);

    @Autowired
    private LitemallAddressService addressService;
    @Autowired
    private LitemallRegionService regionService;

    /**
     * 用户收货地址列表
     *
     * @param userId 用户ID
     * @return 收货地址列表
     */
    @GetMapping("list")
    public Object list(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        List<LitemallAddress> addressList = addressService.queryByUid(userId);
        List<Map<String, Object>> addressVoList = new ArrayList<>(addressList.size());
        for (LitemallAddress address : addressList) {
            Map<String, Object> addressVo = new HashMap<>();
            addressVo.put("id", address.getId());
            addressVo.put("name", address.getName());
            addressVo.put("mobile", address.getMobile());
            addressVo.put("isDefault", address.getIsDefault());
            String province = regionService.findById(address.getProvinceId()).getName();
            String city = regionService.findById(address.getCityId()).getName();
            String area = regionService.findById(address.getAreaId()).getName();
            String addr = address.getAddress();
            String detailedAddress = province + city + area + " " + addr;
            addressVo.put("detailedAddress", detailedAddress);

            addressVoList.add(addressVo);
        }
        return ResponseUtil.ok(addressVoList);
    }

    /**
     * 收货地址详情
     *
     * @param userId 用户ID
     * @param id     收货地址ID
     * @return 收货地址详情
     */
    @GetMapping("detail")
    public Object detail(@LoginUser Integer userId, @NotNull Integer id) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        LitemallAddress address = addressService.findById(id);
        if (address == null) {
            return ResponseUtil.badArgumentValue();
        }

        Map<Object, Object> data = new HashMap<Object, Object>();
        data.put("id", address.getId());
        data.put("name", address.getName());
        data.put("provinceId", address.getProvinceId());
        data.put("cityId", address.getCityId());
        data.put("areaId", address.getAreaId());
        data.put("mobile", address.getMobile());
        data.put("address", address.getAddress());
        data.put("isDefault", address.getIsDefault());
        String pname = regionService.findById(address.getProvinceId()).getName();
        data.put("provinceName", pname);
        String cname = regionService.findById(address.getCityId()).getName();
        data.put("cityName", cname);
        String dname = regionService.findById(address.getAreaId()).getName();
        data.put("areaName", dname);
        return ResponseUtil.ok(data);
    }

    private Object validate(LitemallAddress address) {
        String name = address.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }

        // 测试收货手机号码是否正确
        String mobile = address.getMobile();
        if (StringUtils.isEmpty(mobile)) {
            return ResponseUtil.badArgument();
        }
        if (!RegexUtil.isMobileExact(mobile)) {
            return ResponseUtil.badArgument();
        }

        Integer pid = address.getProvinceId();
        if (pid == null) {
            return ResponseUtil.badArgument();
        }
        if (regionService.findById(pid) == null) {
            return ResponseUtil.badArgumentValue();
        }

        Integer cid = address.getCityId();
        if (cid == null) {
            return ResponseUtil.badArgument();
        }
        if (regionService.findById(cid) == null) {
            return ResponseUtil.badArgumentValue();
        }

        Integer aid = address.getAreaId();
        if (aid == null) {
            return ResponseUtil.badArgument();
        }
        if (regionService.findById(aid) == null) {
            return ResponseUtil.badArgumentValue();
        }

        String detailedAddress = address.getAddress();
        if (StringUtils.isEmpty(detailedAddress)) {
            return ResponseUtil.badArgument();
        }

        Boolean isDefault = address.getIsDefault();
        if (isDefault == null) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    /**
     * 添加或更新收货地址
     *
     * @param userId  用户ID
     * @param address 用户收货地址
     * @return 添加或更新操作结果
     */
    @PostMapping("save")
    public Object save(@LoginUser Integer userId, @RequestBody LitemallAddress address) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Object error = validate(address);
        if (error != null) {
            return error;
        }

        if (address.getIsDefault()) {
            // 重置其他收获地址的默认选项
            addressService.resetDefault(userId);
        }

        if (address.getId() == null || address.getId().equals(0)) {
            address.setId(null);
            address.setUserId(userId);
            addressService.add(address);
        } else {
            address.setUserId(userId);
            if (addressService.update(address) == 0) {
                return ResponseUtil.updatedDataFailed();
            }
        }
        return ResponseUtil.ok(address.getId());
    }

    /**
     * 删除收货地址
     *
     * @param userId  用户ID
     * @param address 用户收货地址，{ id: xxx }
     * @return 删除操作结果
     */
    @PostMapping("delete")
    public Object delete(@LoginUser Integer userId, @RequestBody LitemallAddress address) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Integer id = address.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        addressService.delete(id);
        return ResponseUtil.ok();
    }
}