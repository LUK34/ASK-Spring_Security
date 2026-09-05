# Spring Boot & Spring Security: Filters and Filter Chain

## Introduction

When working with **Spring Boot** and especially **Spring Security**,
you will frequently hear the terms:

-   **Filter**
-   **Filter Chain**

These concepts are important because HTTP requests normally pass through
filters before reaching the controller that handles the request.

> **Important:** Filters and filter chains are **not exclusive to Spring
> Security**. They also exist in normal Spring Boot web applications.
> They are discussed frequently in Spring Security because security
> processing makes extensive use of filters.

A simple way to remember the concepts is:

> **Filter = A component that intercepts a request or response and
> performs some processing.**\
> **Filter Chain = A sequence of filters through which the request and
> response pass.**

------------------------------------------------------------------------

# 1. Typical HTTP Request Flow in a Spring Boot Application

Consider a client sending an HTTP request to a Spring Boot application.

A simplified request flow is:

``` text
Client
   |
   | HTTP Request
   v
Server
   |
   v
Filter Chain
   |
   v
DispatcherServlet
   |
   v
Controller
   |
   v
Service
   |
   v
Repository
   |
   v
Database
```

The response then travels back toward the client.

``` text
Database
   |
   v
Repository
   |
   v
Service
   |
   v
Controller
   |
   v
DispatcherServlet
   |
   v
Filter Chain
   |
   v
Client
```

This means filters can participate in both:

``` text
Incoming Request Processing
```

and:

``` text
Outgoing Response Processing
```

------------------------------------------------------------------------

# 2. What Is the Server?

The client sends the request to a **server** where the Spring Boot
application is running.

Examples mentioned in the lecture include:

``` text
Tomcat
Jetty
```

The application runs on the server, and incoming HTTP requests
eventually need to reach the appropriate application logic.

Before that happens, the request can be intercepted by the **filter
chain**.

------------------------------------------------------------------------

# 3. What Is DispatcherServlet?

After the request passes through the relevant filters, it can reach the:

``` text
DispatcherServlet
```

The `DispatcherServlet` acts as the central dispatcher in Spring MVC.

Its responsibility is to determine which controller should handle the
incoming request.

Conceptually:

``` text
HTTP Request
     |
     v
DispatcherServlet
     |
     | Finds appropriate handler
     v
Controller
```

The controller can then continue the normal application workflow:

``` text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
Database
```

------------------------------------------------------------------------

# 4. What Is a Filter?

A **filter** is a component that can intercept incoming requests and
outgoing responses in a web application.

A filter can perform additional processing before allowing the request
to continue.

Conceptually:

``` text
Request
   |
   v
Filter
   |
   | Perform processing
   v
Next Filter / Resource
```

A filter may also decide **not to pass the request forward**.

Therefore, a filter can conceptually:

``` text
Intercept Request
       |
       v
Perform Processing
       |
       +----------------------+
       |                      |
       v                      v
Continue Request          Reject / Stop
```

The exact behavior depends on the logic implemented inside that
particular filter.

------------------------------------------------------------------------

# 5. Example: Username Validation Filter

The lecture provides a simple example.

Suppose the application does not allow usernames containing a particular
sequence such as:

``` text
ABC
```

or:

``` text
123
```

Imagine a request for creating a new account:

``` text
POST /create-account
```

The filter intercepts the request before it reaches the rest of the
application.

Conceptually:

``` text
Create Account Request
        |
        v
Username Validation Filter
        |
        | Check username
        |
        +-----------------------------+
        |                             |
   Username Valid               Username Invalid
        |                             |
        v                             v
Continue Request                Reject Request
```

For example:

``` text
Username: john
Result: Valid
```

The request can continue.

But:

``` text
Username: john123
Result: Invalid
```

If the filter's rule prohibits `123`, it can reject the request and
prevent it from moving to the next filter or application resource.

This demonstrates an important property of filters:

> **A filter gets an opportunity to process the request and decide
> whether the request should continue.**

------------------------------------------------------------------------

# 6. Filters Can Perform Different Jobs

