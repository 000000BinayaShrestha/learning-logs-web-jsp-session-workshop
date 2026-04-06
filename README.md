# Learning Logs — Week 7 Session Management Workshop

## Ownership Validation, Cookie Utilities, and Entry Page Headers

> **In the tutorial, you added session management to topic pages — login persists, each user sees only their own topics, and unauthenticated users are blocked.** But there's still a security gap: any logged-in user can view or modify ANY user's topics and entries by changing the `topicid` in the URL. This workshop fixes that vulnerability with ownership checks on both topic and entry pages, and introduces cookies.

---

## The Problem

The tutorial added session management and user-scoped topic lists, but left ownership validation and entry pages unprotected:

| What Works (from Tutorial) | What's Still Missing |
|---------------------------|---------------------|
| Login creates a session, logout destroys it | Entry pages still show static "Username" and broken logout link |
| Topic list is user-scoped (each user sees their own) | No ownership check — users can edit/delete another user's topics or access their entries by manipulating the URL |
| AuthenticationFilter blocks unauthenticated access | No cookie utility exists (04-cookies.md is reference only) |
| Topic page headers show logged-in username | LoginServlet doesn't set cookies — no "remember username" functionality |
| SessionUtil wraps session API | LogoutServlet doesn't clear cookies — cookies persist after logout |

**The security hole:** Log in as `testuser`, then manually change the URL to `?action=edit&topicid=6` — you can edit `demouser`'s topic. Or navigate to `/entry?topicid=6` — you'll see `demouser`'s entries. **After this workshop**, both types of URL manipulation are blocked.

---

## What's Already Done

Everything from the Week 7 tutorial is provided complete:

| Category | Files | Status |
|----------|-------|--------|
| Session management | `SessionUtil.java` (set, get, invalidate) | Provided |
| Auth filter | `AuthenticationFilter.java` (@WebFilter) | Provided |
| User-scoped topics | `TopicDao`/`Impl` (fetchAllTopicsByUserId, searchTopicsByUserId, checkUserForTopic signature) | Provided |
| Login session | `LoginServlet.java` (stores User in session) | Provided |
| Logout | `LogoutServlet.java` (invalidates session) | Provided |
| Topic pages | `TopicServlet` (session headers + **ownership checks on edit/delete**), `topic-list.jsp`, `topic-add-edit.jsp` | Provided |
| Error handling | `TopicServlet`, `EntryServlet` — **try-catch on all `parseInt` calls** (new in workshop) | Provided |
| Link security | `entry-list.jsp` — **safe link rendering** for `entry.link` href (new in workshop) | Provided |
| All other files | Entities, DAOs, CSS, SQL, references, error page | Provided |

---

## What You'll Build

7 TODOs across 7 files — adding ownership validation, cookies, and consistent headers:

| # | File | What You'll Build |
|---|------|-------------------|
| 1 | `CookieUtil.java` | **NEW** — Utility class wrapping Cookie API (add, get, delete) |
| 2 | `TopicDaoImpl.java` | Implement ownership check with SQL query |
| 3 | `EntryServlet.java` | Add ownership validation at top of doGet and doPost |
| 4 | `entry-list.jsp` | Show logged-in username + working logout link |
| 5 | `entry-add-edit.jsp` | Show logged-in username + working logout link |
| 6 | `LoginServlet.java` | Set "username" cookie after session creation |
| 7 | `LogoutServlet.java` | Delete "username" cookie on logout |

> **Note:** The `checkUserForTopic` method signature in `TopicDao.java` is provided — `TopicServlet` already uses it for topic edit/delete ownership checks. You implement the body in TODO 2.

---

## Architecture

### Ownership Check Flow

```
User clicks topic link → GET /entry?topicid=X

                    ┌─────────────────────┐
                    │ AuthenticationFilter │ ← Is user logged in?
                    └────────┬────────────┘
                             │ Yes
                    ┌────────┴────────────┐
                    │    EntryServlet      │
                    │                     │
                    │  1. Parse topicid   │
                    │  2. Get user from   │
                    │     session         │
                    │  3. checkUserFor    │ ← NEW (TODO 3)
                    │     Topic(userId,   │
                    │     topicId)        │
                    │                     │
                    │  Owns topic? ───────┼── No → redirect to /topic
                    │       │             │
                    │      Yes            │
                    │       │             │
                    │  4. Continue with   │
                    │     existing logic  │
                    └─────────────────────┘
```

### Cookie Flow

