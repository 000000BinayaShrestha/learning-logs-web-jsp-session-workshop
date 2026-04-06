<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">

  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Learning Log</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/entry-add.css" />
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
        <%-- ============================================================
             TODO 5: Display Logged-In Username and Logout Link (Entry Form)
             ============================================================
             Same change as TODO 4 (entry-list.jsp) — replace the static
             "Username" and "#" link with dynamic session data.

             Currently:
               <div class="usersession">
                 <h3>Username</h3>
                 <a href="#" class="logout">Logout</a>
               </div>

             Change to:
        <div class="usersession">
          <h3><c:out value="${sessionScope.user.username}" /></h3>
          <a href="${pageContext.request.contextPath}/logout" class="logout"
             onclick="return confirm('Are you sure you want to logout?');">Logout</a>
        </div>

             CONCEPT: Same pattern as TODO 4 and the tutorial's topic pages.
             Every page that shows the header needs this change for
             consistency — the username and logout link should work on
             ALL pages, not just some.

             The complete code:

        <div class="usersession">
          <h3><c:out value="${sessionScope.user.username}" /></h3>
          <a href="${pageContext.request.contextPath}/logout" class="logout"
             onclick="return confirm('Are you sure you want to logout?');">Logout</a>
        </div>
             ============================================================ --%>
        <div class="usersession">
          <h3>Username</h3>
          <a href="#" class="logout">Logout</a>
        </div>
      </header>

      <nav class="navbar">
        <ul>
          <li><a href="${pageContext.request.contextPath}/entry?topicid=${param.topicid}">&lt; Back (Entries)</a></li>
          <li><p>${empty entry ? 'Add Entry' : 'Edit Entry'}</p></li>
          <li></li>
        </ul>
      </nav>

      <main class="content">
        <h2 class="topic-title"><c:out value="${topic.name}" /></h2>

        <div class="form-card">
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
          <form action="${pageContext.request.contextPath}/entry" method="post">
            <input type="hidden" name="action" value="${empty entry ? 'add' : 'edit'}" />
            <input type="hidden" name="topicid" value="${param.topicid}" />
            <c:if test="${not empty entry}">
              <input type="hidden" name="entryid" value="${entry.id}" />
            </c:if>

            <div class="form-row">
              <label for="title">Title:</label>
              <input type="text" id="title" name="title" placeholder="Title"
                     value="<c:out value='${entry.title}' default='' />" />
            </div>

            <div class="form-row">
              <label for="text">Description:</label>
              <textarea id="text" name="text" rows="4"
                placeholder="Description"><c:out value="${entry.text}" default="" /></textarea>
            </div>

            <div class="form-row">
              <label for="link">Link:</label>
              <input type="text" id="link" name="link" placeholder="Link URL"
                     value="<c:out value='${entry.link}' default='' />" />
            </div>

            <div class="form-row">
              <label for="image">Image:</label>
              <input type="text" id="image" name="image" placeholder="Image URL"
                     value="<c:out value='${entry.image}' default='' />" />
            </div>

            <div class="form-actions">
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
