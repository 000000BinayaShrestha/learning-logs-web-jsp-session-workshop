package com.learninglogs.controller;

import com.learninglogs.dao.UserDao;
import com.learninglogs.dao.UserDaoImpl;
import com.learninglogs.entity.User;
import com.learninglogs.utils.PasswordUtil;
import com.learninglogs.utils.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * LoginServlet — handles user login.
 *
 * URL: /login
 *
 * GET  /login -> forward to login.jsp (displays the form)
 * POST /login -> find user, verify password, store session, set cookie, redirect on success
 *
 * Complete from Week 7 tutorial (session management).
 * Week 7 workshop adds cookie functionality for "remember username".
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userDao.findByUsername(username);

        if (user == null) {
            request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp")
                   .forward(request, response);
            return;
        }

        if (!PasswordUtil.checkPassword(password, user.getPassword())) {
            request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp")
                   .forward(request, response);
            return;
        }

        SessionUtil.setAttribute(request, "user", user);

        // ============================================================
        // TODO 6: Set Username Cookie After Login
        // ============================================================
        // After storing the user in the session, set a custom cookie
        // that remembers the username.
        //
        // Add this ONE line after SessionUtil.setAttribute:
        //
        //   CookieUtil.addCookie(response, "username", user.getUsername(), 24 * 60 * 60);
        //
        // CONCEPT: This cookie is SEPARATE from the JSESSIONID session
        // cookie. Here's the difference:
        //
        //   JSESSIONID (automatic):
        //     - Created by the server when a session is created
        //     - Contains a random ID that maps to server-side data
        //     - Session cookie — deleted when browser closes (usually)
        //     - You never create or manage this cookie yourself
        //
        //   "username" cookie (yours):
        //     - Created by YOUR code using CookieUtil.addCookie
        //     - Contains the actual username string (e.g., "testuser")
        //     - Persistent cookie — 24 * 60 * 60 = 86400 seconds = 1 day
        //     - Survives browser close (unlike session cookies)
        //
        // WHY SET A COOKIE?
        // The cookie could be used to pre-fill the username field on the
        // login form even after the session expires. For example:
        //   - User logs in → session created + username cookie set
        //   - User is inactive for 30 min → session expires
        //   - User returns → session gone, but cookie still has username
        //   - Login form could read the cookie to pre-fill the field
        //
        // In DevTools: Application -> Cookies -> localhost:9090
        // You'll see both JSESSIONID and your "username" cookie.
        //
        // You will also need to add this import:
        //   import com.learninglogs.utils.CookieUtil;
        //
        // The complete code:
        //
        //   CookieUtil.addCookie(response, "username", user.getUsername(), 24 * 60 * 60);
        //
        // ============================================================

        response.sendRedirect(request.getContextPath() + "/topic");
    }
}