```
Login:
  POST /login → LoginServlet
    → SessionUtil.setAttribute(request, "user", user)  ← session (server-side)
    → CookieUtil.addCookie(response, "username", ...)  ← cookie (client-side)  NEW
    → Server sends two cookies to browser:
        1. JSESSIONID=abc123  (automatic, session cookie)
        2. username=testuser  (yours, 1-day persistent cookie)

Every request:
  Browser automatically sends ALL cookies:
    Cookie: JSESSIONID=abc123; username=testuser

Logout:
  GET /logout → LogoutServlet
    → SessionUtil.invalidateSession(request)  ← destroys session data
    → CookieUtil.deleteCookie(response, "username")  ← removes cookie  NEW
    → Redirect to /login (both session and cookie are gone)
```

---

## TODO Order Explained

The TODOs follow a bottom-up approach — utility first, then DAO, then servlets, then views:

```
TODO 1: CookieUtil ─────────── Utility (foundation — needed by TODOs 6-7)
TODO 2: TopicDaoImpl ───────── Implementation (ownership check SQL query)
TODO 3: EntryServlet ────────── Use ownership check (needs TODO 2)
TODO 4: entry-list.jsp ──────── Display session data in UI
TODO 5: entry-add-edit.jsp ──── Display session data in UI
TODO 6: LoginServlet ────────── Set cookie on login (needs TODO 1)
TODO 7: LogoutServlet ───────── Delete cookie on logout (needs TODO 1)
```

**Why this order?**
- TODO 1: CookieUtil is a utility — must exist before LoginServlet/LogoutServlet use it
- TODO 2: DAO implementation before Servlet (avoids runtime errors when EntryServlet calls the method)
- TODO 3: The security fix — blocks URL manipulation
- TODOs 4-5: Visual fix — consistent headers across all pages
- TODOs 6-7: Cookie integration — adds persistent "remember username" functionality

> **Important:** `TopicServlet` already calls `checkUserForTopic()` for topic edit/delete ownership checks (provided code). Until you complete TODO 2, topic edit and delete will be blocked for ALL users because the stub returns `false`. This is expected — complete TODO 2 first to fix it.

---

## Cookies vs Sessions Recap

| | Session (Tutorial) | Cookie (Workshop) |
|---|---|---|
| **Where stored** | Server (memory) | Browser (client-side) |
| **Created by** | `SessionUtil.setAttribute` | `CookieUtil.addCookie` |
| **Read by** | `SessionUtil.getAttribute` | `CookieUtil.getCookieValue` |
| **Destroyed by** | `SessionUtil.invalidateSession` | `CookieUtil.deleteCookie` |
| **Contains** | User object (anything) | Small text (username) |
| **Lifetime** | 30-minute inactivity timeout | maxAge in seconds (1 day) |
| **Security** | Hidden from user | Visible in DevTools, editable |
| **Use case** | Authentication, sensitive data | Preferences, "remember me" |

**Key point:** Sessions and cookies are independent. JSESSIONID is the bridge — it's a cookie that maps to server-side session data. Your "username" cookie is completely separate and has its own lifetime.

---

## CookieUtil Pattern

```java
// SET a cookie (LoginServlet)
CookieUtil.addCookie(response, "username", user.getUsername(), 24 * 60 * 60);

// GET a cookie value
String username = CookieUtil.getCookieValue(request, "username");

// DELETE a cookie (LogoutServlet)
CookieUtil.deleteCookie(response, "username");
```

The utility wraps three verbose Cookie API operations into clean one-liners — same pattern as SessionUtil from the tutorial.

---

## Ownership Validation

**Why it matters:** Without the ownership check, the AuthenticationFilter only verifies that a user is logged in — not that they own the resource they're accessing. This is the difference between **authentication** (who are you?) and **authorization** (what can you access?).

```
Authentication (filter):  "Is someone logged in?" → Yes/No
Authorization (servlet):  "Does this user own topic #6?" → Yes/No
```

The tutorial added authentication. This workshop adds authorization — for both topics (provided in TopicServlet) and entries (your TODO 3 in EntryServlet).

### Where Does the Ownership Check Go?

You'll notice `checkUserForTopic()` is placed differently in TopicServlet vs EntryServlet. This is intentional — and understanding why is key:

**EntryServlet — check at the TOP (before all actions):**
```
Every EntryServlet URL includes ?topicid=X:
  /entry?topicid=1               (list entries)
  /entry?topicid=1&action=new    (new entry form)
  /entry?topicid=1&action=edit   (edit entry form)
  /entry?topicid=1&action=search (search entries)

Since ALL actions need a topicid, one check at the top covers everything.
If you don't own the topic, you shouldn't be on any entry page at all.
```

