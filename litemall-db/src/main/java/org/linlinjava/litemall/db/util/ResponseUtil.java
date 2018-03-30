package org.linlinjava.litemall.db.util;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
    public static final Object ok() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", 0);
        obj.put("errmsg", "成功");
        return obj;
    }

    public static final Object ok(Object data) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", 0);
        obj.put("errmsg", "成功");
        obj.put("data", data);
        return obj;
    }

    public static final Object ok(String errmsg, Object data) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", 0);
        obj.put("errmsg", errmsg);
        obj.put("data", data);
        return obj;
    }

    public static final Object fail() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", -1);
        obj.put("errmsg", "错误");
        return obj;
    }

    public static final Object fail(int errno, String errmsg) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", errno);
        obj.put("errmsg", errmsg);
        return obj;
    }

    public static final Object fail401() {
        return fail(401, "请登录");
    }

    public static final Object unlogin(){
        return fail401();
    }

    public static final Object fail402() {
        return fail(402, "参数不对");
    }

    public static final Object badArgument(){
        return fail402();
    }

    public static final Object fail403() {
        return fail(403, "参数值不对");
    }

    public static final Object badArgumentValue(){
        return fail403();
    }

    public static final Object fail501() {
        return fail(501, "业务不支持");
    }

    public static final Object unsupport(){
        return fail501();
    }

    public static final Object fail502() {
        return fail(502, "系统内部错误");
    }

    public static final Object serious(){
        return fail502();
    }


}

