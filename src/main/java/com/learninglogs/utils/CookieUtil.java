package com.learninglogs.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CookieUtil — utility class for cookie management.
 *
 * Wraps the Cookie API into clean, reusable static methods.
 * Similar pattern to SessionUtil — keeps servlet code simple.
 *
 * Used by LoginServlet (set cookie) and LogoutServlet (delete cookie).
 *
 * New for Week 7 workshop.
 */

// ============================================================
// TODO 1: Cookie Utility Class
// ============================================================
// Create three static methods that wrap the Cookie API:
//
// 1. addCookie(response, name, value, maxAge)
//    - Create a new Cookie with the given name and value
//    - Set the path to "/" (available to entire app)
//    - Set HttpOnly to true (prevents JavaScript access — XSS protection)
//    - Set the maxAge (lifetime in seconds)
//    - Add the cookie to the response
//
// 2. getCookieValue(request, name)
//    - Get all cookies from the request: request.getCookies()
//    - IMPORTANT: getCookies() returns NULL (not empty array) when
//      there are no cookies — you MUST null-check before iterating!
//    - Loop through cookies, find the one matching the name
//    - Return its value, or null if not found
//
// 3. deleteCookie(response, name)
//    - Create a new Cookie with the given name and EMPTY value
//    - Set maxAge to 0 (tells browser to delete it immediately)
//    - Set path to "/" (MUST match the original path!)
//    - Add the cookie to the response
//
// CONCEPTS:
// - Cookies are stored in the BROWSER (client-side), unlike sessions
//   which are stored on the SERVER.
// - setPath("/") means the cookie is sent with every request to the app.
//   If you set a different path, the cookie only applies to that path.
// - setHttpOnly(true) prevents JavaScript from reading the cookie via
//   document.cookie — protects against XSS attacks stealing cookie values.
// - maxAge > 0: cookie expires after that many seconds
// - maxAge = 0: delete the cookie immediately
// - maxAge = -1: session cookie (deleted when browser closes)
//
// WHY A UTILITY CLASS?
// Without CookieUtil, servlets would need this verbose code:
//   Cookie cookie = new Cookie("username", user.getUsername());
//   cookie.setMaxAge(86400);
//   cookie.setPath("/");
//   cookie.setHttpOnly(true);
//   response.addCookie(cookie);
//
// With CookieUtil:
//   CookieUtil.addCookie(response, "username", user.getUsername(), 86400);
//
// See 04-cookies.md for the full CookieUtil pattern.
//
// The complete code:
//
//   public class CookieUtil {
//
//       public static void addCookie(HttpServletResponse response,
//                                    String name, String value, int maxAge) {
//           Cookie cookie = new Cookie(name, value);
//           cookie.setMaxAge(maxAge);
//           cookie.setPath("/");
//           cookie.setHttpOnly(true);
//           response.addCookie(cookie);
//       }
//
//       public static String getCookieValue(HttpServletRequest request,
//                                           String name) {
//           Cookie[] cookies = request.getCookies();
//           if (cookies != null) {
//               for (Cookie cookie : cookies) {
//                   if (name.equals(cookie.getName())) {
//                       return cookie.getValue();
//                   }
//               }
//           }
//           return null;
//       }
//
//       public static void deleteCookie(HttpServletResponse response,
//                                       String name) {
//           Cookie cookie = new Cookie(name, "");
//           cookie.setMaxAge(0);
//           cookie.setPath("/");
//           response.addCookie(cookie);
//       }
//   }
//
// ============================================================
public class CookieUtil {

}