A filter is not limited to username validation.

Different filters can perform different responsibilities.

Examples discussed in the lecture include:

``` text
Authentication
Authorization
Logging
Input Validation
Compression
CORS Handling
Character Encoding
Request Manipulation
Response Manipulation
URL Rewriting
```

In Spring Security, filters are heavily used for security-related
processing such as:

``` text
Authentication
Authorization
Other security-related tasks
```

------------------------------------------------------------------------

# 7. What Is a Filter Chain?

A **filter chain** is a sequence of filters through which an HTTP
request and response pass.

Instead of having only one filter:

``` text
Request
   |
   v
Filter
   |
   v
Application
```

an application can have multiple filters:

``` text
Request
   |
   v
Filter 1
   |
   v
Filter 2
   |
   v
Filter 3
   |
   v
...
   |
   v
Last Filter
   |
   v
Target Resource
```

All these filters together form the:

``` text
Filter Chain
```

Therefore:

> **Filter = One processing component**

while:

> **Filter Chain = A sequence of filters**

------------------------------------------------------------------------

# 8. How Does the Filter Chain Work?

Suppose the filter chain contains three filters:

``` text
Filter 1
Filter 2
Filter 3
```

When a request arrives:

``` text
Request
   |
   v
Filter 1
   |
   v
Filter 2
   |
   v
Filter 3
   |
   v
Target Resource
```

Each filter performs whatever processing it was designed to perform.

After processing, a filter can pass the request to the next filter in
the chain.

The lecture refers to the following call:

``` java
filterChain.doFilter(request, response);
```

Conceptually, this means:

``` text
"I have finished my processing.
Pass this request and response to the next component in the chain."
```

The process continues until the request reaches the final resource.

------------------------------------------------------------------------

# 9. Understanding `filterChain.doFilter(request, response)`

This is one of the most important ideas to remember when learning
filters.

A filter performs some processing and then calls:

``` java
filterChain.doFilter(request, response);
```

A simplified conceptual example is:

``` java
// Perform processing before continuing

filterChain.doFilter(request, response);

// Perform processing after the downstream processing returns
```

The lecture's key point is that calling `doFilter(...)` allows
processing to continue through the chain.

So conceptually:

``` text
Filter 1
   |
   | doFilter(...)
   v
Filter 2
   |
   | doFilter(...)
   v
Filter 3
   |
   | doFilter(...)
   v
Target Resource
```

If a filter rejects the request according to its logic, it does not need
to continue the request to the next filter.

------------------------------------------------------------------------

# 10. Request and Response Flow Through Filters

Filters are useful not only while the request is moving toward the
target resource.

They can also participate while the response travels back.

Consider:

``` text
                    REQUEST
                      |
                      v
Client -> Filter 1 -> Filter 2 -> Filter 3 -> Resource
                                              |
                                              | Response generated
                                              v
Client <- Filter 1 <- Filter 2 <- Filter 3 <- Resource
                    RESPONSE
```

The resource generates the response.

The response then travels back through the chain, allowing filters to
perform necessary post-processing.

This allows filters to support:

``` text
Pre-processing
```

and:

``` text
Post-processing
```

------------------------------------------------------------------------

# 11. Pre-Processing

**Pre-processing** means doing something before the request reaches the
controller or target resource.

For example:

``` text
Client Request
     |
     v
Logging Filter
     |
     | Log incoming request
     v
Controller
```

A filter could log information about the incoming request before
allowing it to continue.

Other examples can include:

``` text
Input validation
Authentication checks
Request manipulation
Character encoding setup
```

------------------------------------------------------------------------

# 12. Post-Processing

**Post-processing** means performing processing while the response is
returning to the client.

Conceptually:

``` text
Controller
    |
    | Response
    v
Filter
    |
    | Perform post-processing
    v
Client
```

For example, the lecture explains that you may want to:

``` text
Log incoming requests before they reach the controller
```

and also:

``` text
Log responses before they are sent back to the client
```

Filters provide a centralized mechanism for this type of processing.

------------------------------------------------------------------------

