<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">

  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Learning Log</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/entry-list.css" />
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
             TODO 4: Display Logged-In Username and Logout Link (Entry List)
             ============================================================
             Replace the static "Username" text and placeholder "#" logout
             link with dynamic session data — same pattern as the tutorial's
             topic pages.

             Currently the header shows:
               <div class="usersession">
                 <h3>Username</h3>
                 <a href="#" class="logout">Logout</a>
               </div>

             Change it to use the User object stored in the session:

               <div class="usersession">
                 <h3><c:out value="${sessionScope.user.username}" /></h3>
                 <a href="${pageContext.request.contextPath}/logout" class="logout"
                    onclick="return confirm('Are you sure you want to logout?');">Logout</a>
               </div>

             CONCEPT: ${sessionScope.user.username} reads the User object
             from the session (stored by LoginServlet during login) and
             calls getUsername() via EL.

             This is the SAME pattern used on topic-list.jsp and
             topic-add-edit.jsp (completed in the tutorial). Every page
             with a header should show the logged-in user — consistency
             across all pages.

             The confirm dialog prevents accidental logouts.

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
          <li><a href="${pageContext.request.contextPath}/topic">&lt; Back (Topics)</a></li>
          <li><p>Entries List (${entries.size()})</p></li>
          <li><a href="${pageContext.request.contextPath}/entry?action=new&topicid=${topic.id}">+New Entry</a></li>
        </ul>
      </nav>

      <main class="content">
        <div class="search-bar">
          <form action="${pageContext.request.contextPath}/entry" method="get">
            <input type="hidden" name="action" value="search" />
            <input type="hidden" name="topicid" value="${topic.id}" />
            <label for="search">Entry: </label>
            <input type="text" name="search" placeholder="Search..." value="<c:out value='${searchKeyword}' default='' />" />
            <button type="submit">SEARCH</button>
          </form>
        </div>

        <h2 class="topic-title"><c:out value="${topic.name}" /></h2>

        <div class="entry-grid">
          <c:forEach var="entry" items="${entries}">
            <div class="entry-card">
              <div class="entry-header">
                <div>
                  <h3><c:out value="${entry.title}" /></h3>
                  <p class="date">Date:
                    <fmt:formatDate value="${entry.createdAt}"
                      pattern="MMM d, yyyy" />
                  </p>
                </div>
                <div class="photo">Photo</div>
              </div>

              <p class="entry-text"><c:out value="${entry.text}" /></p>

              <c:if test="${not empty entry.link}">
                <p class="link">
                  Link:
                  <c:choose>
                    <c:when test="${fn:startsWith(entry.link, 'http://') || fn:startsWith(entry.link, 'https://')}">
                      <a href="${fn:escapeXml(entry.link)}" target="_blank"><c:out value="${entry.link}" /></a>
                    </c:when>
                    <c:otherwise>
                      <c:out value="${entry.link}" />
                    </c:otherwise>
                  </c:choose>
                </p>
              </c:if>

              <div class="entry-actions">
                <a href="${pageContext.request.contextPath}/entry?action=edit&entryid=${entry.id}&topicid=${topic.id}">
                  <button>Edit</button>
                </a>
                <form action="${pageContext.request.contextPath}/entry" method="post">
                  <input type="hidden" name="action" value="delete" />
                  <input type="hidden" name="entryid" value="${entry.id}" />
                  <input type="hidden" name="topicid" value="${topic.id}" />
                  <button class="danger" type="submit"
                    onclick="return confirm('Are you sure you want to delete?');">
                    Delete
                  </button>
                </form>
              </div>
            </div>
          </c:forEach>
        </div>
      </main>

      <footer class="footer">
        <h3>&copy; Learning Logs</h3>
      </footer>

    </div>
  </body>
</html>
