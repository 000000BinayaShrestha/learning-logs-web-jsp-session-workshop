# Servlet Filters — Middleware in Jakarta EE

## What is a Filter?

A **servlet filter** is a Java class that intercepts HTTP requests and responses
**before** they reach a servlet (or **after** the servlet has processed them).

Think of it as a **gatekeeper** or **middleware** — the request must pass through
the filter before reaching its destination.

```
Client Request → Filter → Servlet → Filter → Client Response
```

## Filter Interface

Filters implement `jakarta.servlet.Filter` and override `doFilter()`:

```java
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class MyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        // Pre-processing (before servlet)
        System.out.println("Request intercepted!");

        // Pass the request to the next filter or servlet
        chain.doFilter(request, response);

        // Post-processing (after servlet, before response sent)
        System.out.println("Response leaving!");
    }
}
```

### The Critical Line: `chain.doFilter(request, response)`

- **If you call it** → the request continues to the servlet (or next filter)
- **If you DON'T call it** → the request is **blocked**. The servlet never runs.

This is how authentication filters work — they either allow the request through
or redirect the user to a login page.

## @WebFilter Annotation

Register a filter using `@WebFilter` with a URL pattern:

```java
@WebFilter("/*")           // Intercepts ALL requests
@WebFilter("/topic/*")     // Only requests starting with /topic
@WebFilter("/api/*")       // Only API requests
```

`"/*"` is the most common pattern for authentication — you want to protect
**every** route and explicitly allow the exceptions (login page, static files).

## Filter vs Servlet

| Feature   | Servlet                        | Filter                              |
|-----------|--------------------------------|-------------------------------------|
| Purpose   | Handle a specific URL          | Intercept requests across URLs      |
| Mapping   | `@WebServlet("/login")`        | `@WebFilter("/*")`                  |
| Execution | Runs for its mapped URL only   | Runs for ALL matching URLs          |
| Response  | Generates the response         | Can modify or block the request     |
| Chaining  | N/A                            | Multiple filters can chain together |

## Authentication Filter Pattern

The standard pattern for protecting routes:

```java
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 1. Extract the path
        String path = req.getRequestURI()
                         .substring(req.getContextPath().length());

        // 2. Allow static resources through
        if (path.startsWith("/static/")) {
            chain.doFilter(request, response);
            return;
        }

        // 3. Check login status
        boolean isLoggedIn = SessionUtil.getAttribute(req, "user") != null;
        boolean isAuthPage = "/login".equals(path) || "/register".equals(path);

        // 4. Not logged in → redirect to login
        if (!isLoggedIn && !isAuthPage) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // 5. Already logged in → redirect away from auth pages
        if (isLoggedIn && isAuthPage) {
            res.sendRedirect(req.getContextPath() + "/topic");
            return;
        }

        // 6. All other cases → allow through
        chain.doFilter(request, response);
    }
}
```

## Why Allow Static Resources?

The filter intercepts **ALL** requests — including CSS, images, and JavaScript.
Without the static resource check:

```
User visits /login
  → Filter blocks /login (wait, login should be allowed)
  → Even if allowed, /static/css/main.css is ALSO blocked
  → Login page loads with NO styling!
```

Always check for static resource paths first and let them through unconditionally.

## Casting: ServletRequest → HttpServletRequest

The `doFilter` method receives generic `ServletRequest`/`ServletResponse` objects.
To access HTTP-specific methods (like `getRequestURI()`, `getSession()`,
`sendRedirect()`), you must cast to the HTTP versions:

```java
HttpServletRequest req = (HttpServletRequest) request;
HttpServletResponse res = (HttpServletResponse) response;
```

This cast is safe in a web application — all requests are HTTP requests.

## Filter Lifecycle

1. **init()** — Called once when the filter is loaded (optional to override)
2. **doFilter()** — Called for every matching request
3. **destroy()** — Called once when the server shuts down (optional to override)

## Common Use Cases for Filters

| Use Case             | Description                                    |
|----------------------|------------------------------------------------|
| Authentication       | Check if user is logged in                     |
| Authorization        | Check if user has permission for the resource  |
| Logging              | Log all incoming requests                      |
| CORS Headers         | Add Cross-Origin headers to responses          |
| Character Encoding   | Set request/response encoding to UTF-8         |
| Compression          | Compress response data                         |

## Key Points

- Filters are **middleware** — they sit between the client and the servlet
- `chain.doFilter()` passes the request forward; omitting it **blocks** the request
- `@WebFilter("/*")` intercepts everything — be explicit about what you allow through
- Always allow static resources (CSS, JS, images) through the filter
- Cast `ServletRequest` to `HttpServletRequest` for HTTP-specific methods
- Filters are the standard Jakarta EE way to implement cross-cutting concerns
