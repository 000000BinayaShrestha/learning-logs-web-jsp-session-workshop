# Session Utility Pattern

## The Problem: Repetitive Session Code

Without a utility class, every servlet that works with sessions repeats the same
boilerplate code:

```java
// Reading from session — repeated in every servlet
HttpSession session = request.getSession(false);
if (session != null) {
    User user = (User) session.getAttribute("user");
    if (user != null) {
        // finally do something with user
    }
}

// Writing to session — repeated in login servlet
HttpSession session = request.getSession();
session.setMaxInactiveInterval(30 * 60);
session.setAttribute("user", user);

// Destroying session — repeated in logout servlet
HttpSession session = request.getSession(false);
if (session != null) {
    session.invalidate();
}
```

This is error-prone. Forget `getSession(false)` and you accidentally create a
new session. Forget the null check and you get a `NullPointerException`.

## The Solution: SessionUtil

Wrap all session operations into clean static methods:

```java
public class SessionUtil {

    // CREATE/UPDATE — stores a value in the session
    public static void setAttribute(HttpServletRequest request,
                                    String key, Object value) {
        HttpSession session = request.getSession();  // creates if needed
        session.setMaxInactiveInterval(30 * 60);     // 30 min timeout
        session.setAttribute(key, value);
    }

    // READ — retrieves a value from the session
    public static Object getAttribute(HttpServletRequest request,
                                      String key) {
        HttpSession session = request.getSession(false);  // never creates
        if (session != null) {
            return session.getAttribute(key);
        }
        return null;
    }

    // DELETE — destroys the entire session
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);  // never creates
        if (session != null) {
            session.invalidate();
        }
    }
}
```

## Usage Across the Application

### LoginServlet — Store user in session

```java
User user = userDao.findUserByEmail(email);
if (user != null && BCrypt.checkpw(password, user.getPassword())) {
    SessionUtil.setAttribute(request, "user", user);  // one line!
    response.sendRedirect(request.getContextPath() + "/topic");
}
```

### TopicServlet — Read user from session

```java
User user = (User) SessionUtil.getAttribute(request, "user");
int userId = user.getId();
ArrayList<Topic> topics = topicDao.fetchAllTopicsByUserId(userId);
```

### LogoutServlet — Destroy session

```java
SessionUtil.invalidateSession(request);  // one line!
response.sendRedirect(request.getContextPath() + "/login");
```

### AuthenticationFilter — Check if logged in

```java
boolean isLoggedIn = SessionUtil.getAttribute(request, "user") != null;
```

## getSession() vs getSession(false)

This is the most important distinction in session management:

| Method               | Creates New Session? | When to Use                     |
|----------------------|----------------------|---------------------------------|
| `getSession()`       | Yes, if none exists  | Login — you WANT a new session  |
| `getSession(false)`  | Never                | Read/check/logout — don't create|

**Why does this matter?**

If `getAttribute()` used `getSession()` (without `false`), every time you check
"is the user logged in?" you'd create a new empty session. This wastes server
memory and could interfere with filter logic.

**Rule:** Only `setAttribute()` should use `getSession()` (to create a session
for login). Everything else uses `getSession(false)`.

## Session Timeout

```java
session.setMaxInactiveInterval(30 * 60);  // 1800 seconds = 30 minutes
```

- The timeout is set **per session**, not globally
- It resets with every request (it's an **inactivity** timer)
- After 30 minutes of no requests, the server destroys the session automatically
- The user will be redirected to login on their next request (by the filter)

You could also set this globally in `web.xml`:

```xml
<session-config>
    <session-timeout>30</session-timeout>  <!-- minutes -->
</session-config>
```

The programmatic approach (`setMaxInactiveInterval`) takes precedence over `web.xml`.

## Why Static Methods?

SessionUtil uses static methods because:

1. **No state** — it doesn't hold any data itself, it just wraps HttpSession calls
2. **Convenience** — no need to create a SessionUtil object: `SessionUtil.setAttribute(...)`
3. **Consistency** — same pattern as `DatabaseConnection.getConnection()`

## Key Points

- **Centralize** session operations to avoid scattered, error-prone code
- Use `getSession()` only when creating a session (login)
- Use `getSession(false)` everywhere else to avoid accidental session creation
- The null check in `getAttribute`/`invalidateSession` prevents `NullPointerException`
- Session timeout is an **inactivity** timer — each request resets the clock
- Static utility pattern = no object needed, just call `SessionUtil.method(...)`