# 13. Why Do We Use Filters?

The lecture highlights several reasons filters are useful.

## 13.1 Cross-Cutting Concerns

Filters are useful for functionality that needs to apply across multiple
endpoints.

Examples include:

``` text
Logging
Security
Compression
Input Validation
```

Without filters, you might repeat similar logic inside many controllers.

For example:

``` text
Controller A -> Logging code
Controller B -> Logging code
Controller C -> Logging code
Controller D -> Logging code
```

This creates duplicated logic.

Instead, a filter can centralize it:

``` text
                  +----------------+
Request --------> | Logging Filter |
                  +----------------+
                          |
          +---------------+---------------+
          |               |               |
          v               v               v
     Controller A    Controller B    Controller C
```

This helps avoid duplicating the same cross-cutting logic in every
controller.

------------------------------------------------------------------------

## 13.2 Pre-Processing and Post-Processing

Filters provide a centralized mechanism for processing both:

``` text
Before Controller
```

and:

``` text
After Resource Processing
```

For example:

``` text
Request
   |
   v
Filter: Log Request
   |
   v
Controller
   |
   v
Filter: Log Response
   |
   v
Client
```

This is useful when common behavior should apply consistently across
many requests.

------------------------------------------------------------------------

## 13.3 Request and Response Manipulation

Filters can modify or work with requests and responses.

Examples mentioned in the lecture include:

### Character Encoding

A filter can help configure the character encoding used while processing
requests and responses.

### CORS

Filters can participate in handling:

``` text
Cross-Origin Resource Sharing (CORS)
```

The transcript notes that this concept can be covered separately.

### URL Rewriting

Filters can also be used when URLs need to be rewritten.

Therefore, filters can participate in manipulating how requests and
responses are processed.

------------------------------------------------------------------------

## 13.4 Separation of Concerns

Another important reason for using filters is **separation of
concerns**.

Business logic should generally remain focused on the actual business
requirements.

For example:

``` text
Controller / Service
        |
        v
Business Logic
```

Meanwhile, concerns such as:

``` text
Logging
Security
Data Transformation
Request Processing
```

can be handled separately.

Conceptually:

``` text
Request
   |
   v
Filters
   |
   | Logging
   | Security
   | Validation
   | Transformation
   v
Controller
   |
   v
Business Logic
```

This can make the application cleaner and easier to maintain.

------------------------------------------------------------------------

# 14. Filters in Spring Security

Filters become especially important when working with **Spring
Security**.

Spring Security uses filters for tasks such as:

``` text
Authentication
Authorization
Other security-related processing
```

These filters are arranged in a chain.

Conceptually:

``` text
Incoming Request
       |
       v
+------------------------+
| Spring Security Filter |
+------------------------+
       |
       v
+------------------------+
| Security Filter        |
+------------------------+
       |
       v
+------------------------+
| Another Filter         |
+------------------------+
       |
       v
Application Resource
```

The lecture explains that this filter chain is managed by the
Spring/Spring Security framework.

This is why understanding ordinary filters and filter chains first makes
later Spring Security concepts easier to understand.

------------------------------------------------------------------------

# 15. Filter vs Filter Chain

  -----------------------------------------------------------------------
  Concept                             Meaning
  ----------------------------------- -----------------------------------
  **Filter**                          A component that intercepts
                                      requests/responses and performs
                                      processing

  **Filter Chain**                    A sequence of filters through which
                                      requests and responses pass

  **Pre-processing**                  Processing performed before
                                      reaching the target resource

  **Post-processing**                 Processing performed while the
                                      response travels back

  **`filterChain.doFilter(...)`**     Passes processing to the next
                                      component in the chain
  -----------------------------------------------------------------------

A very simple mental model is:

``` text
Filter
    = One checkpoint

Filter Chain
    = Multiple checkpoints arranged in sequence
```

------------------------------------------------------------------------

# 16. Complete Request Flow

Putting the concepts together:

