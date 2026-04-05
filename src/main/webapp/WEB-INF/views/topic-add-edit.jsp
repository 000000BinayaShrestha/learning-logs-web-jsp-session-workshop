<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">

  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Learning Log</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/topic-add.css" />
  </head>

  <body>
    <div class="page">

      <header class="header">
        <div class="logo">
          <a href="${pageContext.request.contextPath}/topic" style="text-decoration: none">
            <img src="${pageContext.request.contextPath}/static/images/book.png" alt="LL" />
          </a>
          <h3>Learning Log</h3>
        </div>
        <div class="usersession">
          <h3><c:out value="${sessionScope.user.username}" /></h3>
          <a href="${pageContext.request.contextPath}/logout" class="logout"
             onclick="return confirm('Are you sure you want to logout?');">Logout</a>
        </div>
      </header>

      <nav class="navbar">
        <ul>
          <li><a href="${pageContext.request.contextPath}/topic">&lt; Back (Topic List)</a></li>
          <li><p>${empty topic ? 'Add Topic' : 'Edit Topic'}</p></li>
          <li></li>
        </ul>
      </nav>

      <main class="content">
        <div class="topicContainer">
          <c:choose>
            <c:when test="${not empty error}">
              <p style="color: red; text-align: center; padding: 5px;">
                <c:out value="${error}" />
              </p>
            </c:when>
            <c:when test="${not empty success}">
              <p style="color: green; text-align: center; padding: 5px;">
                <c:out value="${success}" />
              </p>
            </c:when>
          </c:choose>
          <form action="${pageContext.request.contextPath}/topic" method="post">
            <input type="hidden" name="action" value="${empty topic ? 'add' : 'edit'}" />
            <c:if test="${not empty topic}">
              <input type="hidden" name="topicid" value="${topic.id}" />
            </c:if>
            <div class="topicItem">
              <label for="topic">Topic: </label>
              <input type="text" placeholder="Topic Name" name="topic"
                     id="topic" value="<c:out value='${topic.name}' default='' />" />
            </div>
            <div class="submit">
              <button type="submit">Save</button>
            </div>
          </form>
        </div>
      </main>

      <footer class="footer">
        <h3>&copy; Learning Logs</h3>
      </footer>

    </div>
  </body>
</html>
