# HTTP Sessions

## Why Sessions Exist

HTTP is **stateless** — each request is independent. The server doesn't remember who sent the previous request. Sessions solve this by creating a server-side storage area linked to a specific browser via a cookie.

## How Sessions Work

```
1. Browser sends POST /login (username + password)
2. Server authenticates → creates a Session object in memory
3. Server sends response with Set-Cookie: JSESSIONID=abc123
4. Browser stores the JSESSIONID cookie
5. Every subsequent request: Browser sends Cookie: JSESSIONID=abc123
6. Server looks up session abc123 → finds the stored User object
```

## HttpSession API

### Creating / Getting a Session

```java
// Gets existing session OR creates a new one
HttpSession session = request.getSession();

// Gets existing session OR returns null (never creates)
HttpSession session = request.getSession(false);
```

**When to use which:**
- `getSession()` — when you want to CREATE a session (login)
- `getSession(false)` — when you want to READ/CHECK a session (filter, read user)

### Storing Data

```java
HttpSession session = request.getSession();
session.setAttribute("user", userObject);    // Store any Java object
session.setAttribute("role", "admin");       // Store a String
```

### Reading Data

```java
HttpSession session = request.getSession(false);
if (session != null) {
    User user = (User) session.getAttribute("user");  // Cast from Object
    String role = (String) session.getAttribute("role");
}
```

### Setting Timeout

```java
// Session expires after 30 minutes of inactivity
session.setMaxInactiveInterval(30 * 60);  // seconds
```

### Destroying a Session

```java
HttpSession session = request.getSession(false);
if (session != null) {
    session.invalidate();  // Removes all attributes, destroys session
}
```

## Session Scope in JSP (EL)

```jsp
<%-- Access session attributes in JSP using sessionScope --%>
Welcome, ${sessionScope.user.username}!

<%-- EL automatically calls user.getUsername() --%>
<%-- "sessionScope" tells EL to look in the session, not request scope --%>

<%-- You can also check if a session attribute exists --%>
<c:if test="${not empty sessionScope.user}">
    Logged in as ${sessionScope.user.username}
</c:if>
```

## JSESSIONID Cookie

The `JSESSIONID` is a **session cookie** (deleted when browser closes) that contains only the session ID. The actual data stays on the server.

You never create `JSESSIONID` yourself — the server generates it automatically when you call `request.getSession()`.

### When Does JSESSIONID Get Created?

Earlier than you might expect. JSPs call `request.getSession()` by default when they render (unless `<%@ page session="false" %>` is set). So when a user visits `/login` for the first time, the login page renders and a session is created — the `JSESSIONID` cookie appears in the browser **before the user even types a username**.

At that point the session is **empty** — no user data is stored in it. After a successful login, `SessionUtil.setAttribute(request, "user", user)` stores the User object **inside** that same session. The JSESSIONID doesn't change — what changes is the data on the server.

```
GET  /login → JSP renders → session created (empty) → JSESSIONID cookie sent
POST /login → password verified → User object stored in existing session
GET  /topic → same JSESSIONID sent → server finds session → retrieves User
```

This is why `getSession(false)` is important — in the AuthenticationFilter, you don't want to create a new empty session just to check if someone is logged in. `getSession(false)` returns `null` if no session exists, which tells the filter the user is not authenticated.

### Why JSESSIONID Survives Closing the Browser

JSESSIONID is a **session cookie** (`maxAge = -1`), which means the browser should delete it when it closes. However, modern browsers with "session restore" features (like Chrome's **"Continue where you left off"** setting) preserve session cookies across restarts — the browser treats it as if it never closed.

This doesn't cause a security issue because the **cookie** and the **session** are independent:
- The cookie is just the key (stored in the browser)
- The session data is on the server (controlled by `maxInactiveInterval`)

Even if the cookie survives a browser restart, the server-side session expires after 30 minutes of inactivity. When the browser sends the old JSESSIONID, the server won't find a matching session — the AuthenticationFilter sees no valid session and redirects to `/login`.

To manually clear session cookies: **Ctrl+Shift+Delete** → Clear cookies, or change Chrome's startup setting to "Open the New Tab page".

## Session Lifecycle

```
Created     → request.getSession() called for first time
Active      → Browser sends JSESSIONID, server finds session
Timeout     → No requests for maxInactiveInterval seconds → auto-destroyed
Invalidated → session.invalidate() called → immediately destroyed
```
