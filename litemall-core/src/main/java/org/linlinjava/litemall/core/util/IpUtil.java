package org.linlinjava.litemall.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP地址相关工具类
 */
public class IpUtil {

    private static final Log logger = LogFactory.getLog(IpUtil.class);

    /**
     * 获取客户端真实 IP 地址。
     * <p>
     * 依次从请求头 x-forwarded-for、Proxy-Client-IP、WL-Proxy-Client-IP 中获取，
     * 若都为空则回退到远程地址；经过多级代理时取第一个 IP。
     *
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    /* 根据网卡取本机配置的IP */
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        logger.error(e.getMessage(), e);
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            /* 通过多个代理时，第一个 IP 为客户端真实 IP，多个 IP 以逗号分隔； */
            /* 单个 IP 最长 15 个字符（如 255.255.255.255），超过 15 说明包含多个 IP */
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }

        return ipAddress;
    }
}
