# Spring Security -- `AuthenticationProvider` Interface and Authentication Flow

## Overview

`AuthenticationProvider` is a key interface in Spring Security's
authentication architecture.

It is responsible for handling supported authentication requests and
attempting to verify the supplied authentication information.

The interface is located in:

``` java
org.springframework.security.authentication.AuthenticationProvider
```

At a high level:

``` text
Authentication Request
        |
        v
AuthenticationManager
        |
        v
AuthenticationProvider
        |
        +---- supports(...)
        |
        +---- authenticate(...)
        |
        v
Authentication Result
```

Understanding this interface is important because it helps explain what
happens internally when Spring Security authenticates a user.

------------------------------------------------------------------------

## 1. `AuthenticationProvider`

The interface has two important methods:

``` java
public interface AuthenticationProvider {

    Authentication authenticate(Authentication authentication)
            throws AuthenticationException;

    boolean supports(Class<?> authentication);
}
```

The two methods have different responsibilities:

  -----------------------------------------------------------------------
  Method                              Responsibility
  ----------------------------------- -----------------------------------
  `supports()`                        Determines whether this provider
                                      supports the supplied
                                      `Authentication` type

  `authenticate()`                    Attempts to authenticate a
                                      supported authentication request
  -----------------------------------------------------------------------

------------------------------------------------------------------------

# 2. The `authenticate()` Method

The method signature is:

``` java
Authentication authenticate(Authentication authentication)
        throws AuthenticationException;
```

It receives an `Authentication` object representing the authentication
request.

For a username/password flow, that object may initially contain
information such as:

``` text
Principal / Username
Credentials / Password
Authentication state
```

A common authentication token for username/password authentication is:

``` java
UsernamePasswordAuthenticationToken
```

Before successful authentication, the token can conceptually look like:

``` text
UsernamePasswordAuthenticationToken
------------------------------------
Principal    : john
Credentials  : ********
Authenticated: false
Authorities  : not yet established
```

The provider then attempts to verify the authentication request.

------------------------------------------------------------------------

## 3. Successful Authentication

If authentication succeeds, the provider returns an authenticated
`Authentication` object.

Conceptually:

``` text
Before authentication

Authentication
    |
    +---- Principal
    +---- Credentials
    +---- authenticated = false

            |
            v

AuthenticationProvider.authenticate(...)

            |
            v

After successful authentication

Authentication
    |
    +---- Authenticated Principal
    +---- Authorities / Roles
    +---- authenticated = true
```

For example:

``` text
Principal:
john

Authorities:
ROLE_USER
READ_NOTES
WRITE_NOTES

Authenticated:
true
```

These authorities can later participate in authorization decisions.

------------------------------------------------------------------------

# 4. Authentication Failure

If authentication fails, an `AuthenticationException` or one of its
subclasses may be thrown.

Examples of authentication failures can include:

``` text
Bad credentials
Disabled account
Locked account
Expired account
Unsupported/failed authentication mechanism
```

Conceptually:

``` text
Authentication Request
        |
        v
AuthenticationProvider
        |
        +---- Valid ------> Authenticated Authentication
        |
        +---- Invalid ----> AuthenticationException
```

The exact exception depends on the provider and authentication
mechanism.

------------------------------------------------------------------------

# 5. The `supports()` Method

The second important method is:

``` java
boolean supports(Class<?> authentication);
```

This method determines whether a particular provider can process the
supplied type of `Authentication`.

Example:

``` java
@Override
public boolean supports(Class<?> authentication) {

    return UsernamePasswordAuthenticationToken.class
            .isAssignableFrom(authentication);
}
```

This effectively says:

> This provider supports authentication requests represented by
> `UsernamePasswordAuthenticationToken` or compatible subclasses.

------------------------------------------------------------------------

# 6. Why Is `supports()` Necessary?

A Spring Security application can contain multiple authentication
providers.

For example:

``` text
ProviderManager
      |
      +---- Username/Password Provider
      |
      +---- LDAP Provider
      |
      +---- Custom Provider
      |
      +---- Pre-Authentication Provider
```

Not every provider understands every authentication token.

Therefore, Spring Security needs a way to determine:

``` text
Can this provider process this Authentication type?
```

That is the purpose of:

``` java
supports(...)
```

Conceptually:

``` text
Authentication Request
        |
        v
Provider A.supports(...)
        |
        +---- false

Provider B.supports(...)
        |
        +---- true
               |
               v
      Provider B.authenticate(...)
```

------------------------------------------------------------------------

# 7. AuthenticationManager

The `AuthenticationManager` coordinates authentication.

The interface is conceptually:

``` java
public interface AuthenticationManager {

    Authentication authenticate(Authentication authentication)
            throws AuthenticationException;
}
```

A commonly used implementation is:

``` java
ProviderManager
```

`ProviderManager` maintains a collection of `AuthenticationProvider`
implementations.

------------------------------------------------------------------------

# 8. ProviderManager and Multiple Providers

Conceptually:

``` text
AuthenticationManager
        |
        v
ProviderManager
        |
        +---- AuthenticationProvider 1
        +---- AuthenticationProvider 2
        +---- AuthenticationProvider 3
```

For a given authentication request, `ProviderManager` considers
providers that support the authentication type and attempts
authentication according to Spring Security's provider-management rules.

A simplified learning model is:

``` text
Authentication
     |
     v
Provider 1
     |
supports()?
     |
    No
     |
     v
Provider 2
     |
supports()?
     |
    Yes
     |
     v
authenticate()
```

A successful provider returns the authenticated result.

------------------------------------------------------------------------

# 9. Complete Username/Password Authentication Flow

A simplified username/password flow is:

``` text
User
 |
 | username + password
 v
Authentication Filter
 |
 | creates Authentication request
 v
AuthenticationManager
 |
 v
ProviderManager
 |
 | checks configured providers
 v
AuthenticationProvider
 |
 +---- supports(authentication type)
 |
 +---- load user information
 |
 +---- verify password
 |
 +---- determine authorities
 |
 v
Authenticated Authentication
 |
 v
SecurityContext
```

This is one of the most useful Spring Security flows to understand.

------------------------------------------------------------------------

# 10. Authentication Filter

Authentication commonly begins with a security filter.

The filter obtains authentication information from the incoming request
and creates an `Authentication` request object.

Conceptually:

``` text
HTTP Request
    |
    v
Authentication Filter
    |
    +---- Extract credentials
    |
    +---- Create Authentication object
    |
    v
AuthenticationManager.authenticate(...)
```

The exact filter depends on the authentication mechanism.

------------------------------------------------------------------------

# 11. Authentication Object Before Authentication

Suppose the user submits:

``` text
Username: john
Password: password123
```

The authentication infrastructure may create something conceptually
equivalent to:

``` java
UsernamePasswordAuthenticationToken token =
        UsernamePasswordAuthenticationToken
                .unauthenticated(
                        "john",
                        "password123"
                );
```

Conceptually:

``` text
Principal     = john
Credentials   = password123
Authenticated = false
```

This token is then submitted to:

``` java
authenticationManager.authenticate(token);
```

------------------------------------------------------------------------

# 12. Provider Selection

The `AuthenticationManager`/`ProviderManager` determines which
configured providers support the token type.

Conceptually:

``` text
UsernamePasswordAuthenticationToken
                |
                v
       ProviderManager
                |
        +-------+-------+
        |               |
        v               v
    Provider A      Provider B
    supports?       supports?
       false           true
                        |
                        v
                   authenticate()
```

This architecture allows multiple authentication mechanisms to coexist.

------------------------------------------------------------------------

# 13. DaoAuthenticationProvider Example

A very common username/password provider is:

``` java
DaoAuthenticationProvider
```

It typically works with:

``` java
UserDetailsService
PasswordEncoder
```

