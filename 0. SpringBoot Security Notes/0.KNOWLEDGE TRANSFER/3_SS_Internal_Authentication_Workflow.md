# Spring Security: Internal Authentication Workflow

## Introduction

This document explains the **internal authentication workflow of Spring
Security** using the supplied lecture transcript and the accompanying
Spring Security architecture diagram.

The diagram shows the major components involved when a request enters an
application and Spring Security needs to authenticate a user:

``` text
Request
   |
   v
Filter Chain
   |
   v
Authentication Filter
   |
   v
AuthenticationManager
   |
   v
Authentication Provider
(DaoAuthenticationProvider)
   |
   +----------------------+
   |                      |
   v                      v
UserDetailsService   PasswordEncoder
   |                      |
   v                      |
Database                  |
   |                      |
   +----------+-----------+
              |
              v
     Authenticated User
              |
              v
       Security Context
              |
              v
      DispatcherServlet
              |
              v
 Application Controllers
```

The diagram may initially appear complicated, but each component has a
specific responsibility.

A useful high-level mental model is:

> **Spring Security receives credentials, delegates authentication to
> the appropriate components, verifies the user, creates an
> authenticated `Authentication` object, stores it in the security
> context, and then allows the request to continue into the
> application.**

------------------------------------------------------------------------

# 1. High-Level Authentication Flow

Authentication begins when a request enters the application.

The lecture describes the flow conceptually as:

``` text
1. Request enters application
2. Filter Chain intercepts request
3. Authentication Filter detects authentication/login request
4. Authentication Filter creates an Authentication object
5. AuthenticationManager receives it
6. AuthenticationManager delegates authentication
7. AuthenticationProvider performs authentication
8. UserDetailsService loads stored user details
9. PasswordEncoder verifies the password
10. Authentication succeeds
11. Authentication object becomes fully populated/authenticated
12. Security Context is updated
13. Request proceeds to DispatcherServlet/controllers
```

Each step is discussed below.

------------------------------------------------------------------------

# 2. Request Enters the Application

Everything starts with an HTTP request.

``` text
Client
   |
   | Request
   v
Spring Application
```

For authentication, this may be a login request containing credentials
such as:

``` text
Username
Password
```

The request does not immediately reach the controller.

It first encounters the application's filter chain.

------------------------------------------------------------------------

# 3. Filter Chain

The **Filter Chain** contains multiple filters.

Conceptually:

``` text
Request
   |
   v
+-----------------------+
|     Filter Chain      |
|                       |
|  Filter 1             |
|  Filter 2             |
|  Authentication Filter|
|  Filter N             |
+-----------------------+
   |
   v
Application
```

One of these filters is responsible for authentication-related
processing.

In the supplied diagram, this component is labeled:

``` text
Authentication Filter
```

------------------------------------------------------------------------

# 4. Authentication Filter

The **Authentication Filter** intercepts authentication/login requests.

According to the lecture, when it detects an appropriate login request,
it obtains the submitted credentials.

For example:

``` text
Username: john
Password: ********
```

It then creates an:

``` text
Authentication Object
```

At this stage, however, the object does **not yet mean that the user has
successfully authenticated**.

Conceptually:

``` text
Login Request
     |
     v
Authentication Filter
     |
     | Extract credentials
     v
Authentication Object
     |
     | authenticated = false
     v
AuthenticationManager
```

This distinction is important:

> **Creating an Authentication object does not itself prove that
> authentication succeeded.**

Verification still needs to happen.

------------------------------------------------------------------------

# 5. What Does the Authentication Object Represent?

The authentication object packages authentication-related information.

The lecture discusses information such as:

``` text
Principal
Credentials
Roles / Authorities
Authentication status
```

Before successful authentication, it primarily represents the
authentication attempt.

Conceptually:

``` text
Authentication
├── Principal / username
├── Credentials
└── Not yet authenticated
```

After successful authentication, additional information can be
populated:

``` text
Authentication
├── Principal / user details
├── Credentials-related information
├── Roles / Authorities
└── Authenticated = true
```

------------------------------------------------------------------------

# 6. AuthenticationManager

