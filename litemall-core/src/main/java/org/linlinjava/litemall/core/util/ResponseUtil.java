package org.linlinjava.litemall.core.util;

import com.github.pagehelper.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 响应操作结果
 * <pre>
 *  {
 *      errno： 错误码，
 *      errmsg：错误消息，
 *      data：  响应数据
 *  }
 * </pre>
 *
 * <p>
 * 错误码：
 * <ul>
 * <li> 0，成功；
 * <li> 4xx，前端错误，说明前端开发者需要重新了解后端接口使用规范：
 * <ul>
 * <li> 401，参数错误，即前端没有传递后端需要的参数；
 * <li> 402，参数值错误，即前端传递的参数值不符合后端接收范围。
 * </ul>
 * <li> 5xx，后端错误，除501外，说明后端开发者应该继续优化代码，尽量避免返回后端错误码：
 * <ul>
 * <li> 501，验证失败，即后端要求用户登录；
 * <li> 502，系统内部错误，即没有合适命名的后端内部错误；
 * <li> 503，业务不支持，即后端虽然定义了接口，但是还没有实现功能；
 * <li> 504，更新数据失效，即后端采用了乐观锁更新，而并发更新时存在数据更新失效；
 * <li> 505，更新数据失败，即后端数据库更新失败（正常情况应该更新成功）。
 * </ul>
 * <li> 6xx，小商城后端业务错误码，
 * 具体见litemall-admin-api模块的AdminResponseCode。
 * <li> 7xx，管理后台后端业务错误码，
 * 具体见litemall-wx-api模块的WxResponseCode。
 * </ul>
 */
public class ResponseUtil {
    /**
     * 构建成功响应。
     *
     * @return 成功响应（不含数据）
     */
    public static Object ok() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", 0);
        obj.put("errmsg", "成功");
        return obj;
    }

    /**
     * 构建成功响应。
     *
     * @param data 响应数据
     * @return 成功响应（含数据）
     */
    public static Object ok(Object data) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", 0);
        obj.put("errmsg", "成功");
        obj.put("data", data);
        return obj;
    }

    /**
     * 构建分页列表成功响应。
     *
     * @param list 列表数据（Page 类型时自动附带分页信息）
     * @return 成功响应（含分页数据）
     */
    public static Object okList(List list) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("list", list);

        /* PageHelper 分页对象会附带分页信息，否则按单页处理 */
        if (list instanceof Page) {
            Page page = (Page) list;
            data.put("total", page.getTotal());
            data.put("page", page.getPageNum());
            data.put("limit", page.getPageSize());
            data.put("pages", page.getPages());
        } else {
            data.put("total", list.size());
            data.put("page", 1);
            data.put("limit", list.size());
            data.put("pages", 1);
        }

        return ok(data);
    }

    /**
     * 构建分页列表成功响应。
     *
     * @param list      列表数据
     * @param pagedList 用于分页信息的分页对象
     * @return 成功响应（含分页数据）
     */
    public static Object okList(List list, List pagedList) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("list", list);

        /* PageHelper 分页对象会附带分页信息，否则按单页处理 */
        if (pagedList instanceof Page) {
            Page page = (Page) pagedList;
            data.put("total", page.getTotal());
            data.put("page", page.getPageNum());
            data.put("limit", page.getPageSize());
            data.put("pages", page.getPages());
        } else {
            data.put("total", pagedList.size());
            data.put("page", 1);
            data.put("limit", pagedList.size());
            data.put("pages", 1);
        }

        return ok(data);
    }

    /**
     * 构建失败响应。
     *
     * @return 失败响应（错误码 -1）
     */
    public static Object fail() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", -1);
        obj.put("errmsg", "错误");
        return obj;
    }

    /**
     * 构建失败响应。
     *
     * @param errno  错误码
     * @param errmsg 错误消息
     * @return 失败响应
     */
    public static Object fail(int errno, String errmsg) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", errno);
        obj.put("errmsg", errmsg);
        return obj;
    }

    /**
     * 构建带数据的失败响应。
     *
     * @param errno  错误码
     * @param errmsg 错误消息
     * @param data   响应数据
     * @return 失败响应（含数据）
     */
    public static Object fail(int errno, String errmsg, String data) {
        Map<String, Object> obj = new HashMap<String, Object>(3);
        obj.put("errno", errno);
        obj.put("errmsg", errmsg);
        obj.put("data", data);
        return obj;
    }

    /**
     * 构建参数错误响应（401）。
     *
     * @return 参数错误响应
     */
    public static Object badArgument() {
        return fail(401, "参数不对");
    }

    /**
     * 构建参数值错误响应（402）。
     *
     * @return 参数值错误响应
     */
    public static Object badArgumentValue() {
        return fail(402, "参数值不对");
    }

    /**
     * 构建未登录响应（501）。
     *
     * @return 未登录响应
     */
    public static Object unlogin() {
        return fail(501, "请登录");
    }

    /**
     * 构建系统内部错误响应（502）。
     *
     * @return 系统内部错误响应
     */
    public static Object serious() {
        return fail(502, "系统内部错误");
    }

    /**
     * 构建业务不支持响应（503）。
     *
     * @return 业务不支持响应
     */
    public static Object unsupport() {
        return fail(503, "业务不支持");
    }

    /**
     * 构建更新数据失效响应（504）。
     *
     * @return 更新数据失效响应
     */
    public static Object updatedDateExpired() {
        return fail(504, "更新数据已经失效");
    }

    /**
     * 构建更新数据失败响应（505）。
     *
     * @return 更新数据失败响应
     */
    public static Object updatedDataFailed() {
        return fail(505, "更新数据失败");
    }

    /**
     * 构建无操作权限响应（506）。
     *
     * @return 无操作权限响应
     */
    public static Object unauthz() {
        return fail(506, "无操作权限");
    }
}