``` text
                     HTTP REQUEST
                          |
                          v
                       Client
                          |
                          v
                       Server
                          |
                          v
                 +----------------+
                 |  Filter Chain  |
                 +----------------+
                          |
              +-----------+-----------+
              |           |           |
              v           v           v
           Filter 1    Filter 2    Filter 3
                          |
                          v
                  DispatcherServlet
                          |
                          v
                      Controller
                          |
                          v
                       Service
                          |
                          v
                     Repository
                          |
                          v
                       Database
```

The response then travels back:

``` text
Database
   |
   v
Repository
   |
   v
Service
   |
   v
Controller
   |
   v
DispatcherServlet
   |
   v
Filter Chain
   |
   v
Client
```

------------------------------------------------------------------------

# 17. Practical Mental Model

Imagine entering a secure office building.

Before reaching your destination, you pass through several checkpoints:

``` text
Entrance
   |
   v
Checkpoint 1
   |
   v
Checkpoint 2
   |
   v
Checkpoint 3
   |
   v
Office
```

Each checkpoint performs a particular job.

For example:

``` text
Checkpoint 1 -> Check ID
Checkpoint 2 -> Check authorization
Checkpoint 3 -> Record entry
```

Each checkpoint is similar to a:

``` text
Filter
```

The complete sequence of checkpoints is similar to a:

``` text
Filter Chain
```

This is a useful mental model when learning Spring Security.

------------------------------------------------------------------------

# 18. Quick Revision

## What is a Filter?

``` text
A component that intercepts incoming requests
and outgoing responses and performs processing.
```

## What is a Filter Chain?

``` text
A sequence of filters through which the request
and response pass.
```

## How Does a Filter Continue Processing?

``` java
filterChain.doFilter(request, response);
```

## Can a Filter Stop a Request?

Yes.

If its logic determines that the request should not continue, it can
reject or stop processing instead of passing the request to the next
filter.

## Are Filters Only a Spring Security Concept?

No.

The lecture emphasizes that filters also exist in normal Spring Boot web
applications.

They are discussed heavily in Spring Security because Spring Security
makes extensive use of them.

------------------------------------------------------------------------

# 19. Why Filters Matter

The major reasons discussed in the lecture are:

  -----------------------------------------------------------------------
  Reason                              Purpose
  ----------------------------------- -----------------------------------
  **Cross-cutting concerns**          Apply common logic across multiple
                                      endpoints

  **Pre-processing**                  Perform work before the request
                                      reaches the resource

  **Post-processing**                 Perform work while the response
                                      returns

  **Request/response manipulation**   Modify or configure
                                      request/response processing

  **Separation of concerns**          Keep common infrastructure logic
                                      separate from business logic

  **Security**                        Support authentication,
                                      authorization, and other security
                                      processing
  -----------------------------------------------------------------------

------------------------------------------------------------------------

# 20. Key Takeaways

Remember these core ideas:

``` text
1. Filters are not exclusive to Spring Security.

2. A filter intercepts a request or response.

3. A filter performs some processing.

4. A filter can allow the request to continue.

5. A filter can also stop/reject the request.

6. Multiple filters arranged together form a filter chain.

7. filterChain.doFilter(request, response)
   passes processing forward through the chain.

8. Responses travel back through the chain,
   allowing post-processing.

9. Filters are useful for cross-cutting concerns
   such as logging, security, validation, and compression.

10. Spring Security makes extensive use of filters
    for authentication and authorization.
```

------------------------------------------------------------------------

# Conclusion

Filters and filter chains form an important part of request and response
processing in Spring web applications.

The simplest way to remember them is:

> **Filter = Intercept + Process + Continue/Stop**

and:

> **Filter Chain = Sequence of Filters**

The overall idea can be visualized as:

``` text
Client
  |
  v
Filter 1
  |
  v
Filter 2
  |
  v
Filter 3
  |
  v
DispatcherServlet
  |
  v
Controller
  |
  v
Application Logic
```

In Spring Security, these filters become particularly important because
authentication, authorization, and other security-related tasks are
performed through filters arranged in a chain.

Understanding this foundation will make the later Spring Security
filter-related code much easier to follow.
