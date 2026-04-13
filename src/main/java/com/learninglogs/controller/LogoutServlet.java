package com.learninglogs.controller;

import com.learninglogs.utils.SessionUtil;
import com.learninglogs.utils.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * LogoutServlet — handles user logout.
 *
 * URL: /logout
 *
 * GET /logout -> invalidate session -> delete cookies -> redirect to /login
 *
 * Complete from Week 7 tutorial (session invalidation).
 * Week 7 workshop adds cookie deletion on logout.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        SessionUtil.invalidateSession(request);

        // ============================================================
        // TODO 7: Delete Cookies on Logout
        // ============================================================
        // After invalidating the session, delete the "username" cookie
        // that was set during login (TODO 6).
        //
        // Without this, the username cookie would persist for its full
        // 1-day lifetime even after the user explicitly logs out.
        //
        // Add this ONE line after SessionUtil.invalidateSession:
        //
        //   CookieUtil.deleteCookie(response, "username");
        //
        // CONCEPT: Deleting a cookie means sending the SAME cookie back
        // with maxAge=0. The browser sees maxAge=0 and removes it.
        //
        // CookieUtil.deleteCookie handles this — it creates a cookie
        // with the same name, empty value, maxAge=0, and same path("/").
        //
        // The path MUST match — if the original cookie was set with
        // setPath("/") but you try to delete with a different path,
        // the browser won't know which cookie to delete.
        //
        // FLOW:
        //   User clicks Logout -> GET /logout
        //     -> SessionUtil.invalidateSession (destroys session data)
        //     -> CookieUtil.deleteCookie (removes username cookie)
        //     -> redirect to /login (both session and cookies are gone)
        //
        // You will also need to add this import:
        //   import com.learninglogs.utils.CookieUtil;
        //
        // The complete code:
        //
        //   CookieUtil.deleteCookie(response, "username");
        //
        // ============================================================
        CookieUtil.deleteCookie(response, "username");
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
