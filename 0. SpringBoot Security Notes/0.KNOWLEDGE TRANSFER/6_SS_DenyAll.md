# Spring Security -- Denying Access to Endpoints with `denyAll()`

## Overview

In a real-world Spring Boot application, there may be situations where
an HTTP endpoint must be completely inaccessible.

Spring Security provides the `denyAll()` authorization rule for this
purpose.

Unlike `authenticated()`, which allows authenticated users to access an
endpoint, `denyAll()` rejects matching requests regardless of whether
the user is authenticated or what roles they have.

------------------------------------------------------------------------

## 1. What Is `denyAll()`?

`denyAll()` tells Spring Security that **no request matching the
configured authorization rule should be authorized**.

Example:

``` java
http
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/admin").denyAll()
        .anyRequest().authenticated()
    );
```

The important configuration is:

``` java
.requestMatchers("/admin").denyAll()
```

This means requests to `/admin` are denied for everyone.

Authentication does not make the endpoint accessible.

------------------------------------------------------------------------

## 2. Expected HTTP Response

If a user attempts to access:

``` text
/admin
```

Spring Security will normally reject the request with:

``` text
HTTP 403 Forbidden
```

A `403 Forbidden` response indicates that access to the requested
resource is not permitted.

Even if the user supplies valid authentication credentials, the
`denyAll()` authorization rule continues to deny access.

------------------------------------------------------------------------

## 3. `403 Forbidden` vs `401 Unauthorized`

These two HTTP responses represent different security situations.

  -----------------------------------------------------------------------
  Status                              Meaning
  ----------------------------------- -----------------------------------
  `401 Unauthorized`                  Authentication is missing, invalid,
                                      or otherwise required.

  `403 Forbidden`                     The request is understood, but
                                      access to the resource is denied.
  -----------------------------------------------------------------------

With:

``` java
.requestMatchers("/admin").denyAll()
```

successfully authenticating does **not** make `/admin` accessible
because the authorization policy explicitly denies it.

------------------------------------------------------------------------

## 4. Request Rejection Before Controller Processing

Suppose there is an endpoint:

``` text
/admin
```

and the security configuration contains:

``` java
.requestMatchers("/admin").denyAll()
```

Spring Security can reject the request during security processing before
the application proceeds to normal controller handling.

Therefore, the endpoint is protected by the security configuration
rather than relying on controller code to reject access.

------------------------------------------------------------------------

## 5. Common Use Cases

### Maintenance Mode

Suppose an administrative section is temporarily under maintenance.

``` java
.requestMatchers("/admin/**").denyAll()
```

Requests such as:

``` text
/admin
/admin/dashboard
/admin/settings
/admin/users
```

can be temporarily blocked until maintenance is complete.

### Deprecated Endpoints

An old API may still exist in the codebase but should no longer be
accessible.

For example:

``` java
.requestMatchers("/api/v1/deprecated/**").denyAll()
```

This prevents clients from continuing to use endpoints that should no
longer be exposed.

### Temporarily Unsafe Endpoints

An endpoint may require additional security hardening before being
released.

Until the required controls are implemented, it can be blocked:

``` java
.requestMatchers("/internal/unsafe/**").denyAll()
```

This can provide a fail-closed approach while development or security
remediation is underway.

### Sensitive Functionality

Some functionality may exist in the application but should not currently
be exposed through HTTP.

For example:

``` java
.requestMatchers("/sensitive/**").denyAll()
```

### Feature or Configuration-Based Restrictions

Real-world security configurations can also be built conditionally based
on application configuration.

For example, an application might expose an administrative feature only
when a configuration property enables it.

The exact implementation depends on the application's Spring Security
version and configuration design. The important principle is that
`denyAll()` can be used as the authorization rule when the feature must
remain inaccessible.

------------------------------------------------------------------------

## 6. Denying an Entire URL Pattern

`requestMatchers()` can match more than one individual endpoint.

For example:

``` java
.requestMatchers("/admin/**").denyAll()
```

The `/**` pattern represents resources underneath the specified path.

This can deny requests such as:

``` text
/admin/dashboard
/admin/users
/admin/configuration
/admin/reports
```