The Authentication Filter passes the authentication object to the:

``` text
AuthenticationManager
```

The lecture compares this component to a **manager or orchestra
conductor**.

Its responsibility is to coordinate authentication.

It does not necessarily perform the actual username/password
verification itself.

Instead, it delegates authentication to an appropriate:

``` text
AuthenticationProvider
```

Conceptually:

``` text
Authentication Filter
        |
        v
AuthenticationManager
        |
        | Delegate authentication
        v
AuthenticationProvider
```

The lecture also identifies `AuthenticationManager` as an interface and
discusses `ProviderManager` as its implementation used to work with
available authentication providers.

------------------------------------------------------------------------

# 7. Why Are Authentication Providers Needed?

Applications can authenticate users in different ways.

Examples mentioned in the lecture include:

``` text
Database authentication
LDAP authentication
```

Therefore, an application could conceptually have multiple providers:

``` text
AuthenticationManager
        |
        +--------------------------+
        |                          |
        v                          v
Database Provider            LDAP Provider
        |
        v
Another Provider...
```

The manager coordinates which provider can handle the authentication
request.

This separation allows Spring Security authentication to remain modular.

------------------------------------------------------------------------

# 8. AuthenticationProvider

An **AuthenticationProvider** is responsible for performing a particular
type of authentication.

The supplied diagram specifically shows:

``` text
Authentication Provider
[DaoAuthenticationProvider]
```

The lecture explains that the provider is responsible for determining
whether the supplied username and password are correct.

For database-backed username/password authentication, the provider works
with two important collaborators:

``` text
UserDetailsService
PasswordEncoder
```

Conceptually:

``` text
             DaoAuthenticationProvider
                     |
             +-------+-------+
             |               |
             v               v
     UserDetailsService  PasswordEncoder
             |               |
             v               |
          Database           |
```

------------------------------------------------------------------------

# 9. DaoAuthenticationProvider

The diagram uses:

``` text
DaoAuthenticationProvider
```

This is the provider discussed in the lecture for the username/password
authentication flow.

Its job is to coordinate the pieces needed to authenticate the user.

Two important collaborators are:

``` text
UserDetailsService
PasswordEncoder
```

A simple mental model is:

``` text
DaoAuthenticationProvider
        |
        |---- "Load the user"
        |          |
        |          v
        |   UserDetailsService
        |
        |---- "Verify the password"
                   |
                   v
             PasswordEncoder
```

------------------------------------------------------------------------

# 10. UserDetailsService

The **UserDetailsService** is responsible for loading user information.

The reason this is necessary is straightforward.

The user has supplied credentials:

``` text
Username + Password
```

Spring Security needs the corresponding stored user information so that
authentication can be performed.

Conceptually:

``` text
Submitted Username
        |
        v
UserDetailsService
        |
        v
Database
        |
        v
Stored User Details
```

The lecture describes loading information such as:

``` text
Username
Password
Roles
```

from wherever the application stores the user information.

------------------------------------------------------------------------

# 11. Database Lookup

The supplied diagram shows the UserDetailsService interacting with a
database.

It labels the database-side lookup as:

``` text
findByUsername()
```

and the UserDetailsService operation as:

``` text
loadByUsername()
```

Conceptually:

``` text
AuthenticationProvider
        |
        v
UserDetailsService
        |
        | Load user by username
        v
Repository / Database Lookup
        |
        | findByUsername(...)
        v
Database
```

The purpose is to retrieve the stored user information needed for
authentication.

------------------------------------------------------------------------

# 12. Why Must Stored Credentials Be Loaded?

Suppose a user submits:

``` text
Username: john
Password: myPassword
```

The application cannot simply trust these values.

It needs to find the existing user record associated with:

``` text
john
```

and then verify whether the submitted password corresponds to the stored
password representation.

Therefore:

``` text
Submitted Credentials
          +
Stored User Information
          |
          v
Credential Verification
```

This is why `UserDetailsService` is an important part of the
authentication workflow.

------------------------------------------------------------------------

# 13. PasswordEncoder

The other major collaborator shown in the diagram is:

