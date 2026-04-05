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

          <input type="text" name="username" placeholder="Username"
                 value="<c:out value='${param.username}' default='' />" required />
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
