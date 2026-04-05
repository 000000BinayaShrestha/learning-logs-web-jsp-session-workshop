package com.learninglogs.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * SessionUtil — utility class for session management.
 *
 * Wraps HttpSession operations into clean, reusable static methods.
 * Used by LoginServlet (create session), LogoutServlet (destroy session),
 * TopicServlet (read session), and AuthenticationFilter (check session).
 *
 * Complete from Week 7 tutorial.
 */
public class SessionUtil {

    public static void setAttribute(HttpServletRequest request,
                                    String key, Object value) {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(30 * 60);
        session.setAttribute(key, value);
    }

    public static Object getAttribute(HttpServletRequest request,
                                      String key) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return session.getAttribute(key);
        }
        return null;
    }

    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
