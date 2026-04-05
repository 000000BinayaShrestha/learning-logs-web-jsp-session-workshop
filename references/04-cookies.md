# Cookies in Jakarta EE

> **Workshop Preview:** This reference is included for the Workshop, where you'll build a CookieUtil and use cookies for "remember username" functionality. The tutorial focuses on sessions (not cookies) — but this guide is here so you can read ahead.

## What is a Cookie?

A **cookie** is a small piece of data that the server sends to the browser.
The browser stores it and sends it back with every subsequent request to the
same server.

```
1. Browser → Server:  POST /login (username, password)
2. Server → Browser:  Set-Cookie: JSESSIONID=abc123
                      Set-Cookie: lastUsername=testuser; Max-Age=604800
3. Browser → Server:  GET /topic  (Cookie: JSESSIONID=abc123; lastUsername=testuser)
4. Browser → Server:  GET /entry  (Cookie: JSESSIONID=abc123; lastUsername=testuser)
   ... cookies sent automatically with every request ...
```

## Cookies vs Sessions

| Feature        | Cookie                         | Session                        |
|----------------|--------------------------------|--------------------------------|
| Stored where?  | Browser (client-side)          | Server (server-side)           |
| Size limit     | ~4 KB per cookie               | No practical limit             |
| Security       | Visible to user, can be edited | Hidden from user               |
| Lifetime       | Controlled by maxAge           | Controlled by timeout          |
| Use case       | Preferences, "remember me"     | Authentication, sensitive data |

**Important:** Sessions USE cookies internally. The `JSESSIONID` cookie is how
the server knows which session belongs to which browser.

## JSESSIONID — The Session Cookie

When you call `request.getSession()`, the server:

1. Creates a session object on the server
2. Generates a unique ID (e.g., `A1B2C3D4E5F6`)
3. Sends a `Set-Cookie: JSESSIONID=A1B2C3D4E5F6` header to the browser
4. On subsequent requests, the browser sends this cookie back
5. The server uses the ID to look up the correct session

You **never** manage JSESSIONID yourself — the server handles it automatically.

## JSESSIONID vs Custom Cookies — They Are Separate

This is a common point of confusion. JSESSIONID and your custom cookies are
**completely independent**. They just happen to both be cookies.

```
Browser's cookie storage for localhost:9090:

┌──────────────┬────────────────────┬───────────────┬─────────────┐
│ Name         │ Value              │ Expires       │ Created By  │
├──────────────┼────────────────────┼───────────────┼─────────────┤
│ JSESSIONID   │ A1B2C3D4E5F6      │ Session*      │ Tomcat      │
│ lastUsername  │ testuser           │ 7 days        │ YOUR code   │
│ theme         │ dark               │ 30 days       │ YOUR code   │
└──────────────┴────────────────────┴───────────────┴─────────────┘

* "Session" = deleted when browser closes
```

Every request sends **ALL** cookies for that domain:

```
GET /topic HTTP/1.1
Cookie: JSESSIONID=A1B2C3D4E5F6; lastUsername=testuser; theme=dark
```

The server reads them independently:

```java
// JSESSIONID — handled automatically by the server
// You never read this cookie directly. Instead:
User user = (User) SessionUtil.getAttribute(request, "user");
// Server uses JSESSIONID internally to find the session → gets User object

// Custom cookie — YOU read this directly
String lastUsername = CookieUtil.getCookieValue(request, "lastUsername");
// Reads the cookie value "testuser" directly from the request
```

**Key difference:**
- JSESSIONID is just a **key** — the data (User object) lives on the server
- Custom cookies **ARE** the data — stored in the browser, visible to the user

That's why you'd never put a password in a cookie, but a username is fine.

**When JSESSIONID disappears** (logout/session expires), your custom cookies
**still exist** — they have their own independent lifetime. For example,
`lastUsername` survives logout because it has a 7-day maxAge, so the login
form can pre-fill the username field even after the session is gone.

## Creating Cookies in Java