``` text
PasswordEncoder
```

The lecture emphasizes that storing passwords in raw/plain-text form is
not good practice.

For example, this would be unsafe:

``` text
USERNAME      PASSWORD
john          myPassword123
alice         alicePassword
```

Anyone with database access could directly read those passwords.

Instead, passwords should be stored in a non-plain-text encoded form.

The `PasswordEncoder` participates in verifying whether the submitted
password matches the stored encoded password.

The supplied diagram labels this operation:

``` text
matches()
```

------------------------------------------------------------------------

# 14. Password Matching

Conceptually:

``` text
Password entered by user
          |
          |
          v
     PasswordEncoder
          |
          | matches(...)
          |
          v
Stored encoded password
```

The important idea is:

> **Spring Security does not need the database password to be stored as
> readable plain text in order to verify the user's submitted
> password.**

The password encoder handles the comparison.

------------------------------------------------------------------------

# 15. Successful Authentication

If the username and password verification succeeds, the authentication
provider confirms that authentication was successful.

The authentication object can then be populated with additional
authenticated-user information.

For example:

``` text
Authentication
├── Principal / User Details
├── Authorities / Roles
└── Authenticated = true
```

The lecture explains that roles fetched for the user are added to the
authentication information.

For example:

``` text
Username: john
Role: ROLE_ADMIN
Authenticated: true
```

This is different from the initial authentication object created before
verification.

------------------------------------------------------------------------

# 16. Before vs After Authentication

## Before Authentication

``` text
Authentication Object
├── Submitted identity
├── Submitted credentials
└── Authentication not yet confirmed
```

## After Successful Authentication

``` text
Authentication Object
├── Principal / User Details
├── Authentication information
├── Authorities / Roles
└── Authenticated
```

This transition is one of the central ideas in the lecture.

------------------------------------------------------------------------

# 17. Security Context

After successful authentication, the authentication information is
stored in the:

``` text
Security Context
```

The supplied architecture diagram places the Security Context alongside
the authentication/filter workflow.

Conceptually:

``` text
Successful Authentication
          |
          v
Authentication Object
          |
          v
Security Context
```

The lecture describes the Security Context as the place where the
authenticated user's information is made available during the
security/request workflow.

This allows the application and Spring Security infrastructure to know
which user has been authenticated.

------------------------------------------------------------------------

# 18. Security Context and Later Processing

Once the authentication information has been established, later parts of
request processing can access the authenticated user's security
information.

Conceptually:

``` text
Security Context
      |
      +---- Principal
      |
      +---- Authorities
      |
      +---- Authentication information
```

This connects directly with the earlier concepts of **Principal** and
**Authentication**.

A useful relationship is:

``` text
Security Context
      |
      v
Authentication
      |
      +---- Principal
      |
      +---- Authorities
```

------------------------------------------------------------------------

# 19. Request Continues to the Application

After the relevant security processing succeeds, the request can
continue toward:

``` text
DispatcherServlet
```

and then to:

``` text
Application Controllers
```

The supplied diagram explicitly shows:

``` text
Filter Chain
      |
      v
DispatcherServlet / Your Application Controllers
```

Therefore, the overall high-level flow is:

``` text
Request
   |
   v
Spring Security Processing
   |
   | Authentication successful
   v
DispatcherServlet
   |
   v
Controller
   |
   v
Application Logic
```

------------------------------------------------------------------------

# 20. Complete Authentication Workflow

Putting all the components together:

``` text
                         REQUEST
                            |
                            v
                    +---------------+
                    | Filter Chain  |
                    +---------------+
                            |
                            v
                +-----------------------+
                | Authentication Filter |
                +-----------------------+
                            |
                            | Creates authentication
                            | representation
                            v
                +-----------------------+
                | AuthenticationManager |
                +-----------------------+
                            |
                            | authenticate()
                            v
              +-----------------------------+
              | AuthenticationProvider      |
              | DaoAuthenticationProvider   |
              +-----------------------------+
                    |                 |
                    |                 |
                    v                 v
          +------------------+  +-----------------+
          |UserDetailsService|  | PasswordEncoder |
          +------------------+  +-----------------+
                    |                 |
                    |                 | matches()
                    v                 |
                Database             |
                    |                 |
                    +--------+--------+
                             |
                             v
                   Credentials Verified
                             |
                             v
                  Authentication Updated
                             |
                             v
                     Security Context
                             |
                             v
                    DispatcherServlet
                             |
                             v
                 Application Controllers
```

