package com.hwlcn.security.web.mgt;

import com.hwlcn.security.codec.Base64;
import com.hwlcn.security.mgt.AbstractRememberMeManager;
import com.hwlcn.security.subject.Subject;
import com.hwlcn.security.subject.SubjectContext;
import com.hwlcn.security.web.servlet.Cookie;
import com.hwlcn.security.web.servlet.SecurityHttpServletRequest;
import com.hwlcn.security.web.servlet.SimpleCookie;
import com.hwlcn.security.web.subject.WebSubjectContext;
import com.hwlcn.security.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieRememberMeManager extends AbstractRememberMeManager {

    private static transient final Logger log = LoggerFactory.getLogger(CookieRememberMeManager.class);

    public static final String DEFAULT_REMEMBER_ME_COOKIE_NAME = "rememberMe";

    private Cookie cookie;

    public CookieRememberMeManager() {
        Cookie cookie = new SimpleCookie(DEFAULT_REMEMBER_ME_COOKIE_NAME);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Cookie.ONE_YEAR);
        this.cookie = cookie;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
    }


    protected void rememberSerializedIdentity(Subject subject, byte[] serialized) {

        if (!WebUtils.isHttp(subject)) {
            if (log.isDebugEnabled()) {
                String msg = "Subject argument is not an HTTP-aware instance.  This is required to obtain a servlet " +
                        "request and response in order to set the rememberMe cookie. Returning immediately and " +
                        "ignoring rememberMe operation.";
                log.debug(msg);
            }
            return;
        }
        HttpServletRequest request = WebUtils.getHttpRequest(subject);
        HttpServletResponse response = WebUtils.getHttpResponse(subject);
        String base64 = Base64.encodeToString(serialized);
        Cookie template = getCookie();

        Cookie cookie = new SimpleCookie(template);
        cookie.setValue(base64);
        cookie.saveTo(request, response);
    }

    private boolean isIdentityRemoved(WebSubjectContext subjectContext) {
        ServletRequest request = subjectContext.resolveServletRequest();
        if (request != null) {
            Boolean removed = (Boolean) request.getAttribute(SecurityHttpServletRequest.IDENTITY_REMOVED_KEY);
            return removed != null && removed;
        }
        return false;
    }


    protected byte[] getRememberedSerializedIdentity(SubjectContext subjectContext) {

        if (!WebUtils.isHttp(subjectContext)) {
            if (log.isDebugEnabled()) {
                String msg = "SubjectContext argument is not an HTTP-aware instance.  This is required to obtain a " +
                        "servlet request and response in order to retrieve the rememberMe cookie. Returning " +
                        "immediately and ignoring rememberMe operation.";
                log.debug(msg);
            }
            return null;
        }

        WebSubjectContext wsc = (WebSubjectContext) subjectContext;
        if (isIdentityRemoved(wsc)) {
            return null;
        }

        HttpServletRequest request = WebUtils.getHttpRequest(wsc);
        HttpServletResponse response = WebUtils.getHttpResponse(wsc);

        String base64 = getCookie().readValue(request, response);

        if (Cookie.DELETED_COOKIE_VALUE.equals(base64)) return null;

        if (base64 != null) {
            base64 = ensurePadding(base64);
            if (log.isTraceEnabled()) {
                log.trace("Acquired Base64 encoded identity [" + base64 + "]");
            }
            byte[] decoded = Base64.decode(base64);
            if (log.isTraceEnabled()) {
                log.trace("Base64 decoded byte array length: " + (decoded != null ? decoded.length : 0) + " bytes.");
            }
            return decoded;
        } else {
            return null;
        }
    }

    private String ensurePadding(String base64) {
        int length = base64.length();
        if (length % 4 != 0) {
            StringBuilder sb = new StringBuilder(base64);
            for (int i = 0; i < length % 4; ++i) {
                sb.append('=');
            }
            base64 = sb.toString();
        }
        return base64;
    }

    protected void forgetIdentity(Subject subject) {
        if (WebUtils.isHttp(subject)) {
            HttpServletRequest request = WebUtils.getHttpRequest(subject);
            HttpServletResponse response = WebUtils.getHttpResponse(subject);
            forgetIdentity(request, response);
        }
    }

    public void forgetIdentity(SubjectContext subjectContext) {
        if (WebUtils.isHttp(subjectContext)) {
            HttpServletRequest request = WebUtils.getHttpRequest(subjectContext);
            HttpServletResponse response = WebUtils.getHttpResponse(subjectContext);
            forgetIdentity(request, response);
        }
    }

    private void forgetIdentity(HttpServletRequest request, HttpServletResponse response) {
        getCookie().removeFrom(request, response);
    }
}

