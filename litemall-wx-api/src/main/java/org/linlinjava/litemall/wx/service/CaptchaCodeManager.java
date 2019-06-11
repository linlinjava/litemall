package org.linlinjava.litemall.wx.service;

import org.linlinjava.litemall.wx.dto.CaptchaItem;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存系统中的验证码
 */
public class CaptchaCodeManager {
    private static ConcurrentHashMap<String, CaptchaItem> captchaCodeCache = new ConcurrentHashMap<>();

    /**
     * 添加到缓存
     *
     * @param phoneNumber 电话号码
     * @param code        验证码
     */
    public static boolean addToCache(String phoneNumber, String code) {


        //已经发过验证码且验证码还未过期
        if (captchaCodeCache.get(phoneNumber) != null) {
            if (captchaCodeCache.get(phoneNumber).getExpireTime().isAfter(LocalDateTime.now())) {
                return false;
            } else {
                //存在但是已过期，删掉
                captchaCodeCache.remove(phoneNumber);
            }
        }

        CaptchaItem captchaItem = new CaptchaItem();
        captchaItem.setPhoneNumber(phoneNumber);
        captchaItem.setCode(code);
        // 有效期为1分钟
        captchaItem.setExpireTime(LocalDateTime.now().plusMinutes(1));

        captchaCodeCache.put(phoneNumber, captchaItem);

        return true;
    }

    /**
     * 获取缓存的验证码
     *
     * @param phoneNumber 关联的电话号码
     * @return 验证码
     */
    public static String getCachedCaptcha(String phoneNumber) {
        //没有这个电话记录
        if (captchaCodeCache.get(phoneNumber) == null)
            return null;

        //有电话记录但是已经过期
        if (captchaCodeCache.get(phoneNumber).getExpireTime().isBefore(LocalDateTime.now())) {
            return null;
        }

        return captchaCodeCache.get(phoneNumber).getCode();
    }
}