**TopicServlet — check INSIDE specific branches only:**
```
Not every TopicServlet action receives a topicid:
  /topic                         (list)   → No topicid — uses fetchAllTopicsByUserId (already safe)
  /topic?action=new              (new)    → No topicid — creates for session user (already safe)
  /topic?action=search&search=X  (search) → No topicid — uses searchTopicsByUserId (already safe)
  /topic?action=edit&topicid=3   (edit)   → HAS topicid — needs ownership check ✓
  /topic?action=delete&topicid=3 (delete) → HAS topicid — needs ownership check ✓

A check at the top would crash on list/new/search because there's no topicid to parse.
So the check goes inside the edit and delete branches only.
```

> **Read the provided TopicServlet code** — it shows how the same `checkUserForTopic()` method you implement in TODO 2 is used in a different placement pattern. Both servlets achieve the same goal (block unauthorized access) but adapt the check placement to their URL structure.

---

## What's Improved from the Tutorial

Two security improvements were added to the provided code in this workshop that weren't in the tutorial:

### 1. Safe URL Parameter Parsing

The tutorial used `Integer.parseInt()` without error handling:
```java
// Tutorial (Week 7) — crashes with HTTP 500 if topicid is invalid
int topicId = Integer.parseInt(request.getParameter("topicid"));
```

This workshop wraps every `parseInt` call with try-catch:
```java
// Workshop — redirects gracefully instead of crashing
int topicId;
try {
    topicId = Integer.parseInt(request.getParameter("topicid"));
} catch (NumberFormatException e) {
    response.sendRedirect(request.getContextPath() + "/topic");
    return;
}
```

**Why?** A user could type `?topicid=abc` in the URL. Without the try-catch, the server returns an HTTP 500 error page. With it, the user is quietly redirected to a safe page.

### 2. Safe Link Rendering

The tutorial rendered entry links directly in the href attribute:
```jsp
<%-- Tutorial — vulnerable to javascript: URL injection --%>
<a href="${entry.link}"><c:out value="${entry.link}" /></a>
```

This workshop only renders clickable links for `http://` and `https://` URLs:
```jsp
<%-- Workshop — blocks javascript: and other dangerous URL schemes --%>
<c:choose>
  <c:when test="${fn:startsWith(entry.link, 'http://') || fn:startsWith(entry.link, 'https://')}">
    <a href="${fn:escapeXml(entry.link)}" target="_blank"><c:out value="${entry.link}" /></a>
  </c:when>
  <c:otherwise>
    <c:out value="${entry.link}" />
  </c:otherwise>
</c:choose>
```

**Why?** Without this check, a user could enter `javascript:alert('xss')` as a link value. The display text is safely escaped by `<c:out>`, but clicking the link would execute the script. The `fn:startsWith` check ensures only real URLs become clickable links.

> **Note:** This uses the JSTL functions taglib (`fn`), imported at the top of entry-list.jsp with `<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>`.

---

## Project Structure

```
learning-logs-web-jsp-session-workshop/
├── README.md
├── pom.xml
├── sql/
│   ├── learninglog.sql                     (schema — unchanged)
│   └── seed.sql                            (2 test users for isolation testing)
├── references/
│   ├── 01-http-sessions.md                 (from tutorial)
│   ├── 02-servlet-filters.md               (from tutorial)
│   ├── 03-session-utility-pattern.md       (from tutorial)
│   ├── 04-cookies.md                       (KEY reference for TODO 1)
│   └── 05-forward-vs-redirect.md           (from tutorial)
├── src/main/
│   ├── java/com/learninglogs/
│   │   ├── controller/
│   │   │   ├── TopicServlet.java           (provided — user-scoped + ownership checks)
│   │   │   ├── EntryServlet.java           ← TODO 3: ownership check
│   │   │   ├── LoginServlet.java           ← TODO 6: set cookie
│   │   │   ├── RegisterServlet.java        (provided — unchanged)
│   │   │   ├── LogoutServlet.java          ← TODO 7: delete cookie
│   │   │   └── filter/
│   │   │       └── AuthenticationFilter.java (provided — route protection)
│   │   ├── entity/
│   │   │   ├── Topic.java                  (provided)
│   │   │   ├── Entry.java                  (provided)
│   │   │   └── User.java                   (provided)
│   │   ├── dao/
│   │   │   ├── TopicDao.java               (provided — includes checkUserForTopic signature)
│   │   │   ├── TopicDaoImpl.java           ← TODO 2: implement ownership check
│   │   │   ├── EntryDao.java               (provided)
│   │   │   ├── EntryDaoImpl.java           (provided)
│   │   │   ├── UserDao.java                (provided)
│   │   │   └── UserDaoImpl.java            (provided)
│   │   └── utils/
│   │       ├── CookieUtil.java             ← TODO 1: NEW — cookie utility
│   │       ├── SessionUtil.java            (provided — from tutorial)
│   │       ├── DatabaseConnection.java     (provided)
│   │       ├── ValidationUtil.java         (provided)
│   │       └── PasswordUtil.java           (provided)
│   └── webapp/
│       ├── error404.jsp                    (provided)
│       ├── static/
│       │   ├── css/                        (all provided — no changes)
│       │   ├── images/book.png
│       │   └── js/.gitkeep
│       └── WEB-INF/
│           ├── views/
│           │   ├── topic-list.jsp          (provided — session header done)
│           │   ├── topic-add-edit.jsp      (provided — session header done)
│           │   ├── entry-list.jsp          ← TODO 4: session header
│           │   ├── entry-add-edit.jsp      ← TODO 5: session header
│           │   ├── login.jsp               (provided)
│           │   └── register.jsp            (provided)
│           └── web.xml                     (provided — unchanged)
```

