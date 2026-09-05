# Spring Security -- Permitting Public Endpoints

## Overview

In a Spring Security--enabled application, the usual production approach
is to require authentication for protected application resources while
explicitly allowing selected endpoints to be accessed publicly.

Typical public endpoints include:

-   Login or authentication endpoints
-   Registration/sign-up endpoints
-   Contact pages
-   About pages
-   Forgot-password endpoints
-   Other resources that intentionally do not require authentication

Spring Security allows us to define these exceptions using request
matchers and `permitAll()`.

------------------------------------------------------------------------

## 1. Default Protected Requests

A common Spring Security configuration requires authentication for every
request:

``` java
http
    .authorizeHttpRequests(auth -> auth
        .anyRequest().authenticated()
    );
```

The important part is:

``` java
.anyRequest().authenticated()
```

This means that every request that reaches the application must be
authenticated unless a more specific rule permits it.

> Note: The exact Spring Security configuration syntax varies by Spring
> Security version. The concepts in this document remain the same.

------------------------------------------------------------------------

## 2. Why Do We Need Public Endpoints?

Not every resource in an application should require authentication.

For example:

``` text
/contact
/about
/public/login
/public/signup
/public/forgot-password
```

A login endpoint itself must normally be publicly reachable. Otherwise,
an unauthenticated user would need to authenticate before being allowed
to access the functionality used to authenticate.

The same principle applies to registration and password-recovery
functionality.

------------------------------------------------------------------------

## 3. Permitting a Specific Endpoint

Suppose the application contains a public `/contact` endpoint.

A security rule can explicitly allow access to it:

``` java
http
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/contact").permitAll()
        .anyRequest().authenticated()
    );
```

### What does `requestMatchers()` do?

`requestMatchers()` identifies HTTP requests that should receive a
particular authorization rule.

In this example:

``` java
.requestMatchers("/contact").permitAll()
```

Spring Security is instructed to allow requests matching `/contact`
without requiring authentication.

All other requests continue to fall through to:

``` java
.anyRequest().authenticated()
```

------------------------------------------------------------------------

## 4. Example Controller

A simple controller endpoint could look like this:

``` java
@RestController
public class DemoController {

    @GetMapping("/contact")
    public String contact() {
        return "Contact";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }
}
```

With the security configuration above:

  Endpoint     Authentication Required?
  ------------ --------------------------
  `/contact`   No
  `/hello`     Yes

Therefore, `/contact` can be called without credentials, while `/hello`
remains protected.

------------------------------------------------------------------------

## 5. Grouping Public APIs Under One URL Pattern

Production applications may contain several public APIs, for example:

``` text
/public/login
/public/signup
/public/forgot-password
```

Instead of configuring every endpoint separately, they can be grouped
under a common URL prefix.

For example:

``` java
http
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/public/**").permitAll()
        .anyRequest().authenticated()
    );
```

The pattern:

``` text
/public/**
```

matches resources underneath `/public/`.

Examples include:

``` text
/public/login
/public/signup
/public/forgot-password
/public/contact
```

All such endpoints will be publicly accessible when matched by this
rule.

------------------------------------------------------------------------

## 6. Multiple Public Request Patterns

You can also explicitly configure several public URL patterns:

``` java
http
    .authorizeHttpRequests(auth -> auth
        .requestMatchers(
            "/contact",
            "/about",
            "/public/**"
        ).permitAll()
        .anyRequest().authenticated()
    );
```

This makes `/contact`, `/about`, and resources under `/public/**`
accessible without authentication.

Everything else remains protected.

------------------------------------------------------------------------

## 7. Rule Ordering Is Important

Specific authorization rules should be declared before the general rule:

``` java
.requestMatchers("/public/**").permitAll()
.anyRequest().authenticated()
```

Conceptually:

1.  Check whether the request is a known public request.
2.  If yes, permit it.
3.  Otherwise, require authentication.

This produces a secure default: endpoints are protected unless they are
intentionally declared public.

------------------------------------------------------------------------

## 8. `permitAll()` Does Not Mean Removing Spring Security

It is useful to distinguish between allowing a request and completely
bypassing the Spring Security filter chain.

``` java
.requestMatchers("/public/**").permitAll()
```

means that matching requests are **authorized without requiring an
authenticated user**.

It does not necessarily mean that the request completely bypasses all
Spring Security filters. This distinction can matter when working with
security headers, CORS, CSRF, custom filters, logging, or other
filter-chain behavior.

------------------------------------------------------------------------

## 9. Testing with Postman

To verify a public endpoint:

1.  Start the Spring Boot application.
2.  Open Postman.
3.  Call the public endpoint without an `Authorization` header.
4.  Confirm that the request succeeds.
5.  Call a protected endpoint without authentication.
6.  Confirm that Spring Security rejects the protected request.
7.  Authenticate correctly and verify that the protected endpoint can
    then be accessed.

Example:

``` text
GET /contact
```

Expected:

``` text
Accessible without authentication
```

Protected example:

``` text
GET /hello
```

Expected:

``` text
Authentication required
```

------------------------------------------------------------------------

## 10. Recommended Production Structure

A simple security design might look like:

``` text
/public/**
    /login
    /signup
    /forgot-password

/api/**
    protected application APIs

/admin/**
    protected administrative APIs
```

Security configuration:

``` java
http
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/public/**").permitAll()
        .anyRequest().authenticated()
    );
```

For a real production application, access can be refined further with
roles and authorities.

------------------------------------------------------------------------

## 11. Security Best Practices

Keep the public surface area as small as possible. Do not place an
endpoint under `/public/**` simply for convenience.

Recommended principles:

-   Explicitly identify endpoints that genuinely need anonymous access.
-   Keep sensitive APIs authenticated.
-   Use HTTPS in production.
-   Validate all input even on public endpoints.
-   Apply appropriate rate limiting to abuse-sensitive public endpoints
    such as login and password recovery.
-   Avoid exposing internal information through public error responses.
-   Add role/authority checks where authentication alone is
    insufficient.
-   Review public endpoint patterns whenever new controllers are added.

------------------------------------------------------------------------

## 12. Key Takeaway

The main pattern is:

``` java
http
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/public/**").permitAll()
        .anyRequest().authenticated()
    );
```

In simple terms:

> **Explicitly permit the endpoints that should be public, and require
> authentication for everything else.**

This approach provides a secure default while still allowing users to
reach resources such as login, sign-up, contact, and password-recovery
endpoints without already being authenticated.
