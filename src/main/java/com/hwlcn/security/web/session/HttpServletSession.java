package com.hwlcn.security.web.session;


import com.hwlcn.security.session.InvalidSessionException;
import com.hwlcn.security.session.Session;
import com.hwlcn.security.util.StringUtils;
import com.hwlcn.security.web.servlet.SecurityHttpSession;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;


public class HttpServletSession implements Session {

    private static final String HOST_SESSION_KEY = HttpServletSession.class.getName() + ".HOST_SESSION_KEY";
    private static final String TOUCH_OBJECT_SESSION_KEY = HttpServletSession.class.getName() + ".TOUCH_OBJECT_SESSION_KEY";

    private HttpSession httpSession = null;

    public HttpServletSession(HttpSession httpSession, String host) {
        if (httpSession == null) {
            String msg = "HttpSession constructor argument cannot be null.";
            throw new IllegalArgumentException(msg);
        }
        if (httpSession instanceof SecurityHttpSession) {
            String msg = "HttpSession constructor argument cannot be an instance of SecurityHttpSession.  This " +
                    "is enforced to prevent circular dependencies and infinite loops.";
            throw new IllegalArgumentException(msg);
        }
        this.httpSession = httpSession;
        if (StringUtils.hasText(host)) {
            setHost(host);
        }
    }

    public Serializable getId() {
        return httpSession.getId();
    }

    public Date getStartTimestamp() {
        return new Date(httpSession.getCreationTime());
    }

    public Date getLastAccessTime() {
        return new Date(httpSession.getLastAccessedTime());
    }

    public long getTimeout() throws InvalidSessionException {
        try {
            return httpSession.getMaxInactiveInterval() * 1000;
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }

    public void setTimeout(long maxIdleTimeInMillis) throws InvalidSessionException {
        try {
            int timeout = Long.valueOf(maxIdleTimeInMillis / 1000).intValue();
            httpSession.setMaxInactiveInterval(timeout);
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }

    protected void setHost(String host) {
        setAttribute(HOST_SESSION_KEY, host);
    }

    public String getHost() {
        return (String) getAttribute(HOST_SESSION_KEY);
    }

    public void touch() throws InvalidSessionException {
        try {
            httpSession.setAttribute(TOUCH_OBJECT_SESSION_KEY, TOUCH_OBJECT_SESSION_KEY);
            httpSession.removeAttribute(TOUCH_OBJECT_SESSION_KEY);
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }

    public void stop() throws InvalidSessionException {
        try {
            httpSession.invalidate();
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }

    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        try {
            Enumeration namesEnum = httpSession.getAttributeNames();
            Collection<Object> keys = null;
            if (namesEnum != null) {
                keys = new ArrayList<Object>();
                while (namesEnum.hasMoreElements()) {
                    keys.add(namesEnum.nextElement());
                }
            }
            return keys;
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }

    private static String assertString(Object key) {
        if (!(key instanceof String)) {
            String msg = "HttpSession based implementations of the Shiro Session interface requires attribute keys " +
                    "to be String objects.  The HttpSession class does not support anything other than String keys.";
            throw new IllegalArgumentException(msg);
        }
        return (String) key;
    }

    public Object getAttribute(Object key) throws InvalidSessionException {
        try {
            return httpSession.getAttribute(assertString(key));
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }

    public void setAttribute(Object key, Object value) throws InvalidSessionException {
        try {
            httpSession.setAttribute(assertString(key), value);
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }

    public Object removeAttribute(Object key) throws InvalidSessionException {
        try {
            String sKey = assertString(key);
            Object removed = httpSession.getAttribute(sKey);
            httpSession.removeAttribute(sKey);
            return removed;
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }
}
