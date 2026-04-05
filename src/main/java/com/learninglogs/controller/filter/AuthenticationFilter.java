package com.learninglogs.controller.filter;

import com.learninglogs.utils.SessionUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * AuthenticationFilter — protects all routes behind login.
 *
 * Acts as middleware (gatekeeper) that intercepts every request and checks
 * if the user is logged in before allowing access.
 *
 * Rules:
 *   - Static resources (.css, .png, .js, .jpg) -> always allowed
 *   - /login and /register -> allowed only if NOT logged in
 *   - Everything else -> allowed only if logged in
 *
 * Complete from Week 7 tutorial.
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        // Allow static resources (CSS, images, JS) through without login
        if (path.startsWith("/static/")) {
            chain.doFilter(request, response);
            return;
        }

        boolean isLoggedIn = SessionUtil.getAttribute(req, "user") != null;
        boolean isAuthPage = "/login".equals(path) || "/register".equals(path);

        if (!isLoggedIn && !isAuthPage) {
            // Not logged in + trying to access protected page -> go to login
            res.sendRedirect(contextPath + "/login");
            return;
        }

        if (isLoggedIn && isAuthPage) {
            // Already logged in + trying to access login/register -> go to topics
            res.sendRedirect(contextPath + "/topic");
            return;
        }

        // All other cases: allow through
        chain.doFilter(request, response);
    }
}
