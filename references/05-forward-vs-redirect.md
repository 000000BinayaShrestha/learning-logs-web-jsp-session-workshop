# Forward vs Redirect in Jakarta EE

## Two Ways to Send Users to Another Page

When a servlet finishes processing, it needs to show a result. There are two
fundamentally different ways to do this:

| Feature              | Forward                              | Redirect                              |
|----------------------|--------------------------------------|---------------------------------------|
| Method               | `request.getRequestDispatcher().forward()` | `response.sendRedirect()`       |
| HTTP requests        | 1 (same request)                     | 2 (new request from browser)          |
| URL in browser       | Stays the same                       | Changes to new URL                    |
| Request attributes   | Preserved (same request)             | Lost (new request)                    |
| Speed                | Faster (server-side)                 | Slightly slower (extra round-trip)    |
| Scope                | Same application only                | Any URL (even external)               |

## Forward — Server-Side Transfer

```java
request.setAttribute("error", "Invalid email");
request.getRequestDispatcher("/WEB-INF/views/login.jsp")
       .forward(request, response);
```

**What happens internally:**

```
1. Browser → Server:  POST /login
2. Server internally passes request to login.jsp
3. Server → Browser:  HTML response (login page with error)

Browser URL still shows: /login
```

The browser has **no idea** a forward happened. It made one request and got one
response. The URL doesn't change.

**Use forward when:**
- Showing a JSP view (the standard MVC pattern)
- You need to pass request attributes (error messages, form data)
- The user should stay on the same URL

## Redirect — Client-Side Transfer

```java
response.sendRedirect(request.getContextPath() + "/topic");
```

**What happens internally:**

```
1. Browser → Server:  POST /login
2. Server → Browser:  302 Redirect to /topic
3. Browser → Server:  GET /topic  (NEW request)
4. Server → Browser:  HTML response (topic list page)

Browser URL changes to: /topic
```

The browser makes **two** requests. The URL changes to the new location.

**Use redirect when:**
- After a successful form submission (POST → Redirect → GET pattern)
- Sending user to a different servlet/URL
- You don't need to preserve request attributes
- Preventing form resubmission on browser refresh

## The POST-Redirect-GET Pattern

This is a fundamental web pattern. Without it:

```
1. User submits login form (POST /login)
2. Server processes and forwards to topic-list.jsp
3. User refreshes the page
4. Browser re-submits the POST! ("Confirm form resubmission" dialog)
```

With POST-Redirect-GET:

```
1. User submits login form (POST /login)
2. Server redirects to GET /topic
3. User refreshes the page
4. Browser simply re-GETs /topic — no resubmission!
```

## How This Applies to Our App

### LoginServlet

```java
// doGet — show the login form
protected void doGet(HttpServletRequest request,
                     HttpServletResponse response) {
    // FORWARD to JSP (show the form)
    request.getRequestDispatcher("/WEB-INF/views/login.jsp")
           .forward(request, response);
}

// doPost — process login
protected void doPost(HttpServletRequest request,
                      HttpServletResponse response) {
    User user = userDao.findUserByEmail(email);

    if (user != null && BCrypt.checkpw(password, user.getPassword())) {
        // Success → REDIRECT (POST-Redirect-GET)
        SessionUtil.setAttribute(request, "user", user);
        response.sendRedirect(request.getContextPath() + "/topic");
    } else {
        // Failure → FORWARD (keep form data, show error)
        request.setAttribute("error", "Invalid credentials");
        request.getRequestDispatcher("/WEB-INF/views/login.jsp")
               .forward(request, response);
    }
}
```

**Why forward on failure?** So the error message (request attribute) is preserved
and displayed on the login page.

**Why redirect on success?** So refreshing the topic list page doesn't re-POST
the login form.

### AuthenticationFilter

```java
// Redirect — not forward — because we're changing the URL
res.sendRedirect(contextPath + "/login");   // not logged in
res.sendRedirect(contextPath + "/topic");   // already logged in
```

The filter uses redirect because:
1. It's sending the user to a **different** URL
2. It doesn't need to pass request attributes
3. The URL should change to reflect where the user actually is

## Request Attributes and Forward

Forward preserves request attributes because it's the **same** request:

```java
// In servlet
request.setAttribute("error", "Topic name required");
request.getRequestDispatcher("/WEB-INF/views/topic-add-edit.jsp")
       .forward(request, response);

// In JSP — works because same request
<c:if test="${not empty error}">
    <p>${error}</p>
</c:if>
```

With redirect, the attribute would be lost (it's a new request). To pass data
across a redirect, you'd need to use session attributes or URL parameters.

## Common Mistakes

### 1. Forgetting `return` After Redirect

```java
// WRONG — code continues executing after redirect!
response.sendRedirect(contextPath + "/login");
// This code still runs:
request.setAttribute("data", someData);
request.getRequestDispatcher("/view.jsp").forward(request, response);
// IllegalStateException: response already committed

// CORRECT
response.sendRedirect(contextPath + "/login");
return;  // Stop execution!
```

### 2. Forward After Redirect (or vice versa)

You can only call forward OR redirect once per request. Calling both causes
`IllegalStateException: Cannot forward after response has been committed`.

### 3. Using Forward in Filter for Different URLs

```java
// WRONG in a filter — can cause redirect loops
request.getRequestDispatcher("/login").forward(request, response);
// The forwarded request hits the filter AGAIN!

// CORRECT — use redirect
response.sendRedirect(contextPath + "/login");
```

## Summary Decision Table

| Scenario                          | Use          | Why                                |
|-----------------------------------|--------------|------------------------------------|
| Show a JSP view                   | Forward      | Same request, pass attributes      |
| After successful form POST        | Redirect     | Prevent resubmission on refresh    |
| Show error on same form           | Forward      | Preserve error message attribute   |
| Filter sends user to login        | Redirect     | Different URL, no attributes needed|
| After logout                      | Redirect     | Change URL to /login               |
| Link to another page              | Redirect     | User navigates to new URL          |

## Key Points

- **Forward** = server-side, same request, URL doesn't change, attributes preserved
- **Redirect** = client-side, new request, URL changes, attributes lost
- Always use **POST-Redirect-GET** after successful form submissions
- Always `return` after `sendRedirect()` to prevent further execution
- Filters should use **redirect**, not forward, to avoid redirect loops
- doGet → forward to JSP (show view), doPost success → redirect (prevent resubmission)