Conceptually:

``` text
UsernamePasswordAuthenticationToken
                |
                v
      DaoAuthenticationProvider
                |
                v
         UserDetailsService
                |
                v
           UserDetails
                |
                v
         PasswordEncoder
                |
                v
         Password Match?
           /        \
         No          Yes
         |            |
         v            v
 Authentication   Authenticated
   Failure        Authentication
```

------------------------------------------------------------------------

# 14. Where Is `supports()` Implemented for DAO Authentication?

When exploring Spring Security source code, you may not always find a
method directly inside the class you first open.

For `DaoAuthenticationProvider`, behavior is inherited through its class
hierarchy.

A useful lesson when reading framework code is:

> If a method is not declared directly in a class, inspect its
> superclass and implemented interfaces.

For username/password authentication, the relevant provider hierarchy
supplies support for `UsernamePasswordAuthenticationToken`.

This is also a good practical debugging skill when navigating Spring
Security source code in IntelliJ IDEA, STS, or Eclipse.

------------------------------------------------------------------------

# 15. Password Verification

In a DAO-based username/password flow, password verification commonly
involves:

``` java
PasswordEncoder
```

Conceptually:

``` text
Submitted Password
       |
       v
PasswordEncoder
       |
       +---- compare against encoded stored password
       |
       v
Match?
 /   \
No   Yes
|     |
v     v
Fail  Continue Authentication
```

Passwords should not be stored as plaintext.

A stored value should normally be an appropriately encoded password
hash.

------------------------------------------------------------------------

# 16. Loading User Details

`DaoAuthenticationProvider` commonly obtains user information through:

``` java
UserDetailsService
```

which exposes:

``` java
UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException;
```

Conceptually:

``` text
DaoAuthenticationProvider
          |
          v
UserDetailsService
          |
          v
Database / User Store
          |
          v
UserDetails
```

`UserDetails` can contain information such as:

``` text
Username
Encoded password
Authorities
Account status
Enabled/disabled status
```

------------------------------------------------------------------------

# 17. Returning the Authentication Result

After successful authentication, the resulting authenticated object
contains information required by Spring Security.

Conceptually:

``` text
Authenticated Authentication
        |
        +---- Principal
        |
        +---- Authorities
        |
        +---- authenticated = true
```

Spring Security can then associate this authentication with the current
security context.

------------------------------------------------------------------------

# 18. SecurityContext

The authenticated identity is normally made available through Spring
Security's:

``` java
SecurityContext
```

Conceptually:

``` text
Successful Authentication
          |
          v
    SecurityContext
          |
          v
SecurityContextHolder
```

Application code and Spring Security authorization infrastructure can
then determine the current authenticated principal.

For example:

``` java
Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();
```

------------------------------------------------------------------------

# 19. Authentication vs Authorization

The authentication provider primarily answers:

> **Who is this user, and can the supplied authentication information be
> trusted?**

After authentication, authorization answers:

> **What is this authenticated user allowed to do?**

Example:

``` text
Authentication
--------------------
User: john
Role: ROLE_USER

        |
        v

Authorization
--------------------
/notes       -> allowed
/profile     -> allowed
/admin       -> denied
```

The authorities returned as part of successful authentication can
therefore influence later authorization decisions.

------------------------------------------------------------------------

# 20. Simplified End-to-End Diagram

``` text
                    HTTP REQUEST
                         |
                         v
                Authentication Filter
                         |
                         | creates
                         v
                   Authentication
                         |
                         v
                AuthenticationManager
                         |
                         v
                   ProviderManager
                         |
              +----------+----------+
              |                     |
              v                     v
      AuthenticationProvider A   AuthenticationProvider B
              |                     |
          supports()?            supports()?
              |                     |
             false                 true
                                    |
                                    v
                              authenticate()
                                    |
                       +------------+------------+
                       |                         |
                    Failure                   Success
                       |                         |
                       v                         v
             AuthenticationException    Authenticated
                                        Authentication
                                              |
                                              v
                                        SecurityContext
                                              |
                                              v
                                        Authorization
```

