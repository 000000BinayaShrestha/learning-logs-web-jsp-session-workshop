<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">

  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Learning Log — Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/auth.css" />
  </head>

  <body>
    <div class="auth-page">

      <div class="auth-header">
        <img src="${pageContext.request.contextPath}/static/images/book.png" alt="LL" />
        <h1>Learning Logs</h1>
      </div>

      <div class="auth-form">
        <form action="${pageContext.request.contextPath}/login" method="post">
          <h2>Login</h2>

          <c:if test="${not empty error}">
            <p class="error"><c:out value="${error}" /></p>
          </c:if>

          <%-- ============================================================
               TODO 8: Pre-Fill Username from Cookie
               ============================================================
               The login form currently uses default='' — so the username
               field is always empty on a fresh visit.

               But in TODO 6, you set a "username" cookie when the user
               logs in. That cookie survives for 1 day — even after the
               session expires.

               Change the default value to read that cookie:

          <input type="text" name="username" placeholder="Username"
                 value="<c:out value='${param.username}' default='${cookie.username.value}' />" required />

               HOW IT WORKS:
                 ${param.username}         — form parameter (set on failed login)
                 ${cookie.username.value}  — reads the "username" cookie via EL

               The <c:out> tag tries param.username first. If it's empty,
               the default attribute kicks in and reads the cookie instead.
               If there's no cookie either, the field is simply empty.

               This is the "remember username" feature:
                 1. User logs in → session + cookie created (TODO 6)
                 2. Session expires (30 min inactivity)
                 3. User returns to /login → session gone, but cookie
                    still has their username → field is pre-filled

               The complete code:

          <input type="text" name="username" placeholder="Username"
                 value="<c:out value='${param.username}' default='${cookie.username.value}' />" required />
               ============================================================ --%>
          <input type="text" name="username" placeholder="Username"
                 value="<c:out value='${param.username}' default='${cookie.username.value}' />" required />
          <input type="password" name="password" placeholder="Password" required />

          <button type="submit">Login</button>

          <p class="link">Don't have an account?
            <a href="${pageContext.request.contextPath}/register">Register</a>
          </p>
        </form>
      </div>

    </div>
  </body>
</html>
