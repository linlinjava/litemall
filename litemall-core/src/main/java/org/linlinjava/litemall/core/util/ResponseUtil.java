package org.linlinjava.litemall.core.util;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
    public static Object ok() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", 0);
        obj.put("errmsg", "成功");
        return obj;
    }

    public static Object ok(Object data) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", 0);
        obj.put("errmsg", "成功");
        obj.put("data", data);
        return obj;
    }

    public static Object ok(String errmsg, Object data) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", 0);
        obj.put("errmsg", errmsg);
        obj.put("data", data);
        return obj;
    }

    public static Object fail() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", -1);
        obj.put("errmsg", "错误");
        return obj;
    }

    public static Object fail(int errno, String errmsg) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", errno);
        obj.put("errmsg", errmsg);
        return obj;
    }

    public static Object fail401() {
        return fail(401, "请登录");
    }

    public static Object unlogin(){
        return fail401();
    }

    public static Object fail402() {
        return fail(402, "参数不对");
    }

    public static Object badArgument(){
        return fail402();
    }

    public static Object fail403() {
        return fail(403, "参数值不对");
    }

    public static Object badArgumentValue(){
        return fail403();
    }

    public static Object fail501() {
        return fail(501, "业务不支持");
    }

    public static Object unsupport(){
        return fail501();
    }

    public static Object fail502() {
        return fail(502, "系统内部错误");
    }

    public static Object serious(){
        return fail502();
    }


}