------------------------------------------------------------------------

# 21. Important Technical Clarification

For learning purposes, the provider process is often described as:

``` text
ProviderManager checks each provider until one authenticates.
```

That is a useful simplified model, but actual provider behavior has
additional rules.

A provider can:

-   Not support the authentication type.
-   Return `null` when it cannot authenticate the request.
-   Return an authenticated `Authentication`.
-   Throw an `AuthenticationException`.

Therefore, do not interpret the flow as Spring blindly calling
`authenticate()` on every provider regardless of `supports()`.

------------------------------------------------------------------------

# 22. Debugging Perspective

Understanding this architecture is valuable when debugging
authentication problems.

If authentication fails, investigate the flow in order:

``` text
1. Did the request reach the expected authentication filter?
2. Was the Authentication object created correctly?
3. Which AuthenticationManager is being used?
4. Which AuthenticationProvider supports the token?
5. Was the user loaded correctly?
6. Was the password verified correctly?
7. Were authorities loaded correctly?
8. Was authentication successful?
9. Was the Authentication stored in the SecurityContext?
10. Is the later failure actually authentication or authorization?
```

This approach is much more effective than treating Spring Security as a
black box.

------------------------------------------------------------------------

# 23. Interview Questions

## What is `AuthenticationProvider`?

`AuthenticationProvider` is a Spring Security interface used by
authentication infrastructure to authenticate supported `Authentication`
types.

------------------------------------------------------------------------

## What are its important methods?

``` java
authenticate(...)
supports(...)
```

`supports()` determines whether the provider understands an
authentication type, while `authenticate()` attempts to authenticate it.

------------------------------------------------------------------------

## What does `authenticate()` return?

When authentication succeeds, it returns an authenticated
`Authentication` object containing the authenticated principal and
typically the granted authorities.

------------------------------------------------------------------------

## What happens when authentication fails?

An appropriate `AuthenticationException` may be thrown.

------------------------------------------------------------------------

## Why is `supports()` needed?

Because an application can have multiple authentication providers, and
each provider may support different `Authentication` implementations.

------------------------------------------------------------------------

## What is `ProviderManager`?

`ProviderManager` is a commonly used `AuthenticationManager`
implementation that delegates authentication to configured
`AuthenticationProvider` instances.

------------------------------------------------------------------------

## What happens after successful authentication?

The authenticated result is made available through Spring Security's
security context infrastructure and can subsequently be used for
authorization.

------------------------------------------------------------------------

# 24. Key Takeaways

Remember this sequence:

``` text
Request
   |
   v
Authentication Filter
   |
   v
Authentication
   |
   v
AuthenticationManager
   |
   v
ProviderManager
   |
   v
AuthenticationProvider
   |
   +---- supports()
   |
   +---- authenticate()
   |
   v
Authenticated Authentication
   |
   v
SecurityContext
   |
   v
Authorization
```

The most important concepts are:

1.  `AuthenticationProvider` is an interface.
2.  It contains `authenticate()` and `supports()`.
3.  `supports()` determines whether the provider understands an
    authentication type.
4.  `authenticate()` performs the authentication attempt.
5.  Successful authentication produces an authenticated `Authentication`
    result.
6.  Authentication failure can produce an `AuthenticationException`.
7.  `ProviderManager` can coordinate multiple authentication providers.
8.  `DaoAuthenticationProvider` commonly handles username/password
    authentication through `UserDetailsService` and `PasswordEncoder`.
9.  The authenticated identity and authorities become available through
    Spring Security's security context.
10. Understanding this flow is extremely useful for debugging real-world
    Spring Security problems.

> **AuthenticationProvider performs the authentication work;
> AuthenticationManager coordinates that work; SecurityContext holds the
> resulting authenticated identity for subsequent security decisions.**