This is the core workflow represented by the supplied lecture diagram.

------------------------------------------------------------------------

# 21. Responsibilities of Each Component

  -----------------------------------------------------------------------
  Component                           Main Responsibility
  ----------------------------------- -----------------------------------
  **Filter Chain**                    Contains the filters through which
                                      the request passes

  **Authentication Filter**           Intercepts authentication requests
                                      and creates the initial
                                      authentication representation

  **AuthenticationManager**           Coordinates/delegates the
                                      authentication process

  **ProviderManager**                 AuthenticationManager
                                      implementation discussed in the
                                      lecture for coordinating providers

  **AuthenticationProvider**          Performs a particular
                                      authentication strategy

  **DaoAuthenticationProvider**       Provider used in the discussed
                                      database-backed username/password
                                      flow

  **UserDetailsService**              Loads stored user details

  **Database**                        Stores application user information

  **PasswordEncoder**                 Supports secure password
                                      encoding/comparison

  **Authentication Object**           Represents authentication-related
                                      information

  **Security Context**                Holds the authenticated user's
                                      authentication information for
                                      security processing

  **DispatcherServlet**               Dispatches the successfully
                                      processed request toward
                                      application controllers
  -----------------------------------------------------------------------

------------------------------------------------------------------------

# 22. AuthenticationManager vs AuthenticationProvider

These two components can initially be confusing.

The easiest distinction is:

## AuthenticationManager

Think:

``` text
Coordinator / Manager
```

It determines how authentication should be delegated.

## AuthenticationProvider

Think:

``` text
Authentication Worker / Strategy
```

It knows how to perform a particular type of authentication.

Conceptually:

``` text
AuthenticationManager
       |
       | "Who can authenticate this?"
       v
AuthenticationProvider
       |
       | "I know how to authenticate this."
       v
Verification
```

------------------------------------------------------------------------

# 23. UserDetailsService vs PasswordEncoder

These also have different responsibilities.

## UserDetailsService

Think:

``` text
Find the stored user
```

Conceptually:

``` text
Username
   |
   v
UserDetailsService
   |
   v
Database
   |
   v
User Details
```

## PasswordEncoder

Think:

``` text
Verify the password
```

Conceptually:

``` text
Submitted Password
        +
Stored Encoded Password
        |
        v
PasswordEncoder.matches(...)
        |
        v
Match / No Match
```

------------------------------------------------------------------------

# 24. Why Does Spring Security Have So Many Components?

The lecture explains this in terms of **modularity**.

Instead of having one giant component responsible for everything:

``` text
Authentication
Database Access
Password Verification
Provider Selection
User Loading
Security State
```

Spring Security separates these responsibilities.

Conceptually:

``` text
Authentication Filter
      |
      v
AuthenticationManager
      |
      v
AuthenticationProvider
      |
      +-------------------+
      |                   |
      v                   v
UserDetailsService   PasswordEncoder
```

Each component has its own job.

This separation makes it easier to:

``` text
Maintain the system
Replace individual components
Support different authentication mechanisms
Extend authentication behavior
Scale the security design
```

------------------------------------------------------------------------

# 25. Example: Database Authentication

Consider a user submitting:

``` text
Username: john
Password: secret123
```

The conceptual workflow is:

``` text
1. Login request arrives.

2. Authentication Filter extracts:
   username = john
   password = secret123

3. An authentication representation is created.

4. AuthenticationManager receives it.

5. AuthenticationManager delegates to
   DaoAuthenticationProvider.

6. DaoAuthenticationProvider asks
   UserDetailsService to load "john".

7. UserDetailsService queries the database.

8. Stored user information is returned.

9. PasswordEncoder compares the submitted
   password with the stored encoded password.

10. If they match, authentication succeeds.

11. User roles/authorities are included in
    the authenticated information.

12. Security Context is updated.

13. The request continues into the application.
```

