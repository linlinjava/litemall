package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.db.domain.LitemallKeyword;
import org.linlinjava.litemall.db.domain.LitemallSearchHistory;
import org.linlinjava.litemall.db.service.LitemallKeywordService;
import org.linlinjava.litemall.db.service.LitemallSearchHistoryService;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/search")
public class WxSearchController {
    @Autowired
    private LitemallKeywordService keywordsService;
    @Autowired
    private LitemallSearchHistoryService searchHistoryService;

    /**
     * 　　index
     * 用户登录是可选的，如果用户登录，则记录用户的搜索数据
     */
    @RequestMapping("index")
    public Object index(@LoginUser Integer userId) {
        //取出输入框默认的关键词
        LitemallKeyword defaultKeyword = keywordsService.queryDefault();
        //取出热闹关键词
        List<LitemallKeyword> hotKeywordList = keywordsService.queryHots();

        List<LitemallSearchHistory> historyList = null;
        if(userId != null) {
            //取出用户历史关键字
            historyList = searchHistoryService.queryByUid(userId);
        }

        Map<String, Object> data = new HashMap();
        data.put("defaultKeyword", defaultKeyword);
        data.put("historyKeywordList", historyList);
        data.put("hotKeywordList", hotKeywordList);
        return ResponseUtil.ok(data);
    }

    /**
     * 　　helper
     */
    @RequestMapping("helper")
    public Object helper(String keyword) {
        if(keyword == null){
            return ResponseUtil.fail402();
        }

        Integer page = 1;
        Integer size = 10;
        List<LitemallKeyword> keywordsList = keywordsService.queryByKeyword(keyword, page, size);
        String[] keys = new String[keywordsList.size()];
        int index = 0;
        for (LitemallKeyword key : keywordsList) {
           keys[index++] = key.getKeyword();
        }
        return ResponseUtil.ok(keys);
    }

    /**
     * 　　清楚用户搜索历史
     */
    @RequestMapping("clearhistory")
    public Object clearhistory(@LoginUser Integer userId) {
        if(userId == null){
            return ResponseUtil.fail401();
        }

        searchHistoryService.deleteByUid(userId);
        return ResponseUtil.ok();
    }
}