---

## Test Users

Same test users from the tutorial — for testing ownership isolation:

| Username | Password | Topics | Use Case |
|----------|----------|--------|----------|
| `testuser` | `Test@123` | Python, Web Development, Data Science, Machine Learning, Cybersecurity | Main test user |
| `demouser` | `Test@123` | Java, Databases, Cloud Computing | Test ownership blocking |

---

## Getting Started

### 1. Set Up the Database
Open phpMyAdmin (`http://localhost/phpmyadmin`) and run:
1. `sql/learninglog.sql` — creates the schema
2. `sql/seed.sql` — adds sample data (2 users + topics + entries)

### 2. Build and Run
```bash
mvn clean package cargo:run
```

### 3. Access the App
Open `http://localhost:9090/learning-logs/topic`

**Before completing the TODOs:** Entry pages show static "Username" with a broken logout link, any user can edit/delete another user's topics or access their entries via URL manipulation, and no cookies are set on login.

**After completing all TODOs:** Entry pages show the real username with a working logout link, ownership is validated on every topic and entry request, and a "username" cookie is set on login and cleared on logout.

---

## Test Cases

After completing all 7 TODOs, rebuild (`mvn clean package cargo:run`) and verify:

| # | Test | Expected Result |
|---|------|-----------------|
| 1 | Log in as testuser, click a topic to see entries | Entry list loads, header shows "testuser" with working Logout link |
| 2 | Click "+New Entry", check header | "testuser" and Logout link visible on add/edit page |
| 3 | Log in as testuser, manually change URL to demouser's topicid | Redirected to /topic (ownership check blocks access) |
| 4 | Log in as demouser, try to access testuser's topic entries via URL | Redirected to /topic (ownership check blocks access) |
| 5 | Add a new entry to own topic | Entry created successfully |
| 6 | Edit and delete own entries | Works normally |
| 7 | Check DevTools → Application → Cookies after login | "username" cookie present with 1-day expiry alongside JSESSIONID |
| 8 | Log out and check DevTools → Cookies | "username" cookie deleted, JSESSIONID invalid |
| 9 | Log in, close browser, reopen | JSESSIONID may survive (browser restore), username cookie survives (1-day maxAge) |
| 10 | Logout from entry page | Redirected to /login, session destroyed |
| 11 | All topic page features still work | Unaffected (tutorial features intact) |
| 12 | Search entries within a topic | Works, scoped to that topic |

---

## Troubleshooting

| Problem | Cause | Fix |
|---------|-------|-----|
| Port 9090 in use | Another app using the port | `mvn clean package cargo:run -Dcargo.servlet.port=9191` |
| Database connection error | MySQL not running | Start XAMPP MySQL |
| `NullPointerException` in EntryServlet | Session user is null | Ensure you're logged in before testing entry pages |
| Compilation error in EntryServlet | Missing import | Add `import com.learninglogs.entity.User;` and `import com.learninglogs.utils.SessionUtil;` |
| Compilation error in LoginServlet/LogoutServlet | Missing import | Add `import com.learninglogs.utils.CookieUtil;` |
| Topic edit/delete blocked for all users | TODO 2 not done | TopicServlet uses `checkUserForTopic()` — complete TODO 2 so it returns the correct result |
| Ownership check always fails | Wrong parameter order | `checkUserForTopic(userId, topicId)` — not reversed |
| Cookie not appearing in DevTools | TODO 6 not done or wrong tab | Check Application → Cookies → localhost:9090 (not Network tab) |
| Cookie persists after logout | TODO 7 not done | Add `CookieUtil.deleteCookie(response, "username")` in LogoutServlet |
| Entry pages still show "Username" | TODOs 4-5 not done | Replace static header with `${sessionScope.user.username}` |

*Informatics College Pokhara — Java Programming By Sandesh Hamal*