------------------------------------------------------------------------

# 26. What If Authentication Fails?

The supplied lecture focuses primarily on the successful authentication
path.

Conceptually, however, the important takeaway from the source is that
the request should only reach the normal application processing after
the required authentication verification succeeds.

The lecture does not provide a detailed failure-handler workflow, so
that topic should be treated separately when introduced later.

------------------------------------------------------------------------

# 27. Important Methods Mentioned in the Diagram

The supplied diagram highlights several method names.

## `authenticate()`

Associated with the authentication manager/provider workflow:

``` text
AuthenticationManager
      |
      | authenticate()
      v
AuthenticationProvider
```

## `loadByUsername()`

Shown between the authentication provider and UserDetailsService in the
supplied diagram:

``` text
AuthenticationProvider
      |
      | loadByUsername()
      v
UserDetailsService
```

## `findByUsername()`

Shown for retrieving the user from the database:

``` text
UserDetailsService
      |
      | findByUsername()
      v
Database
```

## `matches()`

Used by the password encoder:

``` text
PasswordEncoder
      |
      | matches()
      v
Password Verification
```

These names are useful to recognize because the lecture indicates that
they will appear again when implementing the authentication code.

------------------------------------------------------------------------

# 28. Easy Analogy

Imagine entering a company building.

### Authentication Filter

``` text
Reception desk
```

It receives your identity information.

### AuthenticationManager

``` text
Security manager
```

It coordinates how your identity should be verified.

### AuthenticationProvider

``` text
Verification department
```

It knows which authentication process should be used.

### UserDetailsService

``` text
Employee records department
```

It retrieves your stored employee record.

### Database

``` text
Employee records storage
```

### PasswordEncoder

``` text
Credential verification mechanism
```

It checks whether your supplied secret matches the securely stored
credential representation.

### Security Context

``` text
Current security record showing that you have been authenticated
```

### Application

After verification:

``` text
You are allowed to continue to the appropriate destination.
```

------------------------------------------------------------------------

# 29. Quick Revision

## Authentication Filter

``` text
Intercepts authentication/login requests
and creates the initial Authentication object.
```

## AuthenticationManager

``` text
Coordinates authentication and delegates it
to an AuthenticationProvider.
```

## AuthenticationProvider

``` text
Performs a particular authentication strategy.
```

## DaoAuthenticationProvider

``` text
Provider shown in the lecture for database-backed
username/password authentication.
```

## UserDetailsService

``` text
Loads stored user information.
```

## PasswordEncoder

``` text
Handles password encoding/comparison.
```

## Security Context

``` text
Stores the authenticated user's Authentication
information for security processing.
```

------------------------------------------------------------------------

# 30. Key Takeaways

Remember this sequence:

``` text
Request
   ↓
Filter Chain
   ↓
Authentication Filter
   ↓
AuthenticationManager
   ↓
AuthenticationProvider
   ↓
UserDetailsService + PasswordEncoder
   ↓
Database/User Verification
   ↓
Authenticated Authentication Object
   ↓
Security Context
   ↓
DispatcherServlet
   ↓
Application Controllers
```

And remember the responsibilities:

``` text
Authentication Filter
    = Capture authentication request

AuthenticationManager
    = Coordinate authentication

AuthenticationProvider
    = Perform authentication strategy

UserDetailsService
    = Load user details

PasswordEncoder
    = Verify password

Security Context
    = Hold authenticated security information
```

------------------------------------------------------------------------

# Conclusion

Spring Security authentication is built from several small components
that work together.

Although the architecture can initially appear complex, the flow becomes
easier when each component is understood separately:

> **The filter captures the authentication attempt, the manager
> coordinates it, the provider performs the authentication,
> UserDetailsService loads the user, PasswordEncoder verifies the
> password, and successful authentication is stored in the Security
> Context before the request continues into the application.**

The architecture is intentionally modular. Each component performs a
specific responsibility, making the security system easier to extend and
maintain as authentication requirements evolve.
