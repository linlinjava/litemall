package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/index")
public class IndexController {
    private final Log logger = LogFactory.getLog(IndexController.class);

    @RequestMapping("/index")
    public Object index(){
        return ResponseUtil.ok("hello world");
    }


}
