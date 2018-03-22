package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.domain.LitemallRegion;
import org.linlinjava.litemall.db.service.LitemallRegionService;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wx/region")
public class WxRegionController {
    private final Log logger = LogFactory.getLog(WxRegionController.class);

    @Autowired
    private LitemallRegionService regionService;

    @RequestMapping("list")
    public Object list(Integer pid) {
        if(pid == null){
            return ResponseUtil.fail402();
        }

        List<LitemallRegion> regionList = regionService.queryByPid(pid);
        return ResponseUtil.ok(regionList);
    }


}