```java
import jakarta.servlet.http.Cookie;

// Create a cookie
Cookie cookie = new Cookie("theme", "dark");
cookie.setMaxAge(7 * 24 * 60 * 60);  // 7 days in seconds
cookie.setPath("/");                   // available to entire app
cookie.setHttpOnly(true);              // JavaScript can't access it (XSS protection)
// cookie.setSecure(true);             // uncomment in production (HTTPS only)
response.addCookie(cookie);            // send to browser
```

### Cookie Properties

| Property   | Method               | Description                              |
|------------|----------------------|------------------------------------------|
| Name       | `new Cookie(name, value)` | Cookie identifier (cannot change)   |
| Value      | `setValue(String)`   | Cookie data                              |
| Max Age    | `setMaxAge(int)`     | Lifetime in seconds (-1 = session cookie)|
| Path       | `setPath(String)`    | URL path where cookie is sent            |
| HttpOnly   | `setHttpOnly(true)`  | Prevents JavaScript access (security)    |
| Secure     | `setSecure(true)`    | Only sent over HTTPS                     |

### Max Age Values

| Value | Meaning                                            |
|-------|----------------------------------------------------|
| > 0   | Cookie expires after this many seconds             |
| 0     | Delete the cookie immediately                      |
| -1    | Session cookie — deleted when browser closes       |

## Reading Cookies

```java
Cookie[] cookies = request.getCookies();
if (cookies != null) {
    for (Cookie cookie : cookies) {
        if ("theme".equals(cookie.getName())) {
            String theme = cookie.getValue();  // "dark"
        }
    }
}
```

**Note:** `getCookies()` returns `null` (not an empty array) if no cookies exist.
Always check for null first!

## Deleting Cookies

There's no "delete" method. Instead, send the same cookie with `maxAge = 0`:

```java
Cookie cookie = new Cookie("theme", "");
cookie.setMaxAge(0);      // tells browser to delete it
cookie.setPath("/");      // must match the original path!
response.addCookie(cookie);
```

**Important:** The `path` must match the original cookie's path, or the browser
won't know which cookie to delete.

## Cookie Utility Pattern

Just like SessionUtil, you can create a CookieUtil for cleaner code:

```java
public class CookieUtil {

    public static void addCookie(HttpServletResponse response,
                                 String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(true);   // prevents JavaScript access (XSS protection)
        // cookie.setSecure(true);  // uncomment in production (HTTPS only)
        response.addCookie(cookie);
    }

    public static String getCookieValue(HttpServletRequest request,
                                        String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void deleteCookie(HttpServletResponse response,
                                    String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
```

**Why `setHttpOnly(true)`?** Without it, JavaScript can read your cookies
via `document.cookie`. If an attacker injects a script (XSS), they could
steal cookie values. `HttpOnly` makes the cookie invisible to JavaScript —
only the server can read it.

**Why `setSecure(true)` is commented out?** In development we use `http://localhost`
(not HTTPS). A `Secure` cookie is only sent over HTTPS, so it would never
be sent in development. Enable it in production where HTTPS is used.

## Security Considerations

1. **Never store sensitive data in cookies** — they're visible to the user
   and can be modified. Store sensitive data in the session.

2. **Use HttpOnly** — `cookie.setHttpOnly(true)` prevents JavaScript from
   reading the cookie, protecting against XSS attacks.

3. **Use Secure in production** — `cookie.setSecure(true)` ensures the cookie
   is only sent over HTTPS, not plain HTTP.

4. **Don't trust cookie values** — always validate on the server side.
   A user can manually edit cookie values in their browser.

## Common Use Cases

| Use Case           | Cookie Name      | Example Value     |
|--------------------|------------------|-------------------|
| Session tracking   | JSESSIONID       | A1B2C3D4 (auto)   |
| Remember username  | lastUsername     | john@example.com   |
| Theme preference   | theme            | dark               |
| Language           | locale           | en-US              |
| Remember me        | rememberToken    | encrypted-token    |

## Key Points

- Cookies are **client-side** storage sent with every HTTP request
- Sessions use the **JSESSIONID** cookie internally (managed by the server)
- `setMaxAge(0)` deletes a cookie; `-1` makes it a session cookie
- Always null-check `getCookies()` before iterating
- **Never** store passwords or sensitive data in cookies
- Use `setHttpOnly(true)` and `setSecure(true)` for security
- The Cookie utility pattern keeps servlet code clean and consistent
