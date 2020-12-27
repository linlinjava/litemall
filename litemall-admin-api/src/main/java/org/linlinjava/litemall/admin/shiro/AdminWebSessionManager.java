package org.linlinjava.litemall.admin.shiro;

import com.alibaba.druid.util.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

public class AdminWebSessionManager extends DefaultWebSessionManager {

    public static final String LOGIN_TOKEN_KEY = "X-Litemall-Admin-Token";
    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";


    public AdminWebSessionManager() {
        super();
        setGlobalSessionTimeout(MILLIS_PER_HOUR * 6);
//        setSessionIdCookieEnabled(false);
        setSessionIdUrlRewritingEnabled(false);
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String id = WebUtils.toHttp(request).getHeader(LOGIN_TOKEN_KEY);
        if (!StringUtils.isEmpty(id)) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return id;
        } else {
            return super.getSessionId(request, response);
        }
    }
}