This is useful when an entire application module or API group must be
disabled.

------------------------------------------------------------------------

## 7. Combining Public, Denied, and Authenticated Endpoints

A security configuration can contain different authorization policies.

Example:

``` java
http
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/public/**").permitAll()
        .requestMatchers("/admin/**").denyAll()
        .anyRequest().authenticated()
    );
```

Conceptually, this produces three categories:

  URL Pattern       Rule
  ----------------- ---------------------------
  `/public/**`      Public access
  `/admin/**`       Access denied to everyone
  Everything else   Authentication required

This gives the application a clear default security policy.

------------------------------------------------------------------------

## 8. Understanding the Three Main Rules

### `permitAll()`

``` java
.requestMatchers("/public/**").permitAll()
```

Anyone can access matching endpoints.

### `authenticated()`

``` java
.anyRequest().authenticated()
```

The user must be authenticated.

### `denyAll()`

``` java
.requestMatchers("/admin/**").denyAll()
```

Nobody is authorized to access matching endpoints.

A simple way to remember them is:

``` text
permitAll()      → Everyone
authenticated()  → Authenticated users
denyAll()        → Nobody
```

------------------------------------------------------------------------

## 9. Rule Ordering Matters

Authorization rules should be organized from specific rules to broader
fallback rules.

For example:

``` java
http
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/public/**").permitAll()
        .requestMatchers("/admin/**").denyAll()
        .anyRequest().authenticated()
    );
```

The final rule:

``` java
.anyRequest().authenticated()
```

acts as the fallback for requests that were not handled by an earlier
matcher.

Keeping specific rules before general rules makes the intended security
policy easier to understand and maintain.

------------------------------------------------------------------------

## 10. Testing with Postman

A simple test can be performed using Postman.

### Test 1 -- No Authentication

Request:

``` text
GET /admin
```

Expected result:

``` text
403 Forbidden
```

### Test 2 -- Valid Authentication

Send the same request with valid authentication credentials.

``` text
GET /admin
```

Expected result remains:

``` text
403 Forbidden
```

This demonstrates the key property of `denyAll()`:

> Authentication does not override an explicit deny rule.

------------------------------------------------------------------------

## 11. Browser Behavior

When accessing a denied endpoint from a browser, a Spring Boot
application may display an error page depending on its error-handling
configuration.

In API applications, it is generally better to return a structured JSON
error response instead of relying on a browser-oriented error page.

For example:

``` json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied"
}
```

The exact response format should be controlled by the application's
security exception-handling strategy.

------------------------------------------------------------------------

## 12. Production Security Recommendations

`denyAll()` is useful, but it should be part of a broader security
strategy.

Recommended practices include:

-   Use `denyAll()` when an endpoint must explicitly remain
    inaccessible.
-   Prefer a secure default for endpoints not explicitly configured.
-   Group related blocked endpoints under clear URL patterns when
    appropriate.
-   Document why an endpoint or module has been denied.
-   Add automated security tests confirming that denied endpoints return
    the expected response.
-   Do not assume authentication or administrator roles can override
    `denyAll()`.
-   Use proper `403` handling for REST APIs.
-   Avoid exposing sensitive details in error responses.
-   Review temporary deny rules after maintenance or security
    remediation is completed.

------------------------------------------------------------------------

## 13. Example Complete Configuration

A simplified configuration could look like this:

``` java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll()
            .requestMatchers("/admin/**").denyAll()
            .anyRequest().authenticated()
        );

    return http.build();
}
```

This configuration establishes:

``` text
/public/**   → Public
/admin/**    → Completely denied
others       → Authentication required
```

------------------------------------------------------------------------

## 14. Key Takeaway

The central concept is:

``` java
.requestMatchers("/admin/**").denyAll()
```

Use `denyAll()` when an endpoint or URL pattern must not be accessible
to any user under the configured authorization policy.

In simple terms:

> **`permitAll()` opens an endpoint, `authenticated()` protects it, and
> `denyAll()` closes it.**

This is particularly useful for maintenance, deprecated APIs,
temporarily unsafe functionality, sensitive resources, and application
features that must remain disabled.
